package edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("RawRecord")
public class RawRecord {

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

    @XStreamAlias("Description")
    private String description;

    @XStreamAlias("File")
    private String file;

}