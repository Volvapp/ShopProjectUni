package org.example.shopproject.service;

import org.example.shopproject.model.entity.Cashier;
import org.example.shopproject.model.entity.Shop;
import org.example.shopproject.repository.CashierRepository;
import org.example.shopproject.repository.ShopRepository;
import org.example.shopproject.service.impl.CashierServiceImpl;
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

class CashierServiceTests {

    @Mock
    private CashierRepository cashierRepository;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private CashierServiceImpl cashierService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCashier_InvalidCashier() {
        Cashier cashier = new Cashier();
        when(validationUtil.isValid(cashier)).thenReturn(false);

        String result = cashierService.addCashier(cashier);

        assertEquals("Invalid cashier!\n", result);
        verify(cashierRepository, never()).findById(any());
        verify(cashierRepository, never()).save(any());
    }

    @Test
    void testAddCashier_CashierAlreadyExists() {
        Cashier cashier = new Cashier();
        cashier.setId(1L);
        when(validationUtil.isValid(cashier)).thenReturn(true);
        when(cashierRepository.findById(cashier.getId())).thenReturn(Optional.of(cashier));

        String result = cashierService.addCashier(cashier);

        assertEquals(String.format("Cashier with id: %d already exists!\n", cashier.getId()), result);
        verify(cashierRepository).findById(cashier.getId());
        verify(cashierRepository, never()).save(any());
    }

    @Test
    void testAddCashier_Success() {
        Cashier cashier = new Cashier();
        cashier.setId(1L);
        when(validationUtil.isValid(cashier)).thenReturn(true);
        when(cashierRepository.findById(cashier.getId())).thenReturn(Optional.empty());

        String result = cashierService.addCashier(cashier);

        assertEquals("Successfully added cashier!\n", result);
        verify(cashierRepository).findById(cashier.getId());
        verify(cashierRepository).save(cashier);
    }

    @Test
    void testAssignToShop_CashierDoesNotExist() {
        when(cashierRepository.findById(1L)).thenReturn(Optional.empty());

        String result = cashierService.assignToShop(1L, 1L);

        assertEquals("Cashier does not exist!\n", result);
        verify(shopRepository, never()).findById(anyLong());
        verify(cashierRepository, never()).save(any());
    }

    @Test
    void testAssignToShop_ShopDoesNotExist() {
        Cashier cashier = new Cashier();
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(shopRepository.findById(1L)).thenReturn(Optional.empty());

        String result = cashierService.assignToShop(1L, 1L);

        assertEquals("Shop does not exist!\n", result);
        verify(shopRepository).findById(1L);
        verify(cashierRepository, never()).save(any());
    }

    @Test
    void testAssignToShop_CashierAlreadyAssigned() {
        Cashier cashier = new Cashier();
        Shop shop = new Shop();
        shop.setName("Test Shop");
        cashier.setShop(shop);
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        String result = cashierService.assignToShop(1L, 1L);

        assertEquals(String.format("Cashier %s %s is already assigned to shop %s!\n", cashier.getFirstName(), cashier.getLastName(), cashier.getShop().getName()), result);
        verify(shopRepository).findById(1L);
        verify(cashierRepository, never()).save(any());
    }

    @Test
    void testAssignToShop_Success() {
        Cashier cashier = new Cashier();
        cashier.setId(1L);
        cashier.setFirstName("John");
        cashier.setLastName("Doe");
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");
        shop.setCashiers(new ArrayList<>());
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        String result = cashierService.assignToShop(1L, 1L);

        assertEquals(String.format("Successfully assigned cashier %s %s to shop %s!\n", cashier.getFirstName(), cashier.getLastName(), shop.getName()), result);
        verify(cashierRepository).findById(1L);
        verify(shopRepository).findById(1L);
        verify(cashierRepository).save(cashier);
        verify(shopRepository).save(shop);
    }
}
