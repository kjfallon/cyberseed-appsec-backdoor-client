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
@Table(name = "record_insurance")
@Data
public class InsuranceClaimRecord {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private Date date;

    @Column(name = "madmin")
    private String madmin;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "status")
    private String status;


    protected InsuranceClaimRecord()
    {

    }
    public InsuranceClaimRecord(Integer id, String admin, Date date, String status, Float amount) {
        this.id = id;
        this.madmin = admin;
        this.date = date;
        this.status = status;
        this.amount=amount;
    }
}