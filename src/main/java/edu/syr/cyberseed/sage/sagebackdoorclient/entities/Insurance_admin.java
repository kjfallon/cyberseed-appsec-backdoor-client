package edu.syr.cyberseed.sage.sagebackdoorclient.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "insurance_admin")
public class Insurance_admin {

//    private static final long serialVersionUID = -3009157732241241604L;

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "cname")
    private String cname;

    @Column(name = "caddress")
    private String caddress;

    //private Set<User> username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {this.username = username;}

    public String getCname() {
        return cname;
    }
    public void setCname(String cname) {this.cname = cname;}

    public String getCaddress() { return caddress; }
    public void setCaddress(String caddress) {this.caddress = caddress;}
    //@ManyToMany(mappedBy = "usernames")
    //public Set<User> getUsername() {
    //return username;
    //}

    public void setUsers(Set<User> users) {
        this.username = username;
    }

    protected Insurance_admin() {
    }

    public Insurance_admin(String username, String cname, String caddress) {
        this.username = username;
        this.cname = cname;
        this.caddress=caddress;
    }

    @Override
    public String toString() {
        return String.format("Insurance_admin[username=%s, cname='%s', caddress='%s']", username, cname, caddress);
    }
}