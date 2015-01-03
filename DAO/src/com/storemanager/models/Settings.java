package com.storemanager.models;

import com.storemanager.util.DAOException;

import javax.persistence.*;

@Entity
@Table(name = "settings")
public class Settings {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    private String name;
    private String value;

    public Settings() {
    }

    public Settings(int id) {
        this.id = id;
    }


    public Settings(String name, String value) {
        this.name = name;
        this.value = value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIntValue() throws DAOException {
        int value = 0;
        try {
            value = Integer.parseInt(this.getValue());
        } catch (NumberFormatException nfe) {
            throw new DAOException("Error parsing int value for setting: " + this.name);
        }
        return value;
    }

    public double getDoubleValue() throws DAOException {
        double value = 0;
        try {
            value = Double.parseDouble(this.getValue());
        } catch (NumberFormatException nfe) {
            throw new DAOException("Error parsing double value for setting: " + this.name);
        }
        return value;
    }
}
