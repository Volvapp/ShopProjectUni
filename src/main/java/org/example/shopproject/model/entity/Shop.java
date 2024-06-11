package org.example.shopproject.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shops")
public class Shop extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany
    private List<Cashier> cashiers;
    @OneToMany
    private List<Checkout> checkouts;
    @OneToMany
    private List<Product> boughtProducts;
    @OneToMany
    private List<ClientProduct> soldProducts;
    @OneToMany
    private List<Client> clients;

    public Shop(String name) {
        this.name = name;
        this.cashiers = new ArrayList<>();
        this.checkouts = new ArrayList<>();
        this.boughtProducts = new ArrayList<>();
        this.soldProducts = new ArrayList<>();
        this.clients = new ArrayList<>();
    }

    public Shop() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Cashier> getCashiers() {
        return cashiers;
    }

    public void setCashiers(List<Cashier> cashiers) {
        this.cashiers = cashiers;
    }

    public List<Checkout> getCheckouts() {
        return checkouts;
    }

    public void setCheckouts(List<Checkout> checkouts) {
        this.checkouts = checkouts;
    }

    public List<Product> getBoughtProducts() {
        return boughtProducts;
    }

    public void setBoughtProducts(List<Product> boughtProducts) {
        this.boughtProducts = boughtProducts;
    }

    public List<ClientProduct> getSoldProducts() {
        return soldProducts;
    }

    public void setSoldProducts(List<ClientProduct> soldProducts) {
        this.soldProducts = soldProducts;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}
