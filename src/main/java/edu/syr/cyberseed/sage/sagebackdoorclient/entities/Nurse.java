package edu.syr.cyberseed.sage.sagebackdoorclient.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "nurse")
public class Nurse {

//    private static final long serialVersionUID = -3009157732241241604L;

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "pname")
    private String pname;

    @Column(name = "paddress")
    private String paddress;

    @Column(name = "adoctors")
    private String adoctors;

    //private Set<User> username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {this.username = username;}

    public String getPname() {
        return pname;
    }
    public void setPname(String pname) {this.pname = pname;}

    public String getPaddress() { return paddress; }
    public void setPaddress(String paddress) {this.paddress = paddress;}

    public String getAdoctors() {
        return adoctors;
    }
    public void setAdoctors(String adoctor) {this.adoctors = adoctor;}

    //@ManyToMany(mappedBy = "usernames")
    //public Set<User> getUsername() {
    //return username;
    //}

    public void setUsers(Set<User> users) {
        this.username = username;
    }

    protected Nurse() {
    }

    public Nurse(String username, String pname, String paddress, String adoctor) {
        this.username = username;
        this.pname = pname;
        this.paddress= paddress;
        this.adoctors=adoctor;
    }

    @Override
    public String toString() {
        return String.format("Nurse[username=%s, pname='%s', paddress='%s', adoctor='%s']", username, pname, paddress, adoctors);
    }
}
