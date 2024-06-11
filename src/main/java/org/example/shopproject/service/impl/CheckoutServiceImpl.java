package org.example.shopproject.service.impl;

import org.example.shopproject.model.entity.*;
import org.example.shopproject.repository.CashierRepository;
import org.example.shopproject.repository.CheckoutRepository;
import org.example.shopproject.repository.ClientRepository;
import org.example.shopproject.repository.ShopRepository;
import org.example.shopproject.service.CheckoutService;
import org.example.shopproject.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final ValidationUtil validationUtil;
    private final CashierRepository cashierRepository;
    private final ShopRepository shopRepository;
    private final ClientRepository clientRepository;

    public CheckoutServiceImpl(CheckoutRepository checkoutRepository, ValidationUtil validationUtil, CashierRepository cashierRepository, ShopRepository shopRepository, ClientRepository clientRepository, Random random, ClientRepository clientRepository1) {
        this.checkoutRepository = checkoutRepository;
        this.validationUtil = validationUtil;
        this.cashierRepository = cashierRepository;
        this.shopRepository = shopRepository;
        this.clientRepository = clientRepository1;
    }

    @Override
    public String addCheckout(Checkout checkout) {
        if (!this.validationUtil.isValid(checkout)) {
            return "Invalid checkout!\n";
        }
        Optional<Checkout> optionalCheckout = checkoutRepository.findById(checkout.getId());
        if (optionalCheckout.isPresent()) {
            return String.format("Checkout with id: %d already exists!\n", checkout.getId());
        }
        this.checkoutRepository.save(checkout);
        return "Successfully added checkout!\n";
    }

    @Override
    public String addCashierToCheckout(long cashierId, long checkoutId) {
        Optional<Cashier> optionalCashier = this.cashierRepository.findById(cashierId);
        if (optionalCashier.isEmpty()) {
            return "Cashier does not exist!\n";
        }
        Optional<Checkout> optionalCheckout = this.checkoutRepository.findById(checkoutId);
        if (optionalCheckout.isEmpty()) {
            return "Checkout does not exist!\n";
        }
        Cashier cashier = optionalCashier.get();
        Checkout checkout = optionalCheckout.get();

        if (checkout.getShop() == null && cashier.getShop() == null) {
            return "Both Checkout and Cashier are not assigned to any shop!\n";
        } else if (checkout.getShop() == null) {
            return "Checkout is not assigned to any shop!\n";
        } else if (cashier.getShop() == null) {
            return "Cashier is not assigned to any shop!\n";
        }

        if (cashier.getShop().getId() != checkout.getShop().getId()) {
            return "Cashier and checkout are in different shops!\n";
        }

        if (cashier.getCheckout() != null) {
            return String.format("Cashier %s %s is already assigned to another checkout!\n",
                    cashier.getFirstName(), cashier.getLastName());
        }
        if (checkout.getCashier() != null) {
            return "Checkout is already assigned to another cashier!\n";
        }

        cashier.setCheckout(checkout);
        checkout.setCashier(cashier);

        this.cashierRepository.save(cashier);
        this.checkoutRepository.save(checkout);

        return String.format("Successfully added cashier %s %s to checkout number: %d!\n", cashier.getFirstName(),
                cashier.getLastName(), checkout.getShop().getCheckouts().indexOf(checkout) + 1);
    }

    @Override
    public String assignToShop(long checkoutId, long shopId) {
        Optional<Checkout> optionalCheckout = this.checkoutRepository.findById(checkoutId);
        if (optionalCheckout.isEmpty()) {
            return "Checkout does not exist!";
        }
        Optional<Shop> optionalShop = this.shopRepository.findById(shopId);
        if (optionalShop.isEmpty()) {
            return "Shop does not exist!";
        }
        Checkout checkout = optionalCheckout.get();

        if (checkout.getShop() != null) {
            return String.format("Checkout with id: %d is already assigned to shop %s!\n",
                    checkout.getId(), checkout.getShop().getName());
        }
        Shop shop = optionalShop.get();

        checkout.setShop(shop);
        shop.getCheckouts().add(checkout);

        this.shopRepository.save(shop);
        this.checkoutRepository.save(checkout);

        return String.format("Checkout with id: %d successfully assigned to shop %s!\n", checkout.getId(), shop.getName());
    }

    @Override
    public String goToQueue(long shopId) {
        Optional<Shop> optionalShop = this.shopRepository.findById(shopId);
        if (optionalShop.isPresent()) {
            Shop shop = optionalShop.get();
            if (!shop.getCheckouts().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Client client : shop.getClients()) {
                    if (!client.getProducts().isEmpty()) {

                        Checkout checkoutWithLeastClients = shop.getCheckouts().get(0);
                        for (Checkout checkout : shop.getCheckouts()) {
                            if (checkout.getCashier() != null) {
                                if (checkout.getClients().size() < checkoutWithLeastClients.getClients().size()) {
                                    checkoutWithLeastClients = checkout;
                                }
                            } else {
                                return "Checkout does not have a cashier assigned to it!\n";
                            }
                        }
                        client.setCheckout(checkoutWithLeastClients);
                        checkoutWithLeastClients.getClients().add(client);
                        sb.append(String.format("Client: %d assigned to checkout: %d",
                                        client.getId(), shop.getCheckouts().indexOf(checkoutWithLeastClients) + 1))
                                .append(System.lineSeparator());
                        this.checkoutRepository.save(checkoutWithLeastClients);
                        this.clientRepository.save(client);
                    }
                }
                return sb.toString();
            }
            return "Shop doesn't have any checkouts!\n";
        }
        return "Shop doesn't exist!\n";
    }

}