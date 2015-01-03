package com.storemanager.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sale_details")
public class SaleDetail {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    @Column(name = "sale_id")
    private int saleId;
    @Column(name = "product_id")
    private int productId;
    private int quantity;
    private double weight;
    private BigDecimal price;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Column(name = "discount")
    private int discount;
    @Column(name = "discount_price")
    private BigDecimal discountPrice;
    @Column(name = "add_date")
    private Date addDate;


    @OneToOne(targetEntity = Product.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;

    public SaleDetail() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}
