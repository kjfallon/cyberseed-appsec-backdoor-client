package edu.syr.cyberseed.sage.sagebackdoorclient.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "patient")
public class Patient {


    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "ssn")
    private int ssn;

    @Column(name = "address")
    private String address;

    //private Set<User> username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {this.username = username;}

    public Date getDob() { return dob; }
    public void setDob(Date dob) {this.dob = dob;}

    public Integer getSsn() { return ssn; }

    public void setSsn(Integer ssn) {this.ssn = ssn;}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {this.address = address;}

    //@ManyToMany(mappedBy = "usernames")
    //public Set<User> getUsername() {
    //return username;
    //}

    public void setUsers(Set<User> users) {
        this.username = username;
    }

    protected Patient() {
    }

    public Patient(String username, Date dob, Integer ssn, String address) {
        this.username = username;
        this.dob = dob;
        this.ssn = ssn;
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("Patient[username=%s, ssn='%d', address='%s']", username, ssn, address);
    }
}
