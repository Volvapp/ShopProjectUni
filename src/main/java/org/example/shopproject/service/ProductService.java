package org.example.shopproject.service;

import org.example.shopproject.model.entity.Product;

public interface ProductService {
    String addProduct(Product product);

    String assignToShop(long objectId, long shopId);
}
