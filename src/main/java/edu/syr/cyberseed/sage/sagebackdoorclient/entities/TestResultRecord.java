/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import lombok.Data;

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
@Table(name = "record_testresult")
@Data
public class TestResultRecord {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "doctor")
    private String doctor;

    @Column(name = "lab")
    private String lab;

    @Column(name = "notes")
    private String notes;

    @Column(name = "date")
    private Date date;

    protected TestResultRecord() {
    }

    public TestResultRecord(Integer id, String doctor, String lab, String notes, Date date) {
        this.id = id;
        this.doctor = doctor;
        this.lab = lab;
        this.notes = notes;
        this.date=date;
    }
}