package org.example.shopproject.service.impl;

import org.example.shopproject.model.entity.*;
import org.example.shopproject.repository.ClientProductRepository;
import org.example.shopproject.repository.ClientRepository;
import org.example.shopproject.repository.ProductRepository;
import org.example.shopproject.repository.ShopRepository;
import org.example.shopproject.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.example.shopproject.service.ClientService;

import java.util.Optional;
import java.util.Random;

@Service
public class ClientServiceImpl implements ClientService {
    private final Random random;
    private final ClientRepository clientRepository;
    private final ValidationUtil validationUtil;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ClientProductRepository clientProductRepository;

    public ClientServiceImpl(Random random, ClientRepository clientRepository, ValidationUtil validationUtil, ShopRepository shopRepository, ProductRepository productRepository, ClientProductRepository clientProductRepository) {
        this.random = random;
        this.clientRepository = clientRepository;
        this.validationUtil = validationUtil;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.clientProductRepository = clientProductRepository;
    }

    @Override
    public String addClient(Client client) {
        if (!this.validationUtil.isValid(client)) {
            return "Invalid client!\n";
        }
        Optional<Client> optionalClient = this.clientRepository.findById(client.getId());
        if (optionalClient.isPresent()) {
            return String.format("Client %s already exists!\n", optionalClient.get().getFirstName());
        }
        this.clientRepository.save(client);
        return "Successfully added client!\n";
    }

    @Override
    public String assignToShop(long clientId, long shopId) {
        Optional<Client> optionalClient = this.clientRepository.findById(clientId);
        Optional<Shop> optionalShop = this.shopRepository.findById(shopId);
        if (optionalClient.isEmpty()) {
            return String.format("Client with id: %d does not exist!\n", clientId);
        }
        if (optionalShop.isEmpty()) {
            return String.format("Shop with id: %d does not exist!\n", shopId);
        }
        Client client = optionalClient.get();

        if (client.getShop() != null) {
            return String.format("Client %s is already shopping in shop %s\n", client.getFirstName(), client.getShop().getName());
        }
        Shop shop = optionalShop.get();

        client.setShop(shop);
        shop.getClients().add(client);

        this.clientRepository.save(client);
        this.shopRepository.save(shop);

        return String.format("Client %s assigned to shop %s successfully!\n", client.getFirstName(), shop.getName());
    }

    @Override
    public String addProductToClient(long clientId) {
        Optional<Client> optionalClient = this.clientRepository.findById(clientId);

        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();

            if (client.getShop() != null) {
                Shop shop = shopRepository.findById(client.getShop().getId()).get();
                if (shop.getBoughtProducts().isEmpty()){
                    return "shop is empty!\n";
                }
                long randomProductPos = random.nextLong(shop.getBoughtProducts().size());
                if (randomProductPos >= shop.getBoughtProducts().size() || randomProductPos < 0) {
                    return "Invalid product position!\n";
                }

                Product product = shop.getBoughtProducts().get(Math.toIntExact(randomProductPos));
                ClientProduct clientProduct = new ClientProduct(product.getName(), product.getPrice(), product.getCategory(),
                        product.getExpireDate(), 0, product.isExpired(), product.getShop());

                if (productExists(client, clientProduct)) {
                    return String.format("Client already has product %s in his cart!\n", product.getName());
                }

                int requiredQuantity = random.nextInt(product.getQuantity() + 3) + 1;

                if (requiredQuantity <= product.getQuantity()) {

                    clientProduct.setQuantity(requiredQuantity);
                    product.setQuantity(product.getQuantity() - clientProduct.getQuantity());
                    client.getProducts().add(clientProduct);

                    this.clientProductRepository.save(clientProduct);
                    this.productRepository.save(product);
                    this.clientRepository.save(client);
                    return String.format("Successfully added product %s to the client!\n", product.getName());
                }
                return String.format("Quantity not enough! Product: %s\n" +
                                "Required quantity: %d\n" +
                                "Available quantity: %d\n",
                        product.getName(), requiredQuantity, product.getQuantity());

            }
            return "Client is not in any shop!\n";
        }
        return "Client not found!\n";
    }


    private boolean productExists(Client client, ClientProduct product) {
        for (ClientProduct currentProduct : client.getProducts()) {
            if (currentProduct.getName().equals(product.getName())) {
                return true;
            }
        }
        return false;
    }


}
