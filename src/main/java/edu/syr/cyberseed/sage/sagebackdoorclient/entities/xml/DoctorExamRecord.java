package edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("DoctorExamRecord")
public class DoctorExamRecord {

    @XStreamAlias("RecordID")
    private String recordId;

    @XStreamAlias("RecordType")
    private String recordType;

    @XStreamAlias("RecordDate")
    private String recordDate;

    @XStreamAlias("Owner")
    private String owner;

    @XStreamAlias("Patient")
    private String patient;

    @XStreamAlias("EditPermissions")
    private String editPermissions;

    @XStreamAlias("ViewPermissions")
    private String viewPermissions;

    @XStreamAlias("Date")
    private String date;

    @XStreamAlias("Doctor")
    private String doctor;

    @XStreamAlias("Notes")
    private String notes;

}