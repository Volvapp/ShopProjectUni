package org.example.shopproject.core;

import org.example.shopproject.model.entity.*;

public interface Controller {
    String addShop(Shop shop);

    String addCheckout(Checkout checkout);

    String addClient(Client client);

    String addProduct(Product product);

    String addCashier(Cashier cashier);

    String addReceipt(Receipt receipt);

    String addCashierToCheckout(long cashierId, long checkoutId);

    String assignToShop(String nameOfObject, long objectId, long shopId);

    String buy(long clientId);

    String addProductToClient(long clientId);

    String goToQueue(long shopId);

    String calculateMoney();
}
