package org.example.shopproject.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checkouts")
public class Checkout extends BaseEntity {
    @Column
    private double earnings;
    @OneToOne
    private Cashier cashier;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Client> clients;
    @ManyToOne
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    private Shop shop;

    public Checkout(double earnings, Cashier cashier, Shop shop) {
        this.earnings = earnings;
        this.cashier = cashier;
        this.clients = new ArrayList<>();
        this.shop = shop;
    }

    public Checkout() {
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
