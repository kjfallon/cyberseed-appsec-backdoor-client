package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("InsuranceAdministratorUserProfile")
public class InsuranceAdministratorUserProfile {

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

    @XStreamAlias("CompanyName")
    private String companyName;

    @XStreamAlias("CompanyAddress")
    private String companyAddress;
}