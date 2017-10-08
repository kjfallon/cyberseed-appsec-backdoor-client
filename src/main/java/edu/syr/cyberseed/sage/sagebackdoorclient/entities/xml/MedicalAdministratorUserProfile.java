package edu.syr.cyberseed.sage.sagebackdoorclient.entities.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("MedicalAdministratorUserProfile")
public class MedicalAdministratorUserProfile {

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

    @XStreamAlias("PracticeName")
    private String practiceName;

    @XStreamAlias("PracticeAddress")
    private String practiceAddress;

    @XStreamAlias("AssociatedDoctors")
    private String associatedDoctors;

    @XStreamAlias("AssociatedNurses")
    private String associatedNurses;

}