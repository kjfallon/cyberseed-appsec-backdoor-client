package edu.syr.cyberseed.sage.sagebackdoorclient.entities;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_permissions_list")
public class UserPermissions {

    @Id
    @Column(name = "role")
    private String role;

    @Column(name = "default_permissions")
    private String permissions;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

}

