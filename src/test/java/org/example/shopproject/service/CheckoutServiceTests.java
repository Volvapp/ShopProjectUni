package org.example.shopproject.service;

import org.example.shopproject.model.entity.*;
import org.example.shopproject.repository.CashierRepository;
import org.example.shopproject.repository.CheckoutRepository;
import org.example.shopproject.repository.ClientRepository;
import org.example.shopproject.repository.ShopRepository;
import org.example.shopproject.service.impl.CheckoutServiceImpl;
import org.example.shopproject.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutServiceTests {

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private CashierRepository cashierRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCheckout_InvalidCheckout() {
        Checkout checkout = new Checkout();
        when(validationUtil.isValid(checkout)).thenReturn(false);

        String result = checkoutService.addCheckout(checkout);

        assertEquals("Invalid checkout!\n", result);
        verify(checkoutRepository, never()).findById(any());
        verify(checkoutRepository, never()).save(any());
    }

    @Test
    void testAddCheckout_CheckoutAlreadyExists() {
        Checkout checkout = new Checkout();
        checkout.setId(1L);
        when(validationUtil.isValid(checkout)).thenReturn(true);
        when(checkoutRepository.findById(checkout.getId())).thenReturn(Optional.of(checkout));

        String result = checkoutService.addCheckout(checkout);

        assertEquals(String.format("Checkout with id: %d already exists!\n", checkout.getId()), result);
        verify(checkoutRepository).findById(checkout.getId());
        verify(checkoutRepository, never()).save(any());
    }

    @Test
    void testAddCheckout_Success() {
        Checkout checkout = new Checkout();
        checkout.setId(1L);
        when(validationUtil.isValid(checkout)).thenReturn(true);
        when(checkoutRepository.findById(checkout.getId())).thenReturn(Optional.empty());

        String result = checkoutService.addCheckout(checkout);

        assertEquals("Successfully added checkout!\n", result);
        verify(checkoutRepository).findById(checkout.getId());
        verify(checkoutRepository).save(checkout);
    }

    @Test
    void testAddCashierToCheckout_CashierDoesNotExist() {
        when(cashierRepository.findById(1L)).thenReturn(Optional.empty());

        String result = checkoutService.addCashierToCheckout(1L, 1L);

        assertEquals("Cashier does not exist!\n", result);
        verify(checkoutRepository, never()).findById(anyLong());
        verify(cashierRepository, never()).save(any());
    }

    @Test
    void testAddCashierToCheckout_CheckoutDoesNotExist() {
        Cashier cashier = new Cashier();
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(checkoutRepository.findById(1L)).thenReturn(Optional.empty());

        String result = checkoutService.addCashierToCheckout(1L, 1L);

        assertEquals("Checkout does not exist!\n", result);
        verify(checkoutRepository).findById(1L);
        verify(cashierRepository, never()).save(any());
    }

    @Test
    void testAddCashierToCheckout_Success() {
        Cashier cashier = new Cashier();
        cashier.setId(1L);
        cashier.setFirstName("John");
        cashier.setLastName("Doe");
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setCashiers(new ArrayList<>());
        Checkout checkout = new Checkout();
        checkout.setId(1L);
        checkout.setShop(shop);
        shop.setCheckouts(new ArrayList<>());
        shop.getCheckouts().add(checkout);

        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        cashier.setShop(shop);

        String result = checkoutService.addCashierToCheckout(1L, 1L);

        assertEquals(String.format("Successfully added cashier %s %s to checkout number: %d!\n", cashier.getFirstName(), cashier.getLastName(), shop.getCheckouts().indexOf(checkout) + 1), result);
        verify(cashierRepository).save(cashier);
        verify(checkoutRepository).save(checkout);
    }

    @Test
    void testAssignToShop_CheckoutDoesNotExist() {
        when(checkoutRepository.findById(1L)).thenReturn(Optional.empty());

        String result = checkoutService.assignToShop(1L, 1L);

        assertEquals("Checkout does not exist!", result);
        verify(shopRepository, never()).findById(anyLong());
        verify(checkoutRepository, never()).save(any());
    }

    @Test
    void testAssignToShop_ShopDoesNotExist() {
        Checkout checkout = new Checkout();
        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));
        when(shopRepository.findById(1L)).thenReturn(Optional.empty());

        String result = checkoutService.assignToShop(1L, 1L);

        assertEquals("Shop does not exist!", result);
        verify(shopRepository).findById(1L);
        verify(checkoutRepository, never()).save(any());
    }

    @Test
    void testAssignToShop_Success() {
        Checkout checkout = new Checkout();
        checkout.setId(1L);
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setCheckouts(new ArrayList<>());

        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        String result = checkoutService.assignToShop(1L, 1L);

        assertEquals(String.format("Checkout with id: %d successfully assigned to shop %s!\n", checkout.getId(), shop.getName()), result);
        verify(checkoutRepository).save(checkout);
        verify(shopRepository).save(shop);
    }

    @Test
    void testGoToQueue_ShopDoesNotExist() {
        when(shopRepository.findById(1L)).thenReturn(Optional.empty());

        String result = checkoutService.goToQueue(1L);

        assertEquals("Shop doesn't exist!\n", result);
    }

    @Test
    void testGoToQueue_NoCheckouts() {
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setCheckouts(new ArrayList<>());
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        String result = checkoutService.goToQueue(1L);

        assertEquals("Shop doesn't have any checkouts!\n", result);
    }

    @Test
    void testGoToQueue_NoClients() {
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setCheckouts(new ArrayList<>());
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        String result = checkoutService.goToQueue(1L);

        assertEquals("Shop doesn't have any checkouts!\n", result);
    }

    @Test
    void testGoToQueue_Success() {
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setCheckouts(new ArrayList<>());
        shop.setClients(new ArrayList<>());

        Client client1 = new Client();
        client1.setId(1L);
        client1.setProducts(new ArrayList<>());
        client1.getProducts().add(new ClientProduct());

        Client client2 = new Client();
        client2.setId(2L);
        client2.setProducts(new ArrayList<>());
        client2.getProducts().add(new ClientProduct());

        shop.getClients().add(client1);
        shop.getClients().add(client2);

        Cashier cashier1 = new Cashier();
        cashier1.setId(1L);
        cashier1.setFirstName("John");
        cashier1.setLastName("Doe");

        Cashier cashier2 = new Cashier();
        cashier2.setId(2L);
        cashier2.setFirstName("Jane");
        cashier2.setLastName("Smith");

        Checkout checkout1 = new Checkout();
        checkout1.setId(1L);
        checkout1.setCashier(cashier1);
        checkout1.setClients(new ArrayList<>());

        Checkout checkout2 = new Checkout();
        checkout2.setId(2L);
        checkout2.setCashier(cashier2);
        checkout2.setClients(new ArrayList<>());

        shop.getCheckouts().add(checkout1);
        shop.getCheckouts().add(checkout2);

        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        String result = checkoutService.goToQueue(1L);

        System.out.println("Actual result: " + result);

        String expectedResult = "Client: 1 assigned to checkout: 1\r\nClient: 2 assigned to checkout: 2\r\n";

        System.out.println("Expected result: " + expectedResult);

        assertEquals(expectedResult, result);

        verify(clientRepository).save(client1);
        verify(clientRepository).save(client2);
        verify(checkoutRepository).save(checkout1);
        verify(checkoutRepository).save(checkout2);
    }
}