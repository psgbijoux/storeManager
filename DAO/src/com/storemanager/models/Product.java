package com.storemanager.models;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "products", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Product implements Cloneable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;
    @Column(name = "category_id")
    private int categoryId;
    private String name;
    private String description;
    private String code;
    @Column(name = "bare_code")
    private String bareCode;
    private int quantity;
    private BigDecimal price;
    @Column(name = "image", columnDefinition = "longblob")
    private byte[] image;
    private double weight;
    @Column(name = "is_gold")
    private boolean isGold;
    @Column(name = "is_other")
    private boolean isOther;
    @Column(name = "alert_flag")
    private boolean alertFlag;
    @Column(name = "alert_value")
    private String alertValue;
    @Column(name = "generate_bare_code_flag")
    private boolean generateBareCodeFlag;
    @OneToOne(targetEntity = Category.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Category category;

    public Product() {
    }

    public Product(int id) {
        this.id = id;
    }

    public Product(String bareCode) {
        this.bareCode = bareCode;
    }

    public Product(int categoryId, String name, String description, String code, String bareCode, int quantity, BigDecimal price) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.code = code;
        this.bareCode = bareCode;
        this.quantity = quantity;
        this.price = price;
        price.setScale(2, RoundingMode.HALF_UP);
    }

    public Product(int id, int categoryId, String name, String description, String code, String bareCode, int quantity, BigDecimal price, byte[] image) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.code = code;
        this.bareCode = bareCode;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
        price.setScale(2, RoundingMode.HALF_UP);
    }

    public Product(int id, int categoryId, String name, String description, String code, String bareCode, int quantity, BigDecimal price, byte[] image, double weight, boolean gold, boolean other, boolean alertFlag, String alertValue, boolean generateBareCodeFlag, Category category) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.code = code;
        this.bareCode = bareCode;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
        this.weight = weight;
        isGold = gold;
        isOther = other;
        this.alertFlag = alertFlag;
        this.alertValue = alertValue;
        this.generateBareCodeFlag = generateBareCodeFlag;
        this.category = category;
    }

    public Product clone() {
        Product product = new Product(id, categoryId, name, description, code, bareCode, quantity, price, image, weight, isGold, isOther, alertFlag, alertValue, generateBareCodeFlag, category);
        return product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBareCode() {
        return bareCode;
    }

    public void setBareCode(String bareCode) {
        this.bareCode = bareCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isGold() {
        return isGold;
    }

    public void setGold(boolean gold) {
        isGold = gold;
    }

    public boolean isOther() {
        return isOther;
    }

    public void setOther(boolean other) {
        isOther = other;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isAlertFlag() {
        return alertFlag;
    }

    public void setAlertFlag(boolean alertFlag) {
        this.alertFlag = alertFlag;
    }

    public String getAlertValue() {
        return alertValue;
    }

    public void setAlertValue(String alertValue) {
        this.alertValue = alertValue;
    }

    public boolean isGenerateBareCodeFlag() {
        return generateBareCodeFlag;
    }

    public void setGenerateBareCodeFlag(boolean generateBareCodeFlag) {
        this.generateBareCodeFlag = generateBareCodeFlag;
    }
}
