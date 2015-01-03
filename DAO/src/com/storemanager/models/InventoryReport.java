package com.storemanager.models;

public class InventoryReport {
    private int id;
    private String name;
    private String bareCode;
    private String code;
    private int quantity;
    private int stock;

    public InventoryReport() {
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

    public String getBareCode() {
        return bareCode;
    }

    public void setBareCode(String bareCode) {
        this.bareCode = bareCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
