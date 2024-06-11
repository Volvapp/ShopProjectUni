package org.example.shopproject.model.entity;

import jakarta.persistence.*;
import org.example.shopproject.model.enums.Category;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "client_products_in_cart")
public class ClientProduct extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "is_expired", nullable = false)
    private boolean isExpired;
    @ManyToOne
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    private Shop shop;

    public ClientProduct(String name, double price, Category category, LocalDate expireDate, int quantity, boolean isExpired, Shop shop) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.expireDate = expireDate;
        this.quantity = quantity;
        this.isExpired = isExpired;
        this.shop = shop;
    }

    public ClientProduct() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
