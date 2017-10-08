/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 *
 * @author DhruvVerma
 */
@Entity
@Table(name = "record_doctor_exam")
public class DoctorExamRecord {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "doctor")
    private String doctor;

    @Column(name = "notes")
    private String notes;

    @Column(name = "date")
    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    protected DoctorExamRecord() {
    }

  /*  public DoctorExamRecord(String doctor, Date date, String notes) {
        this.doctor = doctor;
        this.date = date;
        this.notes = notes;
    }
*/

    public DoctorExamRecord(Integer id, String doctor, Date date, String notes) {
        this.id = id;
        this.doctor = doctor;
        this.date = date;
        this.notes = notes;
    }

}