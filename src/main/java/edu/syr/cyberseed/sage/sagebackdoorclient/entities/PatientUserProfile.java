package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("PatientUserProfile")
public class PatientUserProfile {

    @XStreamAlias("Username")
    private String username;

    @XStreamAlias("Roles")
    private String roles;

    @XStreamAlias("Permissions")
    private String permissions;

    @XStreamAlias("FirstName")
    private String firstName;

    @XStreamAlias("LastName")
    private String lastName;

    @XStreamAlias("DOB")
    private String dob;

    @XStreamAlias("SSN")
    private String ssn;

    @XStreamAlias("Address")
    private String address;
}