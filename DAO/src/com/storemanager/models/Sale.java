package com.storemanager.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    @Column(name = "user_id")
    private int userId;
    private BigDecimal price;
    @Column(name = "add_date")
    private Date addDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sale_id")
    private List<SaleDetail> details = new ArrayList<SaleDetail>();

    public Sale() {
    }

    public Sale(int id) {
        this.id = id;
    }

    public Sale(int userId, BigDecimal price, Date addDate) {
        this.userId = userId;
        this.price = price;
        this.addDate = addDate;
    }

    public Sale(int id, int userId, BigDecimal price, Date addDate) {
        this.id = id;
        this.userId = userId;
        this.price = price;
        this.addDate = addDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public List<SaleDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetail> details) {
        this.details = details;
    }

    public void addDetail(SaleDetail detail) {
        details.add(detail);
    }
}
