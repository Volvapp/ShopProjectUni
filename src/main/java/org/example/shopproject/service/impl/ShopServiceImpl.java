package org.example.shopproject.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.shopproject.model.entity.*;
import org.example.shopproject.repository.*;
import org.example.shopproject.service.ShopService;
import org.example.shopproject.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShopServiceImpl implements ShopService {
    private static final double DISCOUNT = 0.75;
    private static final int DAYS_TO_ACTIVATE_DISCOUNT = 5;
    private final ShopRepository shopRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final CheckoutRepository checkoutRepository;
    private final ReceiptRepository receiptRepository;
    private final ValidationUtil validationUtil;

    public ShopServiceImpl(ShopRepository shopRepository, ClientRepository clientRepository,
                           ProductRepository productRepository, CheckoutRepository checkoutRepository,
                           ReceiptRepository receiptRepository, ValidationUtil validationUtil) {
        this.shopRepository = shopRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.checkoutRepository = checkoutRepository;
        this.receiptRepository = receiptRepository;
        this.validationUtil = validationUtil;
    }

    @Override
    public String addShop(Shop shop) {
        if (!validationUtil.isValid(shop)) {
            return "Invalid shop!\n";
        }
        Optional<Shop> optionalShop = shopRepository.findByName(shop.getName());
        if (optionalShop.isPresent()) {
            return String.format("Shop %s already exists!\n", shop.getName());
        }
        this.shopRepository.save(shop);
        return "Shop added successfully!\n";
    }

    @Override
    public String buy(long shopId) {
        Optional<Shop> optionalShop = this.shopRepository.findById(shopId);
        StringBuilder sb = new StringBuilder();
        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();
            if (!shop.getClients().isEmpty()) {

                List<Long> clientIdsToRemove = new ArrayList<>();
                for (Checkout checkout : shop.getCheckouts()) {
                    for (Client client : checkout.getClients()) {

                        double requiredSum = calculateSum(client);
                        if (requiredSum <= client.getMoney()) {
                            checkout.setEarnings(checkout.getEarnings() + requiredSum);
                            client.setMoney(client.getMoney() - requiredSum);
                            for (ClientProduct clientProduct : client.getProducts()) {
                                shop.getSoldProducts().add(clientProduct);
                            }
                            Receipt receipt = new Receipt(checkout.getCashier(), LocalDateTime.now(), client.getProducts(), requiredSum);
                            this.receiptRepository.save(receipt);
                            checkout.getCashier().getReceipts().add(receipt);
                            receipt.saveReceiptToFile();
                            sb.append(receipt);
                        } else {
                            // Return the products back to the productRepository if not bought
                            returnProducts(client);
                            sb.append(String.format("Client %s does not have enough money!\n\n", client.getFirstName()));
                        }
                        clientIdsToRemove.add(client.getId());
                        this.clientRepository.save(client);
                        this.checkoutRepository.save(checkout);
                    }
                }
                for (Long currentClientId : clientIdsToRemove) {
                    Client client = clientRepository.findById(currentClientId).get();
                    Checkout checkout = client.getCheckout();
                    Shop currentShop = client.getShop();

                    // Remove the client from the checkout -> shop
                    client.setShop(null);
                    client.setCheckout(null);
                    checkout.getClients().remove(client);
                    currentShop.getClients().remove(client);
                    this.clientRepository.save(client);
                    this.checkoutRepository.save(checkout);
                    this.shopRepository.save(currentShop);
                }
            } else {
                sb.append(String.format("No clients in shop %s!\n", shop.getName()));
            }
        }
        return sb.toString();
    }

    private static double calculateSum(Client client) {
        double sum = 0;
        for (ClientProduct clientProduct : client.getProducts()) {
            checkExpireDate(clientProduct);
            sum += clientProduct.getPrice() * clientProduct.getQuantity();
        }
        return sum;
    }

    private static void checkExpireDate(ClientProduct product) {
        if (product.getExpireDate().minusDays(DAYS_TO_ACTIVATE_DISCOUNT).isBefore(LocalDate.now())) {
            product.setPrice(product.getPrice() * DISCOUNT);
        }
    }

    private void returnProducts(Client client) {
        for (ClientProduct currentProduct : client.getProducts()) {
            Optional<Product> optionalProduct = this.productRepository.findByName(currentProduct.getName());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.setQuantity(product.getQuantity() + currentProduct.getQuantity());
            }
        }
        client.getProducts().clear();
    }

    @Override
    public String calculateMoney() {
        StringBuilder sb = new StringBuilder();
        for (Shop shop : this.shopRepository.findAll()) {
            double expensesSum = 0;
            sb.append(String.format("%s expenses: ", shop.getName()));

            for (Product boughtProduct : shop.getBoughtProducts()) {
                expensesSum += boughtProduct.getPrice() * boughtProduct.getQuantity();
            }
            for (Cashier cashier : shop.getCashiers()) {
                expensesSum += cashier.getSalary();
            }
            sb.append(String.format("%.2f", expensesSum)).append(System.lineSeparator());
            sb.append(String.format("%s earnings: ", shop.getName()));
            double earnings = 0;
            for (Checkout checkout : shop.getCheckouts()) {
                earnings += checkout.getEarnings();
            }
            sb.append(String.format("%.2f", earnings)).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
