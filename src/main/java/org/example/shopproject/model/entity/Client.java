package org.example.shopproject.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client extends BaseEntity {
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(nullable = false)
    private double money;
    @OneToMany(fetch = FetchType.EAGER)
    private List<ClientProduct> products;
    @ManyToOne
    private Checkout checkout;

    @ManyToOne
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    private Shop shop;

    public Client(String firstName, double money, Checkout checkout, Shop shop) {
        this.firstName = firstName;
        this.money = money;
        this.products = new ArrayList<>();
        this.checkout = checkout;
        this.shop = shop;
    }

    public Client() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public List<ClientProduct> getProducts() {
        return products;
    }

    public void setProducts(List<ClientProduct> products) {
        this.products = products;
    }

    public Checkout getCheckout() {
        return checkout;
    }

    public void setCheckout(Checkout checkout) {
        this.checkout = checkout;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
