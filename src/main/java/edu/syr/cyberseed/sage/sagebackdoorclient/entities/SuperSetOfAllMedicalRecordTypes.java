package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import lombok.Data;

import java.util.Date;

@Data
public class SuperSetOfAllMedicalRecordTypes {

    // fields from MedicalRecord base
    private Integer medicalRecordId;
    private String medicalRecordRecord_type;
    private String medicalRecordEdit;
    private String medicalRecordView;
    private String medicalRecordOwner;
    private String medicalRecordPatient;
    private Date medicalRecordDate;

    // fields from DoctorExamRecord
    private String doctorExamRecordDoctor;
    private String doctorExamRecordNotes;
    private Date doctorExamRecordExamDate;

    //fields from TestResultRecord
    private String testResultRecordDoctor;
    private String testResultRecordLab;
    private String testResultRecordnotes;
    private Date testResultRecorddate;

    // fields from DiagnosisRecord
    private String diagnosisRecordDoctor;
    private String diagnosisRecordDiagnosis;
    private Date diagnosisRecordDate;

    //fields from InsuranceClaimRecord
    private String insuranceClaimRecordMadmin;
    private String insuranceClaimRecordStatus;
    private Date insuranceClaimRecordClaimDate;
    private Float insuranceClaimRecordClaimAmount;

    //fields from RawRecord
    private String rawRecordDescription;
    private String rawRecordBase64EncodedBinary;

    //fields from CorrespondenceRecord
    private CorrespondenceRecord[] correspondenceRecordList;

}