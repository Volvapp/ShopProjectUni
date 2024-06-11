package org.example.shopproject.service;

import org.example.shopproject.model.entity.Client;

public interface ClientService {
    String addClient(Client client);

    String assignToShop(long objectId, long shopId);

    String addProductToClient(long clientId);
}
