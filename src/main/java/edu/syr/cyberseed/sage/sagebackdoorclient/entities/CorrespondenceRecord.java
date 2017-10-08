/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.syr.cyberseed.sage.sagebackdoorclient.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @author DhruvVerma
 */
@Entity
@Table(name = "record_correspondence")
@Data
public class CorrespondenceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "note_id")
    private Integer noteId;

    @Column(name = "id")
    private Integer id;

    @Column(name = "doctor")
    private String doctor;

    @Column(name = "note_date")
    private Date note_date;

    @Column(name = "note_text")
    private String note_text;

    private CorrespondenceRecord(){}

    public CorrespondenceRecord(Integer id, String doctor)
    {
        this.id=id;
        this.doctor=doctor;
    }

    public CorrespondenceRecord(Integer id, Date d, String n)
    {
        this.id=id;
        this.note_date=d;
        this.note_text=n;
    }



}