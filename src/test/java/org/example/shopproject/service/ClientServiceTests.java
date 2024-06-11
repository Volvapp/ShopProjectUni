package org.example.shopproject.service;

import org.example.shopproject.model.entity.Client;
import org.example.shopproject.model.entity.ClientProduct;
import org.example.shopproject.model.entity.Product;
import org.example.shopproject.model.entity.Shop;
import org.example.shopproject.repository.ClientProductRepository;
import org.example.shopproject.repository.ClientRepository;
import org.example.shopproject.repository.ProductRepository;
import org.example.shopproject.repository.ShopRepository;
import org.example.shopproject.service.impl.ClientServiceImpl;
import org.example.shopproject.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ClientServiceTests {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private Random random;
    @Mock
    private ClientProductRepository clientProductRepository;
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private Shop shop;
    private ClientProduct clientProduct;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        random = new Random();
        client = new Client();
        shop = new Shop();
        clientProduct = new ClientProduct();
        product = new Product();
        prepareObjects();
    }

    private void prepareObjects() {
        client.setId(1L);
        client.setFirstName("John");

        shop.setId(1L);
        shop.setName("Test Shop");

        clientProduct.setId(1L);
        clientProduct.setName("Test Product");
        clientProduct.setPrice(10.0);
    }

    @Test
    void testAddClient_InvalidClient() {
        when(validationUtil.isValid(client)).thenReturn(false);
        String result = clientService.addClient(client);
        assertEquals("Invalid client!\n", result);
        verify(clientRepository, never()).findById(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void testAddClient_ClientAlreadyExists() {
        when(validationUtil.isValid(client)).thenReturn(true);
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        String result = clientService.addClient(client);
        assertEquals(String.format("Client %s already exists!\n", client.getFirstName()), result);
        verify(clientRepository).findById(client.getId());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void testAddClient_Success() {
        when(validationUtil.isValid(client)).thenReturn(true);
        when(clientRepository.findById(client.getId())).thenReturn(Optional.empty());
        String result = clientService.addClient(client);
        assertEquals("Successfully added client!\n", result);
        verify(clientRepository).findById(client.getId());
        verify(clientRepository).save(client);
    }

    @Test
    void testAssignToShop_ClientNotExists() {
        long clientId = 1L;
        long shopId = 1L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        String result = clientService.assignToShop(clientId, shopId);
        assertEquals(String.format("Client with id: %d does not exist!\n", clientId), result);
    }


    @Test
    void testAssignToShop_ShopNotExists() {
        long clientId = 1L;
        long shopId = 1L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());
        String result = clientService.assignToShop(clientId, shopId);
        assertEquals(String.format("Shop with id: %d does not exist!\n", shopId), result);
        verify(clientRepository).findById(clientId);
        verify(shopRepository).findById(shopId);
    }

    @Test
    void testAssignToShop_Success() {
        long clientId = 1L;
        long shopId = 1L;
        client.setShop(null);
        shop.setClients(new ArrayList<>());
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        String result = clientService.assignToShop(clientId, shopId);
        assertEquals(String.format("Client %s assigned to shop %s successfully!\n", client.getFirstName(), shop.getName()), result);
        assertEquals(shop, client.getShop());
        verify(clientRepository).findById(clientId);
        verify(shopRepository).findById(shopId);
        verify(clientRepository).save(client);
        verify(shopRepository).save(shop);
    }

    @Test
    void testAddProductToClient_ClientNotExists() {
        long clientId = 1L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        String result = clientService.addProductToClient(clientId);
        assertEquals("Client not found!\n", result);
        verify(clientRepository).findById(clientId);
        verifyNoMoreInteractions(shopRepository, clientProductRepository, productRepository);
    }

    @Test
    void testAddProductToClient_ClientNotInShop() {
        long clientId = 1L;
        client.setShop(null);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        String result = clientService.addProductToClient(clientId);
        assertEquals("Client is not in any shop!\n", result);
        verify(clientRepository).findById(clientId);
        verifyNoMoreInteractions(shopRepository, clientProductRepository, productRepository);
    }
    @Test
    void testAddProductToClient_WithFixedProductIndex() {
        long clientId = 1L;

        // Mock the client repository
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Mock the shop

        client.setShop(shop);
        // Mock the product
        shop.setBoughtProducts(new ArrayList<>());
        shop.getBoughtProducts().add(product);
        client.setProducts(new ArrayList<>());
        product.setName("Sample Product");
        product.setQuantity(10);

        // Set the bought products of the shop
        shop.setBoughtProducts(Collections.singletonList(product));

        // Mock the shop repository
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        // Call the method under test
        String result = clientService.addProductToClient(clientId);

        // Assert the result
        assertEquals("Successfully added product Sample Product to the client!\n", result);

        // Verify that the client product and product repositories were called as expected
        verify(clientProductRepository, times(1)).save(any(ClientProduct.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

}
