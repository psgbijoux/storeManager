package com.storemanager.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ScreenProduct {
    private Product product;
    private int quantity;
    private double weight;
    private BigDecimal price;
    private int discount;

    public ScreenProduct(Product product, int quantity, int discount) {
        this.product = product;
        this.quantity = quantity;
        this.weight = 0.0;
        BigDecimal qty = new BigDecimal(quantity);
        BigDecimal dsc = new BigDecimal(discount).multiply(new BigDecimal(0.01));
        this.price = product.getPrice().multiply(qty).subtract(product.getPrice().multiply(qty).multiply(dsc));
        this.discount = discount;
    }

    public ScreenProduct(Product product, double weight, int discount) {
        this.product = product;
        this.weight = weight;
        this.quantity = 1;
        BigDecimal weightValue = new BigDecimal(weight);
        BigDecimal dsc = new BigDecimal(discount).multiply(new BigDecimal(0.01));
        this.price = product.getPrice().multiply(weightValue).subtract(product.getPrice().multiply(weightValue).multiply(dsc));
        this.discount = discount;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        BigDecimal qty = new BigDecimal(quantity);
        this.price = product.getPrice().multiply(qty);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        BigDecimal w = new BigDecimal(weight);
        this.price = product.getPrice().multiply(w);
    }

    public BigDecimal getPrice() {
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDiscount() {
        return this.discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
