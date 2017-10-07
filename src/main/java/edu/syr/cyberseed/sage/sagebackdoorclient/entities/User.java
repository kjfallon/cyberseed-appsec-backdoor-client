package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;

    @Column(name = "Fname")
    private String fname;
    
    @Column(name = "Lname")
    private String lname;

    @Column(name = "roles")
    private String roles;

    @Column(name = "custom_permissions_to_add")
    private String custom_permissions_to_add;

    @Column(name = "custom_permissions_to_remove")
    private String custom_permissions_to_remove;

    protected User() {

    }

    public User(String username, String password, String fname, String lname, String roles, String custom_permissions_to_add, String custom_permissions_to_remove) {
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.roles = roles;
        this.custom_permissions_to_add = custom_permissions_to_add;
        this.custom_permissions_to_remove = custom_permissions_to_remove;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getCustom_permissions_to_add() {
        return custom_permissions_to_add;
    }

    public void setCustom_permissions_to_add(String custom_permissions_to_add) {
        this.custom_permissions_to_add = custom_permissions_to_add;
    }

    public String getCustom_permissions_to_remove() {
        return custom_permissions_to_remove;
    }

    public void setCustom_permissions_to_remove(String custom_permissions_to_remove) {
        this.custom_permissions_to_remove = custom_permissions_to_remove;
    }

}


   
