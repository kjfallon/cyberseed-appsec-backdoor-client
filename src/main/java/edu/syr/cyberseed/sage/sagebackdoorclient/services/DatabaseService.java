package edu.syr.cyberseed.sage.sagebackdoorclient.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import edu.syr.cyberseed.sage.sagebackdoorclient.entities.*;
import edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.*;
import edu.syr.cyberseed.sage.sagebackdoorclient.repositories.UserRepository;
import flexjson.JSONSerializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class DatabaseService {

    // create logger
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    @Value("${smirk.backdoor.defaultadmin.username:unknown}")
    private String adminUsername;
    @Value("${smirk.backdoor.defaultadmin.password:unknown}")
    private String adminPassword;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    public String parseCommandline(String... args) {
        String smirkHost = smirkHost = "http://"
                + "127.0.0.1"
                + ":" + "8080";
        String url="";
        String smirkService="";
        String returnedDataFromAPI = "";
        if (args.length > 0) {
            switch (args[0]) {
                case "setITAdmin":
                    System.out.println("Performing setITAdmin.");

                    // check if this user already exists
                    User possibleExistingUser = userRepository.findByUsername(adminUsername);
                    if ((possibleExistingUser == null) || (StringUtils.isEmpty(possibleExistingUser.getUsername()))) {
                        adminPassword = bCryptPasswordEncoder.encode(adminPassword);

                        //Create a json list of roles for a user of this type
                        String roles;
                        ArrayList<String> roleList = new ArrayList<String>();
                        roleList.add("ROLE_USER");
                        roleList.add("ROLE_SYSTEM_ADMIN");
                        Map<String, Object> rolesJson = new HashMap<String, Object>();
                        rolesJson.put("roles", roleList);
                        JSONSerializer serializer = new JSONSerializer();
                        roles = serializer.include("roles").serialize(rolesJson);

                        logger.info("Adding user " + adminUsername + " with roles " + roles);
                        try {
                            // create the User record
                            userRepository.save(Arrays.asList(new User(adminUsername,
                                    adminPassword,
                                    "Default",
                                    "SystemAdmin",
                                    roles,
                                    null,
                                    null)));
                            logger.info("Created System Admin user " + adminUsername);
                        } catch (Exception e) {
                            logger.error("Failure creating System Admin user " + adminUsername);
                            e.printStackTrace();
                        }
                    }

                    break;

                case "loadData":

                    System.out.println("Performing loadData function.");
                    System.out.println("XML file path: " + args[1]);

                    // Read the file
                    String filenameAndPath = args[1];
                    // Create inputstream from spring Resource
                    Resource xmlFileResource = resourceLoader.getResource("file:" + filenameAndPath);
                    InputStream inputStream = null;
                    try {
                        inputStream = xmlFileResource.getInputStream();
                    } catch (IOException e) {
                        logger.error("Unable to load specified file: " + filenameAndPath);
                        System.out.println("Unable to load specified file: " + filenameAndPath);
                        System.out.println("Specifying the full path the file is required");
                        e.printStackTrace();
                        return "";
                    }

                    // Parse the file via inputstream
                    XStream xstream = new XStream();
                    XStream.setupDefaultSecurity(xstream); // to be removed after 1.5
                    xstream.allowTypesByWildcard(new String[] {
                            "edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.**"
                    });
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DBFile.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.SystemAdministratorUserProfile.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DoctorUserProfile.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.NurseUserProfile.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.MedicalAdministratorUserProfile.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.InsuranceAdministratorUserProfile.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.PatientUserProfile.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DoctorExamRecord.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DiagnosisRecord.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.TestResultsRecord.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.InsuranceClaimRecord.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.PatientDoctorCorrespondenceRecord.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.Notes.class);
                    xstream.processAnnotations(Note.class);
                    xstream.processAnnotations(edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.RawRecord.class);
                    DBFile dbFile = null;
                    try {
                        dbFile = (DBFile) xstream.fromXML(inputStream);
                    }
                    catch (Exception e) {
                        logger.error("Unable to parse xml file.");
                        System.out.println("Unable to parse xml file.");
                        e.printStackTrace();
                        return "";
                    }

                    // Extract lists of user objects to store
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.SystemAdministratorUserProfile> sysAdminUserProfiles = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.SystemAdministratorUserProfile>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DoctorUserProfile> doctorUserProfiles = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DoctorUserProfile>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.NurseUserProfile> nurseUserProfiles = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.NurseUserProfile>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.MedicalAdministratorUserProfile> medAdminUserProfiles = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.MedicalAdministratorUserProfile>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.InsuranceAdministratorUserProfile> insAdminUserProfiles = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.InsuranceAdministratorUserProfile>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.PatientUserProfile> patientUserProfiles = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.PatientUserProfile>();
                    if (dbFile.getSysAdminUserProfiles() != null )
                        sysAdminUserProfiles = dbFile.getSysAdminUserProfiles();
                    if (dbFile.getDoctorUserProfiles() != null )
                        doctorUserProfiles = dbFile.getDoctorUserProfiles();
                    if (dbFile.getNurseUserProfiles() != null )
                        nurseUserProfiles = dbFile.getNurseUserProfiles();
                    if (dbFile.getMedAdminUserProfiles() != null )
                        medAdminUserProfiles = dbFile.getMedAdminUserProfiles();
                    if (dbFile.getInsAdminUserProfiles() != null )
                        insAdminUserProfiles = dbFile.getInsAdminUserProfiles();
                    if (dbFile.getPatientUserProfiles() != null )
                        patientUserProfiles = dbFile.getPatientUserProfiles();
                    System.out.println("Importing " + sysAdminUserProfiles.size() + " System Administrators");
                    System.out.println("Importing " + doctorUserProfiles.size() + " Doctors");
                    System.out.println("Importing " + nurseUserProfiles.size() + " Nurses");
                    System.out.println("Importing " + medAdminUserProfiles.size() + " Medical Administrators");
                    System.out.println("Importing " + insAdminUserProfiles.size() + " Insurance Administrators");
                    System.out.println("Importing " + patientUserProfiles.size() + " Patients");

                    // Extract lists of record objects to store
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DoctorExamRecord> doctorExamRecords = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DoctorExamRecord>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DiagnosisRecord> diagnosisRecords = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DiagnosisRecord>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.TestResultsRecord> testResultsRecords = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.TestResultsRecord>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.InsuranceClaimRecord> insuranceClaimRecords = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.InsuranceClaimRecord>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.PatientDoctorCorrespondenceRecord> patientDoctorCorrespondenceRecords = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.PatientDoctorCorrespondenceRecord>();
                    List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.RawRecord> rawRecords = new ArrayList<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.RawRecord>();

                    if (dbFile.getDoctorExamRecords() != null )
                        doctorExamRecords = dbFile.getDoctorExamRecords();
                    if (dbFile.getDiagnosisRecords() != null )
                        diagnosisRecords = dbFile.getDiagnosisRecords();
                    if (dbFile.getTestResultsRecords() != null )
                        testResultsRecords = dbFile.getTestResultsRecords();
                    if (dbFile.getInsuranceClaimRecords() != null )
                        insuranceClaimRecords = dbFile.getInsuranceClaimRecords();
                    if (dbFile.getPatientDoctorCorrespondenceRecords() != null )
                        patientDoctorCorrespondenceRecords = dbFile.getPatientDoctorCorrespondenceRecords();
                    if (dbFile.getRawRecords() != null )
                        rawRecords = dbFile.getRawRecords();
                    System.out.println("Importing " + doctorExamRecords.size() + " Doctor Exam Records");
                    System.out.println("Importing " + diagnosisRecords.size() + " Diagnosis Records");
                    System.out.println("Importing " + testResultsRecords.size() + " Test Result Records");
                    System.out.println("Importing " + insuranceClaimRecords.size() + " Insurance Claim Records");
                    System.out.println("Importing " + patientDoctorCorrespondenceRecords.size() + " Patient Doctor Correspondence Records");
                    System.out.println("Importing " + rawRecords.size() + " Raw Records");

                    // write Users to database

                    break;

                case "getBackupCfg":
                    smirkService = "/getBackupCfg";
                    url = smirkHost + smirkService;

                    // Create HTTP headers that specify the auth for this request and the content type
                    HttpHeaders httpHeaders = new HttpHeaders();
                    String auth = "sysad" + ":" + "1234567891234567";
                    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")) );
                    String authHeader = "Basic " + new String( encodedAuth );
                    httpHeaders.set("Authorization", authHeader);
                    httpHeaders.set("Content-Type", "application/json");

                    // create request with http headers
                    HttpEntity<String> postHeaders = new HttpEntity <String> (httpHeaders);

                    // actually GET the API and get a string array back
                    BackupCfg medRecord = new BackupCfg();
                    try {
                        RestTemplate r1 = new RestTemplate();
                        ResponseEntity<BackupCfg> httpEntityResponse = r1.exchange(url + "/" + args[1],
                                HttpMethod.GET,
                                postHeaders,
                                BackupCfg.class);
                        medRecord = httpEntityResponse.getBody();

                    }

                    catch (HttpClientErrorException e)
                    {
                        System.out.println(e);
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                    String recordSubType = medRecord.getanswer();
                    switch (recordSubType) {
                        case "yes":
                            System.out.println("offsiteServerIp : " + medRecord.getoffsiteServerIp());
                            System.out.println("offsiteServerUsername: " + medRecord.getoffsiteServerUsername());
                            System.out.println("offsiteServerPassword : " + medRecord.getOffsiteServerPassword());
                            break;

                        case "no":
                            System.out.println("No cfg loaded");
                            break;

                        default:
                            System.out.println("default switch ");
                    }

                    break;
                case "loadBackupCfg":
                    System.out.println("loadBackupCfg");
                    smirkService = "/loadBackupCfg";
                    url = smirkHost + smirkService;
                    // Create HTTP headers that specify the auth for this request and the content type
                    HttpHeaders httpHeaders1 = new HttpHeaders();
                    String auth1 = "sysad" + ":" + "1234567891234567";
                    byte[] encodedAuth1 = Base64.encodeBase64(auth1.getBytes(Charset.forName("US-ASCII")));
                    String authHeader1 = "Basic " + new String(encodedAuth1);
                    httpHeaders1.set("Authorization", authHeader1);
                    httpHeaders1.set("Content-Type", "application/json");

                    // Define the data we are submitting to the API
                    ObjectMapper mapper = new ObjectMapper();
                    com.fasterxml.jackson.databind.node.ObjectNode objectNode = mapper.createObjectNode();
                    objectNode.put("offsiteServerIp", "123.123.123.123");
                    objectNode.put("offsiteServerUsername", "thisisnottheusername");
                    objectNode.put("offsiteServerPassword", "thisisnotthepassword");
                    objectNode.put("offsiteServerFilename", args[1]);
                    String postData = objectNode.toString();

                    // create full request with data and http headers
                    HttpEntity<String> postDataWithHeaders = new HttpEntity <String> (postData, httpHeaders1);
                    System.out.println(postDataWithHeaders);
                    try {
                        System.out.println("loadBackupCfg");
                        RestTemplate r = new RestTemplate();
                        r.postForObject(url, postDataWithHeaders, String.class);

                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                    break;
                case "DumpDB":
                    System.out.println("DumpDB");
                    break;
                default:
                    System.out.println("Invalid commandline, options are: <setITAdmin|loadData|getBackupCfg|loadBackupCfg|DumpDB");
            }
        }
        else {
            System.out.println("No commandline parameters specified.");
        }

        return "";
    }
}
