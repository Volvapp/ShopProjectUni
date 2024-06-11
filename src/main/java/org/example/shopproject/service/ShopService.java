package org.example.shopproject.service;

import org.example.shopproject.model.entity.Shop;

public interface ShopService {
    String addShop(Shop shop);

    String buy(long clientId);

    String calculateMoney();
}
