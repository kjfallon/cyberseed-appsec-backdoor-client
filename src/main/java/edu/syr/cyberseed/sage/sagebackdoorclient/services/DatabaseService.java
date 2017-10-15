package edu.syr.cyberseed.sage.sagebackdoorclient.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import edu.syr.cyberseed.sage.sagebackdoorclient.entities.*;
import edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.*;
import edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DoctorExamRecord;
import edu.syr.cyberseed.sage.sagebackdoorclient.repositories.*;
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

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Value("${smirk.application.server.url:unknown}")
    private String smirkUrl;
    @Autowired
    MedicalRecordRepository medicalRecordRepository;
    @Autowired
    MedicalRecordWithoutAutoIdRepository medicalRecordWithoutAutoIdRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    NurseRepository nurseRepository;
    @Autowired
    MedicalAdminRepository medicalAdminRepository;
    @Autowired
    InsuranceAdminRepository insAdminRepository;
    @Autowired
    PermissionsRepository permissionListRepository;
    @Autowired
    DoctorExamRecordRepository doctorExamRecordRepository;
    @Autowired
    TestResultRecordRepository testResultRecordRepository;
    @Autowired
    DiagnosisRecordRepository diagnosisRecordRepository;
    @Autowired
    InsuranceClaimRecordRepository insuranceClaimRecordRepository;
    @Autowired
    CorrespondenceRecordRepository correspondenceRecordRepository;
    @Autowired
    RawRecordRepository rawRecordRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    public String parseCommandline(String... args) throws FileNotFoundException, UnsupportedEncodingException {
        String url="";
        String smirkService="";
        String returnedDataFromAPI = "";
        String b="";
        String abc= "loadBackupCfg db_backup_2017.cfg";
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
                    XStream xstream = new XStream() {
                        @Override
                        protected MapperWrapper wrapMapper(MapperWrapper next) {
                            return new MapperWrapper(next) {
                                @Override
                                public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                                    if (definedIn == Object.class) {
                                        System.out.println("Error unable to load <" + fieldName + ">value</" + fieldName + ">");
                                        return false;
                                    } else {
                                        return super.shouldSerializeMember(definedIn, fieldName);
                                    }
                                }
                            };
                        }
                    };

                    XStream.setupDefaultSecurity(xstream);
                    xstream.allowTypesByWildcard(new String[]{
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
                    } catch (Exception e) {
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
                    if (dbFile.getSysAdminUserProfiles() != null)
                        sysAdminUserProfiles = dbFile.getSysAdminUserProfiles();
                    if (dbFile.getDoctorUserProfiles() != null)
                        doctorUserProfiles = dbFile.getDoctorUserProfiles();
                    if (dbFile.getNurseUserProfiles() != null)
                        nurseUserProfiles = dbFile.getNurseUserProfiles();
                    if (dbFile.getMedAdminUserProfiles() != null)
                        medAdminUserProfiles = dbFile.getMedAdminUserProfiles();
                    if (dbFile.getInsAdminUserProfiles() != null)
                        insAdminUserProfiles = dbFile.getInsAdminUserProfiles();
                    if (dbFile.getPatientUserProfiles() != null)
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

                    if (dbFile.getDoctorExamRecords() != null)
                        doctorExamRecords = dbFile.getDoctorExamRecords();
                    if (dbFile.getDiagnosisRecords() != null)
                        diagnosisRecords = dbFile.getDiagnosisRecords();
                    if (dbFile.getTestResultsRecords() != null)
                        testResultsRecords = dbFile.getTestResultsRecords();
                    if (dbFile.getInsuranceClaimRecords() != null)
                        insuranceClaimRecords = dbFile.getInsuranceClaimRecords();
                    if (dbFile.getPatientDoctorCorrespondenceRecords() != null)
                        patientDoctorCorrespondenceRecords = dbFile.getPatientDoctorCorrespondenceRecords();
                    if (dbFile.getRawRecords() != null)
                        rawRecords = dbFile.getRawRecords();
                    System.out.println("Importing " + doctorExamRecords.size() + " Doctor Exam Records");
                    System.out.println("Importing " + diagnosisRecords.size() + " Diagnosis Records");
                    System.out.println("Importing " + testResultsRecords.size() + " Test Result Records");
                    System.out.println("Importing " + insuranceClaimRecords.size() + " Insurance Claim Records");
                    System.out.println("Importing " + patientDoctorCorrespondenceRecords.size() + " Patient Doctor Correspondence Records");
                    System.out.println("Importing " + rawRecords.size() + " Raw Records");

                    //
                    //
                    // write Sys Admins to database
                    //
                    //

                    for (SystemAdministratorUserProfile user : sysAdminUserProfiles) {

                        // no password specified so setting to random type 4 uuid.
                        String password = bCryptPasswordEncoder.encode(UUID.randomUUID().toString());

                        ArrayList<String> applicationRoleList = parseXmlRoles(user.getRoles());

                        //Create a json list of roles for a user of this type
                        String roles;
                        ArrayList<String> roleList = new ArrayList<String>();
                        // roles for this user type
                        roleList.add("ROLE_USER");
                        roleList.add("ROLE_SYSTEM_ADMIN");
                        // extra roles from xml
                        for (String role : applicationRoleList) {
                            if (StringUtils.isNotEmpty(role)) {
                                roleList.add(role);
                            }
                        }
                        Map<String, Object> rolesJson = new HashMap<String, Object>();
                        rolesJson.put("roles", roleList);
                        JSONSerializer serializer = new JSONSerializer();
                        roles = serializer.include("roles").serialize(rolesJson);

                        //parse and permissions to add
                        List<String> userSuppliedIncludeList = parseXmlPermissions(user.getPermissions());
                        ArrayList<String> includeList = new ArrayList<String>();
                        for (String perm : userSuppliedIncludeList) {
                            Permissions permsObject = permissionListRepository.findByPermission(perm);
                            if ((permsObject != null) && (StringUtils.isNotEmpty(permsObject.getPermission()))) {
                                //logger.info("Permission " + permsObject.getPermission() + " was requested to be added to " + user.getUsername());
                                includeList.add(permsObject.getPermission());
                            }
                        }
                        String includeRolesJson = null;
                        if (includeList.size() > 0) {
                            Map<String, Object> irolesJson = new HashMap<String, Object>();
                            irolesJson.put("roles", includeList);
                            JSONSerializer permserializer = new JSONSerializer();
                            includeRolesJson = permserializer.include("roles").serialize(irolesJson);
                        }
                        userRepository.save(Arrays.asList(new User(user.getUsername(),
                                password,
                                user.getFirstName(),
                                user.getLastName(),
                                roles,
                                includeRolesJson,
                                null)));
                        System.out.println("Added System Admin: " + user.getUsername());
                    }

                    //
                    //
                    // write Doctors to database
                    //
                    //

                    for (DoctorUserProfile user : doctorUserProfiles) {

                        // no password specified so setting to random type 4 uuid.
                        String password = bCryptPasswordEncoder.encode(UUID.randomUUID().toString());

                        ArrayList<String> applicationRoleList = parseXmlRoles(user.getRoles());

                        //Create a json list of roles for a user of this type
                        String roles;
                        ArrayList<String> roleList = new ArrayList<String>();
                        // roles for this user type
                        roleList.add("ROLE_USER");
                        roleList.add("ROLE_DOCTOR");
                        // extra roles from xml
                        for (String role : applicationRoleList) {
                            if (StringUtils.isNotEmpty(role)) {
                                roleList.add(role);
                            }
                        }
                        Map<String, Object> rolesJson = new HashMap<String, Object>();
                        rolesJson.put("roles", roleList);
                        JSONSerializer serializer = new JSONSerializer();
                        roles = serializer.include("roles").serialize(rolesJson);

                        //parse and permissions to add
                        List<String> userSuppliedIncludeList = parseXmlPermissions(user.getPermissions());
                        ArrayList<String> includeList = new ArrayList<String>();
                        for (String perm : userSuppliedIncludeList) {
                            Permissions permsObject = permissionListRepository.findByPermission(perm);
                            if ((permsObject != null) && (StringUtils.isNotEmpty(permsObject.getPermission()))) {
                                logger.info("Permission " + permsObject.getPermission() + " was requested to be added to " + user.getUsername());
                                includeList.add(permsObject.getPermission());
                            }
                        }
                        String includeRolesJson = null;
                        if (includeList.size() > 0) {
                            Map<String, Object> irolesJson = new HashMap<String, Object>();
                            irolesJson.put("roles", includeList);
                            JSONSerializer permserializer = new JSONSerializer();
                            includeRolesJson = permserializer.include("roles").serialize(irolesJson);
                        }
                        userRepository.save(Arrays.asList(new User(user.getUsername(),
                                password,
                                user.getFirstName(),
                                user.getLastName(),
                                roles,
                                includeRolesJson,
                                null)));
                        // create the Doctor record
                        doctorRepository.save(new Doctor(user.getUsername(), user.getPracticeName()
                                , user.getPracticeAddress(), user.getRecoveryPhrase()));

                        System.out.println("Added Doctor: " + user.getUsername());
                    }

                    //
                    //
                    // write Nurses to database
                    //
                    //

                    for (NurseUserProfile user : nurseUserProfiles) {

                        // no password specified so setting to random type 4 uuid.
                        String password = bCryptPasswordEncoder.encode(UUID.randomUUID().toString());

                        ArrayList<String> applicationRoleList = parseXmlRoles(user.getRoles());

                        //Create a json list of roles for a user of this type
                        String roles;
                        ArrayList<String> roleList = new ArrayList<String>();
                        // roles for this user type
                        roleList.add("ROLE_USER");
                        roleList.add("ROLE_NURSE");
                        // extra roles from xml
                        for (String role : applicationRoleList) {
                            if (StringUtils.isNotEmpty(role)) {
                                roleList.add(role);
                            }
                        }
                        Map<String, Object> rolesJson = new HashMap<String, Object>();
                        rolesJson.put("roles", roleList);
                        JSONSerializer serializer = new JSONSerializer();
                        roles = serializer.include("roles").serialize(rolesJson);

                        //parse and permissions to add
                        List<String> userSuppliedIncludeList = parseXmlPermissions(user.getPermissions());
                        ArrayList<String> includeList = new ArrayList<String>();
                        for (String perm : userSuppliedIncludeList) {
                            Permissions permsObject = permissionListRepository.findByPermission(perm);
                            if ((permsObject != null) && (StringUtils.isNotEmpty(permsObject.getPermission()))) {
                                logger.info("Permission " + permsObject.getPermission() + " was requested to be added to " + user.getUsername());
                                includeList.add(permsObject.getPermission());
                            }
                        }
                        String includeRolesJson = null;
                        if (includeList.size() > 0) {
                            Map<String, Object> irolesJson = new HashMap<String, Object>();
                            irolesJson.put("roles", includeList);
                            JSONSerializer permserializer = new JSONSerializer();
                            includeRolesJson = permserializer.include("roles").serialize(irolesJson);
                        }
                        userRepository.save(Arrays.asList(new User(user.getUsername(),
                                password,
                                user.getFirstName(),
                                user.getLastName(),
                                roles,
                                includeRolesJson,
                                null)));
                        // create the Nurse record
                        nurseRepository.save(new Nurse(user.getUsername(), user.getPracticeName()
                                , user.getPracticeAddress(), user.getAssociatedDoctors()));

                        System.out.println("Added Nurse: " + user.getUsername());
                    }

                    //
                    //
                    // write Med Admin to database
                    //
                    //

                    for (MedicalAdministratorUserProfile user : medAdminUserProfiles) {

                        // no password specified so setting to random type 4 uuid.
                        String password = bCryptPasswordEncoder.encode(UUID.randomUUID().toString());

                        ArrayList<String> applicationRoleList = parseXmlRoles(user.getRoles());

                        //Create a json list of roles for a user of this type
                        String roles;
                        ArrayList<String> roleList = new ArrayList<String>();
                        // roles for this user type
                        roleList.add("ROLE_USER");
                        roleList.add("ROLE_MEDICAL_ADMIN");
                        // extra roles from xml
                        for (String role : applicationRoleList) {
                            if (StringUtils.isNotEmpty(role)) {
                                roleList.add(role);
                            }
                        }
                        Map<String, Object> rolesJson = new HashMap<String, Object>();
                        rolesJson.put("roles", roleList);
                        JSONSerializer serializer = new JSONSerializer();
                        roles = serializer.include("roles").serialize(rolesJson);

                        //parse and permissions to add
                        List<String> userSuppliedIncludeList = parseXmlPermissions(user.getPermissions());
                        ArrayList<String> includeList = new ArrayList<String>();
                        for (String perm : userSuppliedIncludeList) {
                            Permissions permsObject = permissionListRepository.findByPermission(perm);
                            if ((permsObject != null) && (StringUtils.isNotEmpty(permsObject.getPermission()))) {
                                logger.info("Permission " + permsObject.getPermission() + " was requested to be added to " + user.getUsername());
                                includeList.add(permsObject.getPermission());
                            }
                        }
                        String includeRolesJson = null;
                        if (includeList.size() > 0) {
                            Map<String, Object> irolesJson = new HashMap<String, Object>();
                            irolesJson.put("roles", includeList);
                            JSONSerializer permserializer = new JSONSerializer();
                            includeRolesJson = permserializer.include("roles").serialize(irolesJson);
                        }
                        userRepository.save(Arrays.asList(new User(user.getUsername(),
                                password,
                                user.getFirstName(),
                                user.getLastName(),
                                roles,
                                includeRolesJson,
                                null)));
                        // create the Med Admin record
                        medicalAdminRepository.save(Arrays.asList(new Medical_admin(user.getUsername(),
                                user.getPracticeName(), user.getPracticeAddress(), user.getAssociatedDoctors(),
                                user.getAssociatedNurses())));


                        System.out.println("Added Medical Admin: " + user.getUsername());
                    }

                    //
                    //
                    // write Insurance Admin to database
                    //
                    //

                    for (InsuranceAdministratorUserProfile user : insAdminUserProfiles) {

                        // no password specified so setting to random type 4 uuid.
                        String password = bCryptPasswordEncoder.encode(UUID.randomUUID().toString());

                        ArrayList<String> applicationRoleList = parseXmlRoles(user.getRoles());

                        //Create a json list of roles for a user of this type
                        String roles;
                        ArrayList<String> roleList = new ArrayList<String>();
                        // roles for this user type
                        roleList.add("ROLE_USER");
                        roleList.add("ROLE_INSURANCE_ADMIN");
                        // extra roles from xml
                        for (String role : applicationRoleList) {
                            if (StringUtils.isNotEmpty(role)) {
                                roleList.add(role);
                            }
                        }
                        Map<String, Object> rolesJson = new HashMap<String, Object>();
                        rolesJson.put("roles", roleList);
                        JSONSerializer serializer = new JSONSerializer();
                        roles = serializer.include("roles").serialize(rolesJson);

                        //parse and permissions to add
                        List<String> userSuppliedIncludeList = parseXmlPermissions(user.getPermissions());
                        ArrayList<String> includeList = new ArrayList<String>();
                        for (String perm : userSuppliedIncludeList) {
                            Permissions permsObject = permissionListRepository.findByPermission(perm);
                            if ((permsObject != null) && (StringUtils.isNotEmpty(permsObject.getPermission()))) {
                                logger.info("Permission " + permsObject.getPermission() + " was requested to be added to " + user.getUsername());
                                includeList.add(permsObject.getPermission());
                            }
                        }
                        String includeRolesJson = null;
                        if (includeList.size() > 0) {
                            Map<String, Object> irolesJson = new HashMap<String, Object>();
                            irolesJson.put("roles", includeList);
                            JSONSerializer permserializer = new JSONSerializer();
                            includeRolesJson = permserializer.include("roles").serialize(irolesJson);
                        }
                        userRepository.save(Arrays.asList(new User(user.getUsername(),
                                password,
                                user.getFirstName(),
                                user.getLastName(),
                                roles,
                                includeRolesJson,
                                null)));
                        // create the Ins Admin record
                        insAdminRepository.save(Arrays.asList(new Insurance_admin(user.getUsername(),
                                user.getCompanyName(), user.getCompanyAddress())));


                        System.out.println("Added Insurance Admin: " + user.getUsername());
                    }

                    //
                    //
                    // write Patient to database
                    //
                    //

                    for (PatientUserProfile user : patientUserProfiles) {

                        // no password specified so setting to random type 4 uuid.
                        String password = bCryptPasswordEncoder.encode(UUID.randomUUID().toString());

                        ArrayList<String> applicationRoleList = parseXmlRoles(user.getRoles());

                        //Create a json list of roles for a user of this type
                        String roles;
                        ArrayList<String> roleList = new ArrayList<String>();
                        // roles for this user type
                        roleList.add("ROLE_USER");
                        roleList.add("ROLE_PATIENT");
                        // extra roles from xml
                        for (String role : applicationRoleList) {
                            if (StringUtils.isNotEmpty(role)) {
                                roleList.add(role);
                            }
                        }
                        Map<String, Object> rolesJson = new HashMap<String, Object>();
                        rolesJson.put("roles", roleList);
                        JSONSerializer serializer = new JSONSerializer();
                        roles = serializer.include("roles").serialize(rolesJson);

                        //parse and permissions to add
                        List<String> userSuppliedIncludeList = parseXmlPermissions(user.getPermissions());
                        ArrayList<String> includeList = new ArrayList<String>();
                        for (String perm : userSuppliedIncludeList) {
                            Permissions permsObject = permissionListRepository.findByPermission(perm);
                            if ((permsObject != null) && (StringUtils.isNotEmpty(permsObject.getPermission()))) {
                                logger.info("Permission " + permsObject.getPermission() + " was requested to be added to " + user.getUsername());
                                includeList.add(permsObject.getPermission());
                            }
                        }
                        String includeRolesJson = null;
                        if (includeList.size() > 0) {
                            Map<String, Object> irolesJson = new HashMap<String, Object>();
                            irolesJson.put("roles", includeList);
                            JSONSerializer permserializer = new JSONSerializer();
                            includeRolesJson = permserializer.include("roles").serialize(irolesJson);
                        }
                        userRepository.save(Arrays.asList(new User(user.getUsername(),
                                password,
                                user.getFirstName(),
                                user.getLastName(),
                                roles,
                                includeRolesJson,
                                null)));
                        // create the patientrecord
                        String ssn2 = user.getSsn().replace("-", "");
                        Integer ssnInt = Integer.valueOf(ssn2);
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        Date dobDate= new Date();
                        try {
                            dobDate = df.parse(user.getDob());
                        } catch (ParseException e) {
                            System.out.println("DOB for " + user.getUsername() + " not in MM/DD/YYYY format");
                        }
                        patientRepository.save(Arrays.asList(new Patient(user.getUsername(),
                                dobDate, ssnInt, user.getAddress())));


                        System.out.println("Added Patient: " + user.getUsername());
                    }

                    //
                    //
                    // write DoctorExamRecord to database
                    //
                    //

                    for (edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DoctorExamRecord record : doctorExamRecords) {
                        // change id from string to Integer
                        Integer id = Integer.valueOf(record.getRecordId());

                        // change dates from string to date
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        Date recordDate= new Date();
                        try {
                            recordDate = df.parse(record.getRecordDate());
                        } catch (ParseException e) {
                            System.out.println("RecordDate for " + id + " not in MM/DD/YYYY format");
                        }
                        Date examDate= new Date();
                        try {
                            examDate = df.parse(record.getDate());
                        } catch (ParseException e) {
                            System.out.println("ExamDate for " + id + " not in MM/DD/YYYY format");
                        }

                        //Check if API user specified supplemental users for edit or view permission
                        List<String> xmlEditList = Arrays.asList(record.getEditPermissions().split(","));
                        List<String> xmlViewList = Arrays.asList(record.getViewPermissions().split(","));
                        Boolean editUsersSubmitted = ((xmlEditList != null) && (xmlEditList.size() > 0)) ? true : false;
                        Boolean viewUsersSubmitted = ((xmlViewList != null) && (xmlViewList.size() > 0)) ? true : false;

                        // create a json object of the default edit users
                        ArrayList<String> editUserList = new ArrayList<String>();
                        // by default do not add any users
                        //editUserList.add(currentUser);
                        Map<String, Object> editUserListJson = new HashMap<String, Object>();


                        // create a json object of the default view users
                        ArrayList<String> viewUserList = new ArrayList<String>();
                        // by default do not add any users
                        //viewUserList.add(currentUser);
                        //viewUserList.add(submittedData.getPatientUsername());
                        Map<String, Object> viewUserListJson = new HashMap<String, Object>();


                        String finalEditPermissions = "";
                        String finalViewPermissions = "";

                        if (editUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantEdit = xmlEditList;
                            for (String username : userSuppliedListOfUsersToGrantEdit) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    editUserList.add(username);
                                }
                            }
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }
                        else {
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }

                        if (viewUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantView = xmlViewList;
                            for (String username : userSuppliedListOfUsersToGrantView) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    viewUserList.add(username);
                                }
                            }
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }
                        else {
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }

                        //logger.info("Creating records with id " + id);
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId savedMedicalRecord =
                                medicalRecordWithoutAutoIdRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId(id,
                                        "Doctor Exam Record",
                                        recordDate,
                                        record.getOwner(),
                                        record.getPatient(),
                                        finalEditPermissions,
                                        finalViewPermissions));
                        //logger.info("Created  MedicalRecord with id " + savedMedicalRecord.getId());

                        // create the Doctor exam record
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.DoctorExamRecord savedDoctorExamRecord =
                                doctorExamRecordRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.DoctorExamRecord(id,
                                        record.getDoctor(),
                                        examDate,
                                        record.getNotes()));
                        System.out.println("Created DoctorExamRecord with id " + savedDoctorExamRecord.getId());

                    }

                    //
                    //
                    // write DiagnosisRecord to database
                    //
                    //

                    for (edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.DiagnosisRecord record : diagnosisRecords) {
                        // change id from string to Integer
                        Integer id = Integer.valueOf(record.getRecordId());

                        // change dates from string to date
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        Date recordDate= new Date();
                        try {
                            recordDate = df.parse(record.getRecordDate());
                        } catch (ParseException e) {
                            System.out.println("RecordDate for " + id + " not in MM/DD/YYYY format");
                        }
                        Date examDate= new Date();
                        try {
                            examDate = df.parse(record.getDate());
                        } catch (ParseException e) {
                            System.out.println("ExamDate for " + id + " not in MM/DD/YYYY format");
                        }

                        //Check if API user specified supplemental users for edit or view permission
                        List<String> xmlEditList = Arrays.asList(record.getEditPermissions().split(","));
                        List<String> xmlViewList = Arrays.asList(record.getViewPermissions().split(","));
                        Boolean editUsersSubmitted = ((xmlEditList != null) && (xmlEditList.size() > 0)) ? true : false;
                        Boolean viewUsersSubmitted = ((xmlViewList != null) && (xmlViewList.size() > 0)) ? true : false;

                        // create a json object of the default edit users
                        ArrayList<String> editUserList = new ArrayList<String>();
                        // by default do not add any users
                        //editUserList.add(currentUser);
                        Map<String, Object> editUserListJson = new HashMap<String, Object>();


                        // create a json object of the default view users
                        ArrayList<String> viewUserList = new ArrayList<String>();
                        // by default do not add any users
                        //viewUserList.add(currentUser);
                        //viewUserList.add(submittedData.getPatientUsername());
                        Map<String, Object> viewUserListJson = new HashMap<String, Object>();


                        String finalEditPermissions = "";
                        String finalViewPermissions = "";

                        if (editUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantEdit = xmlEditList;
                            for (String username : userSuppliedListOfUsersToGrantEdit) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    editUserList.add(username);
                                }
                            }
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }
                        else {
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }

                        if (viewUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantView = xmlViewList;
                            for (String username : userSuppliedListOfUsersToGrantView) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    viewUserList.add(username);
                                }
                            }
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }
                        else {
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }

                        //logger.info("Creating records with id " + id);
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId savedMedicalRecord =
                                medicalRecordWithoutAutoIdRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId(id,
                                        "Diagnosis Record",
                                        recordDate,
                                        record.getOwner(),
                                        record.getPatient(),
                                        finalEditPermissions,
                                        finalViewPermissions));
                        //logger.info("Created  MedicalRecord with id " + savedMedicalRecord.getId());

                        // create the Doctor exam record
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.DiagnosisRecord savedDiagnosisRecord =
                                diagnosisRecordRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.DiagnosisRecord(id,
                                        record.getDoctor(),
                                        examDate,
                                        record.getDiagnosis()));
                        System.out.println("Created DiagnosisRecord with id " + savedDiagnosisRecord.getId());

                    }

                    //
                    //
                    // write TestResultsRecords to database
                    //
                    //

                    for (edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.TestResultsRecord record : testResultsRecords) {
                        // change id from string to Integer
                        Integer id = Integer.valueOf(record.getRecordId());

                        // change dates from string to date
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        Date recordDate= new Date();
                        try {
                            recordDate = df.parse(record.getRecordDate());
                        } catch (ParseException e) {
                            System.out.println("RecordDate for " + id + " not in MM/DD/YYYY format");
                        }
                        Date examDate= new Date();
                        try {
                            examDate = df.parse(record.getDate());
                        } catch (ParseException e) {
                            System.out.println("ExamDate for " + id + " not in MM/DD/YYYY format");
                        }

                        //Check if API user specified supplemental users for edit or view permission
                        List<String> xmlEditList = Arrays.asList(record.getEditPermissions().split(","));
                        List<String> xmlViewList = Arrays.asList(record.getViewPermissions().split(","));
                        Boolean editUsersSubmitted = ((xmlEditList != null) && (xmlEditList.size() > 0)) ? true : false;
                        Boolean viewUsersSubmitted = ((xmlViewList != null) && (xmlViewList.size() > 0)) ? true : false;

                        // create a json object of the default edit users
                        ArrayList<String> editUserList = new ArrayList<String>();
                        // by default do not add any users
                        //editUserList.add(currentUser);
                        Map<String, Object> editUserListJson = new HashMap<String, Object>();


                        // create a json object of the default view users
                        ArrayList<String> viewUserList = new ArrayList<String>();
                        // by default do not add any users
                        //viewUserList.add(currentUser);
                        //viewUserList.add(submittedData.getPatientUsername());
                        Map<String, Object> viewUserListJson = new HashMap<String, Object>();


                        String finalEditPermissions = "";
                        String finalViewPermissions = "";

                        if (editUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantEdit = xmlEditList;
                            for (String username : userSuppliedListOfUsersToGrantEdit) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    editUserList.add(username);
                                }
                            }
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }
                        else {
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }

                        if (viewUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantView = xmlViewList;
                            for (String username : userSuppliedListOfUsersToGrantView) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    viewUserList.add(username);
                                }
                            }
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }
                        else {
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }

                        //logger.info("Creating records with id " + id);
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId savedMedicalRecord =
                                medicalRecordWithoutAutoIdRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId(id,
                                        "Test Result Record",
                                        recordDate,
                                        record.getOwner(),
                                        record.getPatient(),
                                        finalEditPermissions,
                                        finalViewPermissions));
                        //logger.info("Created  MedicalRecord with id " + savedMedicalRecord.getId());

                        // create the Doctor exam record
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.TestResultRecord savedTestResultRecord =
                                testResultRecordRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.TestResultRecord(id,
                                        record.getDoctor(),
                                        record.getLab(),
                                        record.getNotes(),
                                        examDate));

                        System.out.println("Created TestResultRecord with id " + savedTestResultRecord.getId());

                    }

                    //
                    //
                    // write InsuranceClaimRecords to database
                    //
                    //

                    for (edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.InsuranceClaimRecord record : insuranceClaimRecords) {
                        // change id from string to Integer
                        Integer id = Integer.valueOf(record.getRecordId());

                        // change dates from string to date
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        Date recordDate= new Date();
                        try {
                            recordDate = df.parse(record.getRecordDate());
                        } catch (ParseException e) {
                            System.out.println("RecordDate for " + id + " not in MM/DD/YYYY format");
                        }
                        Date examDate= new Date();
                        try {
                            examDate = df.parse(record.getDate());
                        } catch (ParseException e) {
                            System.out.println("ExamDate for " + id + " not in MM/DD/YYYY format");
                        }

                        //Check if API user specified supplemental users for edit or view permission
                        List<String> xmlEditList = Arrays.asList(record.getEditPermissions().split(","));
                        List<String> xmlViewList = Arrays.asList(record.getViewPermissions().split(","));
                        Boolean editUsersSubmitted = ((xmlEditList != null) && (xmlEditList.size() > 0)) ? true : false;
                        Boolean viewUsersSubmitted = ((xmlViewList != null) && (xmlViewList.size() > 0)) ? true : false;

                        // create a json object of the default edit users
                        ArrayList<String> editUserList = new ArrayList<String>();
                        // by default do not add any users
                        //editUserList.add(currentUser);
                        Map<String, Object> editUserListJson = new HashMap<String, Object>();


                        // create a json object of the default view users
                        ArrayList<String> viewUserList = new ArrayList<String>();
                        // by default do not add any users
                        //viewUserList.add(currentUser);
                        //viewUserList.add(submittedData.getPatientUsername());
                        Map<String, Object> viewUserListJson = new HashMap<String, Object>();


                        String finalEditPermissions = "";
                        String finalViewPermissions = "";

                        if (editUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantEdit = xmlEditList;
                            for (String username : userSuppliedListOfUsersToGrantEdit) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    editUserList.add(username);
                                }
                            }
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }
                        else {
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }

                        if (viewUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantView = xmlViewList;
                            for (String username : userSuppliedListOfUsersToGrantView) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    viewUserList.add(username);
                                }
                            }
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }
                        else {
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }

                        //logger.info("Creating records with id " + id);
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId savedMedicalRecord =
                                medicalRecordWithoutAutoIdRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId(id,
                                        "Insurance Claim Record",
                                        recordDate,
                                        record.getOwner(),
                                        record.getPatient(),
                                        finalEditPermissions,
                                        finalViewPermissions));
                        //logger.info("Created  MedicalRecord with id " + savedMedicalRecord.getId());

                        // create the Doctor exam record
                        Float amount = Float.valueOf(record.getAmount());
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.InsuranceClaimRecord savedInsuranceClaimRecord =
                                insuranceClaimRecordRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.InsuranceClaimRecord(id,
                                        record.getMedicalAdministrator(),
                                        examDate,
                                        record.getStatus(),
                                        amount));

                        System.out.println("Created Insurance Claim Record with id " + savedInsuranceClaimRecord.getId());

                    }

                    //
                    //
                    // write Raw Records to database
                    //
                    //

                    for (edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.RawRecord record : rawRecords) {
                        // change id from string to Integer
                        Integer id = Integer.valueOf(record.getRecordId());

                        // change dates from string to date
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        Date recordDate= new Date();
                        try {
                            recordDate = df.parse(record.getRecordDate());
                        } catch (ParseException e) {
                            System.out.println("RecordDate for " + id + " not in MM/DD/YYYY format");
                        }

                        //Check if API user specified supplemental users for edit or view permission
                        List<String> xmlEditList = Arrays.asList(record.getEditPermissions().split(","));
                        List<String> xmlViewList = Arrays.asList(record.getViewPermissions().split(","));
                        Boolean editUsersSubmitted = ((xmlEditList != null) && (xmlEditList.size() > 0)) ? true : false;
                        Boolean viewUsersSubmitted = ((xmlViewList != null) && (xmlViewList.size() > 0)) ? true : false;

                        // create a json object of the default edit users
                        ArrayList<String> editUserList = new ArrayList<String>();
                        // by default do not add any users
                        //editUserList.add(currentUser);
                        Map<String, Object> editUserListJson = new HashMap<String, Object>();


                        // create a json object of the default view users
                        ArrayList<String> viewUserList = new ArrayList<String>();
                        // by default do not add any users
                        //viewUserList.add(currentUser);
                        //viewUserList.add(submittedData.getPatientUsername());
                        Map<String, Object> viewUserListJson = new HashMap<String, Object>();


                        String finalEditPermissions = "";
                        String finalViewPermissions = "";

                        if (editUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantEdit = xmlEditList;
                            for (String username : userSuppliedListOfUsersToGrantEdit) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    editUserList.add(username);
                                }
                            }
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }
                        else {
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }

                        if (viewUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantView = xmlViewList;
                            for (String username : userSuppliedListOfUsersToGrantView) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    viewUserList.add(username);
                                }
                            }
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }
                        else {
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }

                        //logger.info("Creating records with id " + id);
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId savedMedicalRecord =
                                medicalRecordWithoutAutoIdRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId(id,
                                        "Raw",
                                        recordDate,
                                        record.getOwner(),
                                        record.getPatient(),
                                        finalEditPermissions,
                                        finalViewPermissions));
                        //logger.info("Created  MedicalRecord with id " + savedMedicalRecord.getId());

                        // create the raw record
                        byte[] fileAsBytes = null;
                        // Was the user supplied file actually base64 encoded data?
                        try {
                            fileAsBytes = java.util.Base64.getDecoder().decode(record.getFile());
                        }
                        catch (Exception e) {
                            logger.warn("submitted an invalid base64 encoded file");
                            e.printStackTrace();
                        }
                        Integer fileByteLength = fileAsBytes.length;
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.RawRecord savedRawRecord =
                                rawRecordRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.RawRecord(id,
                                        record.getDescription(),
                                        fileAsBytes,
                                        fileByteLength));

                        System.out.println("Created Raw Record with id " + savedRawRecord.getId());

                    }

                    //
                    //
                    // write PatientDoctorCorrespondenceRecord to database
                    //
                    //

                    for (edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.PatientDoctorCorrespondenceRecord record : patientDoctorCorrespondenceRecords) {
                        // change id from string to Integer
                        Integer id = Integer.valueOf(record.getRecordId());

                        // change dates from string to date
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        Date recordDate= new Date();
                        try {
                            recordDate = df.parse(record.getRecordDate());
                        } catch (ParseException e) {
                            System.out.println("RecordDate for " + id + " not in MM/DD/YYYY format");
                        }

                        //Check if API user specified supplemental users for edit or view permission
                        List<String> xmlEditList = Arrays.asList(record.getEditPermissions().split(","));
                        List<String> xmlViewList = Arrays.asList(record.getViewPermissions().split(","));
                        Boolean editUsersSubmitted = ((xmlEditList != null) && (xmlEditList.size() > 0)) ? true : false;
                        Boolean viewUsersSubmitted = ((xmlViewList != null) && (xmlViewList.size() > 0)) ? true : false;

                        // create a json object of the default edit users
                        ArrayList<String> editUserList = new ArrayList<String>();
                        // by default do not add any users
                        //editUserList.add(currentUser);
                        Map<String, Object> editUserListJson = new HashMap<String, Object>();


                        // create a json object of the default view users
                        ArrayList<String> viewUserList = new ArrayList<String>();
                        // by default do not add any users
                        //viewUserList.add(currentUser);
                        //viewUserList.add(submittedData.getPatientUsername());
                        Map<String, Object> viewUserListJson = new HashMap<String, Object>();


                        String finalEditPermissions = "";
                        String finalViewPermissions = "";

                        if (editUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantEdit = xmlEditList;
                            for (String username : userSuppliedListOfUsersToGrantEdit) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    editUserList.add(username);
                                }
                            }
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }
                        else {
                            editUserListJson.put("users", editUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalEditPermissions = serializer.include("users").serialize(editUserListJson);
                        }

                        if (viewUsersSubmitted) {
                            List<String> userSuppliedListOfUsersToGrantView = xmlViewList;
                            for (String username : userSuppliedListOfUsersToGrantView) {
                                User possibleUser = userRepository.findByUsername(username);
                                if ((possibleUser != null) && (StringUtils.isNotEmpty(possibleUser.getUsername()))) {
                                    viewUserList.add(username);
                                }
                            }
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }
                        else {
                            viewUserListJson.put("users", viewUserList);
                            JSONSerializer serializer = new JSONSerializer();
                            finalViewPermissions = serializer.include("users").serialize(viewUserListJson);
                        }

                        //logger.info("Creating records with id " + id);
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId savedMedicalRecord =
                                medicalRecordWithoutAutoIdRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.MedicalRecordWithoutAutoId(id,
                                        "Patient Doctor Correspondence Record",
                                        recordDate,
                                        record.getOwner(),
                                        record.getPatient(),
                                        finalEditPermissions,
                                        finalViewPermissions));
                        //logger.info("Created  MedicalRecord with id " + savedMedicalRecord.getId());

                        // create the CorrespondenceRecord
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.CorrespondenceRecord savedCorrespondenceRecord =
                                correspondenceRecordRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.CorrespondenceRecord(id,
                                        record.getDoctor()));

                        // write any notes
                        edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.Notes notes = record.getNotes();
                        List<edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.Note> noteList = notes.getNoteList();
                        for (edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml.Note note : noteList) {
                            // change date from string to date
                            DateFormat noteDf = new SimpleDateFormat("MM/dd/yyyy");
                            Date noteDate = new Date();
                            try {
                                noteDate = noteDf.parse(note.getDate());
                            } catch (ParseException e) {
                                System.out.println("Note Date for " + id + " not in MM/DD/YYYY format");
                            }
                            edu.syr.cyberseed.sage.sagebackdoorclient.entities.CorrespondenceRecord savedNote =
                                    correspondenceRecordRepository.save(new edu.syr.cyberseed.sage.sagebackdoorclient.entities.CorrespondenceRecord(id,
                                            noteDate, note.getText()));
                        }

                        System.out.println("Created CorrespondenceRecord with id " + savedCorrespondenceRecord.getId());

                    }

                    break;

                case "getBackupCfg":
                    smirkService = "/getBackupCfg";
                    url = smirkUrl+smirkService;

                    // Create HTTP headers that specify the auth for this request and the content type
                    HttpHeaders httpHeaders = new HttpHeaders();
                    String auth = adminUsername + ":" + adminPassword;
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
                        ResponseEntity<BackupCfg> httpEntityResponse = r1.exchange(url,
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
                    }

                    break;
                case "loadBackupCfg":
                        smirkService = "/loadBackupCfg";
                        url = smirkUrl+smirkService;
                        // Create HTTP headers that specify the auth for this request and the content type
                        HttpHeaders httpHeaders1 = new HttpHeaders();
                        String auth1 = adminUsername + ":" + adminPassword;
                        byte[] encodedAuth1 = Base64.encodeBase64(auth1.getBytes(Charset.forName("US-ASCII")));
                        String authHeader1 = "Basic " + new String(encodedAuth1);
                        httpHeaders1.set("Authorization", authHeader1);
                        httpHeaders1.set("Content-Type", "application/json");
                        String offsiteServerIp="";
                        String offsiteServerUsername="";
                        String offsiteServerPassword="";
                        try (BufferedReader br = new BufferedReader(new FileReader("db_backup_2017.cfg"))) {
                            offsiteServerIp=br.readLine();
                            offsiteServerUsername=br.readLine();
                            offsiteServerPassword=br.readLine();
                            System.out.println(offsiteServerIp);
                            System.out.println(offsiteServerUsername);
                            System.out.println(offsiteServerPassword);
                        } catch (IOException e) {
                            System.out.println("file db_backup_2017.cfg does not exist. Please place it in the same path as the program");
                        }

                        // Define the data we are submitting to the API
                        ObjectMapper mapper = new ObjectMapper();
                        com.fasterxml.jackson.databind.node.ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("offsiteServerIp", offsiteServerIp);
                        objectNode.put("offsiteServerUsername", offsiteServerUsername);
                        objectNode.put("offsiteServerPassword", offsiteServerPassword);
                        objectNode.put("offsiteServerFilename", "db_backup_2017.cfg");
                        String postData = objectNode.toString();

                        // create full request with data and http headers
                        HttpEntity<String> postDataWithHeaders = new HttpEntity <String> (postData, httpHeaders1);
                        try {
                            RestTemplate r = new RestTemplate();
                            r.postForObject(url, postDataWithHeaders, String.class);
                            System.out.println("CFG Loaded");

                        }
                        catch(Exception e)
                        {
                        }
                        break;
                case "DumpDB":
                    String dump="";
                    smirkService = "/dumpDb";
                    url = smirkUrl+smirkService;

                    // Create HTTP headers that specify the auth for this request and the content type
                    HttpHeaders httpHeaders2 = new HttpHeaders();
                    String auth2 = adminUsername+ ":" +adminPassword;
                    byte[] encodedAuth2 = Base64.encodeBase64(auth2.getBytes(Charset.forName("US-ASCII")) );
                    String authHeader2 = "Basic " + new String( encodedAuth2 );
                    httpHeaders2.set("Authorization", authHeader2);
                    httpHeaders2.set("Content-Type", "application/json");

                    // create request with http headers
                    HttpEntity<String> postHeaders2 = new HttpEntity <String> (httpHeaders2);

                    // actually GET the API and get a string array back
                    try {
                        RestTemplate r1 = new RestTemplate();
                        ResponseEntity<String> httpEntityResponse = r1.exchange(url,
                                HttpMethod.GET,
                                postHeaders2,
                                String.class);
                        dump = httpEntityResponse.getBody();

                    }

                    catch (HttpClientErrorException e)
                    {
                        System.out.println(e);
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                    PrintWriter writer = new PrintWriter("smirkDBDump.xml", "UTF-8");
                    writer.println(dump);
                    writer.close();
                    break;
                default:
               /*     if ( args[0].equals("loadBackupCfg") && args[1].equals("db_backup_2017.cfg"))
                    {
                        smirkService = "/loadBackupCfg";
                        url = smirkUrl+smirkService;
                        // Create HTTP headers that specify the auth for this request and the content type
                        HttpHeaders httpHeaders1 = new HttpHeaders();
                        String auth1 = adminUsername + ":" + adminPassword;
                        byte[] encodedAuth1 = Base64.encodeBase64(auth1.getBytes(Charset.forName("US-ASCII")));
                        String authHeader1 = "Basic " + new String(encodedAuth1);
                        httpHeaders1.set("Authorization", authHeader1);
                        httpHeaders1.set("Content-Type", "application/json");
                        String offsiteServerIp="";
                        String offsiteServerUsername="";
                        String offsiteServerPassword="";
                        try (BufferedReader br = new BufferedReader(new FileReader("db_backup_2017.cfg"))) {
                        offsiteServerIp=br.readLine();
                        offsiteServerUsername=br.readLine();
                        offsiteServerPassword=br.readLine();
                        System.out.println(offsiteServerIp);
                        System.out.println(offsiteServerUsername);
                        System.out.println(offsiteServerPassword);
                        } catch (IOException e) {
                            System.out.println("file db_backup_2017.cfg does not exist. Please place it in the same path as the program");
                        }


                        // Define the data we are submitting to the API
                        ObjectMapper mapper = new ObjectMapper();
                        com.fasterxml.jackson.databind.node.ObjectNode objectNode = mapper.createObjectNode();
                        objectNode.put("offsiteServerIp", offsiteServerIp);
                        objectNode.put("offsiteServerUsername", offsiteServerUsername);
                        objectNode.put("offsiteServerPassword", offsiteServerPassword);
                        objectNode.put("offsiteServerFilename", "db_backup_2017.cfg");
                        String postData = objectNode.toString();

                        // create full request with data and http headers
                        HttpEntity<String> postDataWithHeaders = new HttpEntity <String> (postData, httpHeaders1);
                        try {
                            RestTemplate r = new RestTemplate();
                            r.postForObject(url, postDataWithHeaders, String.class);
                            System.out.println("CFG Loaded");

                        }
                        catch(Exception e)
                        {
                        }
                        break;
                    }
                    else {*/
                        System.out.println("Invalid commandline, options are: <setITAdmin|loadData|getBackupCfg|loadBackupCfg db_backup_2017.cfg|DumpDB");
                        break;
                   // }
            }
        } else {
            System.out.println("No commandline parameters specified.");
        }

        return "";
    }

    private ArrayList<String> parseXmlRoles(String roleString) {

        ArrayList<String> appRoleList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(roleString)) {
            List<String> items = Arrays.asList(roleString.split("\\s*,\\s*"));
            for (String role : items) {
                if (role.contains("doctor") || role.contains("DOCTOR") || role.contains("Doctor")) {
                    appRoleList.add("ROLE_DOCTOR");
                }
                if (role.contains("system") || role.contains("SYSTEM") || role.contains("System")) {
                    appRoleList.add("ROLE_SYSTEM_ADMIN");
                }
                if (role.contains("nurse") || role.contains("NURSE") || role.contains("Nurse")) {
                    appRoleList.add("ROLE_NURSE");
                }
                if (role.contains("medical") || role.contains("MEDICAL") || role.contains("Medical")) {
                    appRoleList.add("ROLE_MEDICAL_ADMIN");
                }
                if (role.contains("insurance") || role.contains("INSURANCE") || role.contains("Insurance")) {
                    appRoleList.add("ROLE_INSURANCE_ADMIN");
                }
                if (role.contains("patient") || role.contains("PATIENT") || role.contains("Patient")) {
                    appRoleList.add("ROLE_PATIENT");
                }
            }
        }
        return appRoleList;
    }

    private ArrayList<String> parseXmlPermissions(String permissionString) {

        ArrayList<String> appRoleList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(permissionString)) {
            List<String> items = Arrays.asList(permissionString.split(","));
            for (String role : items) {

                switch (role.toLowerCase()) {
                    case "add patient":
                        appRoleList.add("ROLE_ADD_PATIENT");
                        break;
                    case "edit patient":
                        appRoleList.add("ROLE_EDIT_PATIENT");
                        break;
                    case "add doctor":
                        appRoleList.add("ROLE_ADD_DOCTOR");
                        break;
                    case "edit doctor":
                        appRoleList.add("EDIT_ADD_DOCTOR");
                        break;
                    case "add medical administrator":
                        appRoleList.add("ROLE_ADD_MEDICAL_ADMIN");
                        break;
                    case "edit medical administrator":
                        appRoleList.add("ROLE_EDIT_MEDICAL_ADMIN");
                        break;
                    case "add insurance administrator":
                        appRoleList.add("ROLE_ADD_INSURANCE_ADMIN");
                        break;
                    case "edit insurance administrator":
                        appRoleList.add("ROLE_EDIT_INSURANCE_ADMIN");
                        break;
                    case "add nurse":
                        appRoleList.add("ROLE_ADD_NURSE");
                        break;
                    case "edit nurse":
                        appRoleList.add("ROLE_EDIT_NURSE");
                        break;
                    case "add system administrator":
                        appRoleList.add("ROLE_ADD_SYSTEM_ADMIN");
                        break;
                    case "edit system administrator":
                        appRoleList.add("ROLE_EDIT_SYSTEM_ADMIN");
                        break;
                    case "delete user profile":
                        appRoleList.add("ROLE_DELETE_USER_PROFILE");
                        break;
                    case "assign permissions":
                        appRoleList.add("ROLE_ASSIGN_PERMISSIONS");
                        break;
                    case "edit record access":
                        appRoleList.add("ROLE_EDIT_RECORD_ACCESS");
                        break;
                    case "view pii":
                        appRoleList.add("ROLE_VIEW_PII");
                        break;
                }
            }

        }
        return appRoleList;
    }
}

