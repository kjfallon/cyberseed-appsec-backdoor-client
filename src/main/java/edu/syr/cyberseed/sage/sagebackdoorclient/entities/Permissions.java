package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "permissions_list")
public class Permissions {

    @Id
    @Column(name = "permission")
    private String permission;

    @Column(name = "description")
    private String description;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
