package org.example.shopproject.service;

import org.example.shopproject.model.entity.Checkout;

public interface CheckoutService {
    String addCheckout(Checkout checkout);

    String addCashierToCheckout(long cashierId, long checkoutId);

    String assignToShop(long objectId, long shopId);

    String goToQueue(long shopId);

}
