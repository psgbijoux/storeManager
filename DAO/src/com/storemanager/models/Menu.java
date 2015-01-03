package com.storemanager.models;

import javax.persistence.*;

@Entity
@Table(name = "menus")
public class Menu {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    private String name;
    @Column(name = "image_path")
    private String imagePath;
    private String command;
    private int mnemonic;
    @Column(name = "parent_id")
    private int parentId;

    public Menu() {
    }

    public Menu(int id) {
        this.id = id;
    }

    public Menu(String name, String imagePath, String command, int mnemonic, int parentId) {
        this.name = name;
        this.imagePath = imagePath;
        this.command = command;
        this.mnemonic = mnemonic;
        this.parentId = parentId;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(int mnemonic) {
        this.mnemonic = mnemonic;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String toString() {
        return this.getName();
    }
}
