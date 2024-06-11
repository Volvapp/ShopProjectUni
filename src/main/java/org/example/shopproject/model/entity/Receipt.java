package org.example.shopproject.model.entity;

import jakarta.persistence.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "receipts")
public class Receipt extends BaseEntity {
    private static final String FILE_PATH = "src/main/resources/generatedReceipts";

    @ManyToOne
    @JoinColumn(name = "cashier_id", referencedColumnName = "id")
    private Cashier cashier;
    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;
    @Column(nullable = false)
    private double price;
    @OneToMany(fetch = FetchType.EAGER)
    private List<ClientProduct> productList;

    public Receipt(Cashier cashier, LocalDateTime issuedDate, List<ClientProduct> productList, double price) {
        this.cashier = cashier;
        this.issuedDate = issuedDate;
        this.productList = productList;
        this.price = price;
    }

    public Receipt() {
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public List<ClientProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<ClientProduct> productList) {
        this.productList = productList;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        StringBuilder boughtProductAndQ = new StringBuilder();
        for (ClientProduct clientProduct : this.getProductList()) {
            boughtProductAndQ.append(String.format("%s - %d\n", clientProduct.getName(), clientProduct.getQuantity()));
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = this.issuedDate.format(formatter);

        return String.format("%s\n" +
                        "Cashier: %s %s\n" +
                        "Serial number - %d\n" +
                        "Bought products:\n" +
                        "%s" +
                        "Total price: %.2f\n" +
                        "Issued on - %s %s\n" +
                        "Thank you for supporting the local business!\n\n",
                this.getCashier().getShop().getName(), this.getCashier().getFirstName(), this.getCashier().getLastName(),
                this.getId(), boughtProductAndQ, this.getPrice(), issuedDate.getDayOfWeek().toString(), formattedDateTime);
    }

    public void saveReceiptToFile() {
        String fileName = String.format("%s_%d_receipt.txt", this.getCashier().getFirstName(), this.getId());
        Path filePath = Paths.get(FILE_PATH, fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(this.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}