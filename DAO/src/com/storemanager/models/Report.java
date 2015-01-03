package com.storemanager.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    private String name;
    private byte[] report;
    @Column(name = "add_date")
    private Date addDate;

    public Report() {
    }

    public Report(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getReport() {
        return report;
    }

    public void setReport(byte[] report) {
        this.report = report;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}
