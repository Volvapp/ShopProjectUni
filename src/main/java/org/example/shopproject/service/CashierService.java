package org.example.shopproject.service;

import org.example.shopproject.model.entity.Cashier;

public interface CashierService {
    String addCashier(Cashier cashier);

    String assignToShop(long objectId, long shopId);

}
