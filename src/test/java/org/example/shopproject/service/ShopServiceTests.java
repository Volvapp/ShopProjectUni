package org.example.shopproject.service;

import org.example.shopproject.model.entity.*;
import org.example.shopproject.model.enums.Category;
import org.example.shopproject.repository.*;
import org.example.shopproject.service.impl.ShopServiceImpl;
import org.example.shopproject.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShopServiceTests {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private ShopServiceImpl shopService;

    private Shop shop;
    private Client client;
    private Checkout checkout;
    private Cashier cashier;
    private Product product;
    private ClientProduct clientProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        shop = new Shop("TestShop");
        shop.setId(1L);

        cashier = new Cashier("Jane", "Doe", 2000, null, shop);
        cashier.setId(1L);
        shop.getCashiers().add(cashier);

        product = new Product("Product1", 10.0, 8.0, Category.EDIBLE.name(),
                LocalDate.now().plusDays(1), 30, false, shop);
        product.setId(1L);
        shop.getBoughtProducts().add(product);

        client = new Client("John", 50.0, null, shop);
        client.setId(1L);
        clientProduct = new ClientProduct("Product1", 10.0, Category.EDIBLE,
                LocalDate.now().plusDays(1), 5, false, shop);
        clientProduct.setId(1L);
        client.getProducts().add(clientProduct);
        shop.getClients().add(client);

        checkout = new Checkout(0, cashier, shop);
        checkout.setId(1L);
        checkout.getClients().add(client);
        cashier.setCheckout(checkout);
        shop.getCheckouts().add(checkout);

        client.setCheckout(checkout);
    }

    @Test
    void testAddShopSuccess() {
        when(validationUtil.isValid(shop)).thenReturn(true);
        when(shopRepository.findByName(shop.getName())).thenReturn(Optional.empty());

        String result = shopService.addShop(shop);

        assertEquals("Shop added successfully!\n", result);
        verify(shopRepository, times(1)).save(shop);
    }

    @Test
    void testAddShopInvalid() {
        when(validationUtil.isValid(shop)).thenReturn(false);

        String result = shopService.addShop(shop);

        assertEquals("Invalid shop!\n", result);
        verify(shopRepository, never()).save(shop);
    }

    @Test
    void testAddShopAlreadyExists() {
        when(validationUtil.isValid(shop)).thenReturn(true);
        when(shopRepository.findByName(shop.getName())).thenReturn(Optional.of(shop));

        String result = shopService.addShop(shop);

        assertEquals(String.format("Shop %s already exists!\n", shop.getName()), result);
        verify(shopRepository, never()).save(shop);
    }

    @Test
    void testBuyWithSufficientMoney() {
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        String result = shopService.buy(shop.getId());

        assertTrue(result.contains("Thank you for supporting the local business!"));

        verify(receiptRepository, times(1)).save(any(Receipt.class));
        verify(clientRepository, atLeastOnce()).save(any(Client.class));
        verify(checkoutRepository, atLeastOnce()).save(any(Checkout.class));
        verify(shopRepository, atLeastOnce()).save(any(Shop.class));
    }

    @Test
    void testBuyWithInsufficientMoney() {
        client.setMoney(0);
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        String result = shopService.buy(shop.getId());

        assertTrue(result.contains(String.format("Client %s does not have enough money!\n\n", client.getFirstName())));
        verify(receiptRepository, never()).save(any(Receipt.class));
        verify(clientRepository, atLeastOnce()).save(any(Client.class));
        verify(checkoutRepository, atLeastOnce()).save(any(Checkout.class));
        verify(shopRepository, atLeastOnce()).save(any(Shop.class));
    }

    @Test
    void testBuyNoClients() {
        shop.getClients().clear();
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        String result = shopService.buy(shop.getId());

        assertTrue(result.contains(String.format("No clients in shop %s!\n", shop.getName())));
        verify(receiptRepository, never()).save(any(Receipt.class));
        verify(clientRepository, never()).save(any(Client.class));
        verify(checkoutRepository, never()).save(any(Checkout.class));
        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void testCalculateMoney() {
        when(shopRepository.findAll()).thenReturn(List.of(shop));

        String result = shopService.calculateMoney();

        assertTrue(result.contains(String.format("%s expenses: ", shop.getName())));
        assertTrue(result.contains(String.format("%s earnings: ", shop.getName())));
    }
}
