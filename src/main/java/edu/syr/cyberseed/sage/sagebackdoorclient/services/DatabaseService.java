package edu.syr.cyberseed.sage.sagebackdoorclient.services;

import com.thoughtworks.xstream.XStream;
import edu.syr.cyberseed.sage.sagebackdoorclient.entities.*;
import edu.syr.cyberseed.sage.sagebackdoorclient.repositories.UserRepository;
import flexjson.JSONSerializer;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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

                    // Read the file
                    //String filenameAndPath = args[1];
                    String filenameAndPath = "/tmp/file.xml";

                    // Create inputstream from spring Resource
                    Resource xmlFileResource = resourceLoader.getResource("file:" + filenameAndPath);
                    InputStream inputStream = null;
                    try {
                        inputStream = xmlFileResource.getInputStream();
                    } catch (IOException e) {
                        logger.error("Unable to load specified file: " + filenameAndPath);
                        System.out.println("Unable to load specified file: " + filenameAndPath);
                        e.printStackTrace();
                        return "";
                    }

                    // Parse the file via inputstream
                    XStream xstream = new XStream();
                    XStream.setupDefaultSecurity(xstream); // to be removed after 1.5
                    xstream.allowTypesByWildcard(new String[] {
                            "edu.syr.cyberseed.sage.sagebackdoorclient.entities.**"
                    });
                    xstream.processAnnotations(DBFile.class);
                    xstream.processAnnotations(SystemAdministratorUserProfile.class);
                    xstream.processAnnotations(DoctorUserProfile.class);
                    xstream.processAnnotations(NurseUserProfile.class);
                    xstream.processAnnotations(MedicalAdministratorUserProfile.class);
                    xstream.processAnnotations(InsuranceAdministratorUserProfile.class);
                    xstream.processAnnotations(PatientUserProfile.class);
                    xstream.processAnnotations(DoctorExamRecord.class);
                    xstream.processAnnotations(DiagnosisRecord.class);
                    xstream.processAnnotations(TestResultsRecord.class);
                    xstream.processAnnotations(InsuranceClaimRecord.class);
                    xstream.processAnnotations(PatientDoctorCorrespondenceRecord.class);
                    xstream.processAnnotations(Notes.class);
                    xstream.processAnnotations(Note.class);
                    xstream.processAnnotations(RawRecord.class);
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
                    List<SystemAdministratorUserProfile> sysAdminUserProfiles = new ArrayList<SystemAdministratorUserProfile>();
                    List<DoctorUserProfile> doctorUserProfiles = new ArrayList<DoctorUserProfile>();
                    List<NurseUserProfile> nurseUserProfiles = new ArrayList<NurseUserProfile>();
                    List<MedicalAdministratorUserProfile> medAdminUserProfiles = new ArrayList<MedicalAdministratorUserProfile>();
                    List<InsuranceAdministratorUserProfile> insAdminUserProfiles = new ArrayList<InsuranceAdministratorUserProfile>();
                    List<PatientUserProfile> patientUserProfiles = new ArrayList<PatientUserProfile>();
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
                    List<DoctorExamRecord> doctorExamRecords = new ArrayList<DoctorExamRecord>();
                    List<DiagnosisRecord> diagnosisRecords = new ArrayList<DiagnosisRecord>();
                    List<TestResultsRecord> testResultsRecords = new ArrayList<TestResultsRecord>();
                    List<InsuranceClaimRecord> insuranceClaimRecords = new ArrayList<InsuranceClaimRecord>();
                    List<PatientDoctorCorrespondenceRecord> patientDoctorCorrespondenceRecords = new ArrayList<PatientDoctorCorrespondenceRecord>();
                    List<RawRecord> rawRecords = new ArrayList<RawRecord>();

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

                    break;

                case "getBackupCfg":
                    System.out.println("getBackupCfg");
                    break;
                case "loadBackupCfg":
                    System.out.println("loadBackupCfg");
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
