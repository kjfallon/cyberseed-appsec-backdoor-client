package edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@XStreamAlias("DBFile")
public class DBFile {

    @XStreamImplicit(itemFieldName = "SystemAdministratorUserProfile")
    private List sysAdminUserProfiles = new ArrayList();

    @XStreamImplicit(itemFieldName = "DoctorUserProfile")
    private List doctorUserProfiles = new ArrayList();

    @XStreamImplicit(itemFieldName = "NurseUserProfile")
    private List nurseUserProfiles = new ArrayList();

    @XStreamImplicit(itemFieldName = "MedicalAdministratorUserProfile")
    private List medAdminUserProfiles = new ArrayList();

    @XStreamImplicit(itemFieldName = "InsuranceAdministratorUserProfile")
    private List InsAdminUserProfiles = new ArrayList();

    @XStreamImplicit(itemFieldName = "PatientUserProfile")
    private List patientUserProfiles = new ArrayList();

    @XStreamImplicit(itemFieldName = "DoctorExamRecord")
    private List doctorExamRecords = new ArrayList();

    @XStreamImplicit(itemFieldName = "DiagnosisRecord")
    private List diagnosisRecords = new ArrayList();

    @XStreamImplicit(itemFieldName = "TestResultsRecord")
    private List testResultsRecords = new ArrayList();

    @XStreamImplicit(itemFieldName = "InsuranceClaimRecord")
    private List insuranceClaimRecords = new ArrayList();

    @XStreamImplicit(itemFieldName = "PatientDoctorCorrespondenceRecord")
    private List patientDoctorCorrespondenceRecords = new ArrayList();

    @XStreamImplicit(itemFieldName = "RawRecord")
    private List rawRecords = new ArrayList();

}

