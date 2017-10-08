package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "record")
public class MedicalRecordWithoutAutoId {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "record_type")
    private String record_type;

    @Column(name = "edit")
    private String edit;

    @Column(name = "view")
    private String view;

    @Column(name = "owner")
    private String owner;

    @Column(name = "patient")
    private String patient;

     @Column(name = "date")
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRecord_type() {
        return record_type;
    }

    public void setRecord_type(String record_type) {
        this.record_type = record_type;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    protected MedicalRecordWithoutAutoId() {
    }

    // Constructor when id is specified
    public MedicalRecordWithoutAutoId(Integer id, String record_type, Date date, String owner, String patient, String edit, String view) {
        this.id = id;
        this.record_type = record_type;
        this.date = date;
        this.owner = owner;
        this.patient = patient;
        this.edit = edit;
        this.view = view;
    }

    @Override
    public String toString() {
        return String.format("MedicalRecord[recordID=%d, patient='%s', owner='%s']", id, patient, owner);
    }

}