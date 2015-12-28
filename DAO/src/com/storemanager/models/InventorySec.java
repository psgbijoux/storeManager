package com.storemanager.models;

import javax.persistence.*;

@Entity
@Table(name = "inventory_cat2")
public class InventorySec {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    @Column(name = "product_id")
    private int productId;
    private int quantity;
    @Column(name = "product_name")
    private String productName;

    public InventorySec() {
    }

    public InventorySec(Product product) {
        this.productId = product.getId();
        this.quantity = 1;
        this.productName = product.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
