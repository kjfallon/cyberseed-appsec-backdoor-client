package edu.syr.cyberseed.sage.sagebackdoorclient.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "doctor")
public class Doctor {

//    private static final long serialVersionUID = -3009157732241241604L;

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "Pname")
    private String pname;

    @Column(name = "Paddress")
    private String paddress;

    @Column(name = "Rphrase")
    private String rphrase;

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
    public void setPaddress (String paddress) {this.paddress = paddress;}

    public String getRphrase() {
        return rphrase;
    }
    public void setRphrase(String rphrase) {this.rphrase = rphrase;}
    //@ManyToMany(mappedBy = "usernames")
    //public Set<User> getUsername() {
        //return username;
    //}

    public void setUsers(Set<User> users) {
        this.username = username;
    }

    protected Doctor() {
    }

    public Doctor(String username, String pname, String paddress, String rphrase) {
        this.username = username;
        this.pname = pname;
        this.paddress = paddress;
        this.rphrase = rphrase;
    }

    @Override
    public String toString() {
        return String.format("Doctor[username=%s, pname='%s', paddress='%s', rpharse='%s']", username, pname, paddress, rphrase);
    }
}