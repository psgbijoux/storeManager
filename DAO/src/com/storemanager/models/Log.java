package com.storemanager.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "logs")
public class Log {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    private String username;
    private String message;
    private String type;
    private String app;
    @Column(name = "add_date")
    private Date addDate;

    public Log() {
    }

    public Log(int id) {
        this.id = id;
    }

    public Log(int id, String username, String message, String type, String app, Date addDate) {
        this.id = id;
        this.username = username;
        this.message = message;
        this.type = type;
        this.app = app;
        this.addDate = addDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}
