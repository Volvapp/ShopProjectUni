package org.example.shopproject.service.impl;

import org.example.shopproject.model.entity.*;
import org.example.shopproject.repository.CashierRepository;
import org.example.shopproject.repository.CheckoutRepository;
import org.example.shopproject.repository.ClientRepository;
import org.example.shopproject.repository.ShopRepository;
import org.example.shopproject.service.CashierService;
import org.example.shopproject.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service

public class CashierServiceImpl implements CashierService {
    private final CashierRepository cashierRepository;
    private final ValidationUtil validationUtil;
    private final ShopRepository shopRepository;

    public CashierServiceImpl(CashierRepository cashierRepository, ValidationUtil validationUtil, ShopRepository shopRepository, CheckoutRepository checkoutRepository, ClientRepository clientRepository) {
        this.cashierRepository = cashierRepository;
        this.validationUtil = validationUtil;
        this.shopRepository = shopRepository;
    }

    @Override
    public String addCashier(Cashier cashier) {
        if (!this.validationUtil.isValid(cashier)) {
            return "Invalid cashier!\n";
        }
        Optional<Cashier> optionalCashier = cashierRepository.findById(cashier.getId());
        if (optionalCashier.isPresent()) {
            return String.format("Cashier with id: %d already exists!\n", cashier.getId());
        }
        this.cashierRepository.save(cashier);
        return "Successfully added cashier!\n";
    }

    @Override
    public String assignToShop(long cashierId, long shopId) {
        Optional<Cashier> optionalCashier = this.cashierRepository.findById(cashierId);
        if (optionalCashier.isEmpty()) {
            return "Cashier does not exist!\n";
        }
        Optional<Shop> optionalShop = this.shopRepository.findById(shopId);
        if (optionalShop.isEmpty()) {
            return "Shop does not exist!\n";
        }
        Cashier cashier = optionalCashier.get();
        if (cashier.getShop() != null) {
            return String.format("Cashier %s %s is already assigned to shop %s!\n",
                    cashier.getFirstName(), cashier.getLastName(), cashier.getShop().getName());
        }
        Shop shop = optionalShop.get();
        cashier.setShop(shop);
        shop.getCashiers().add(cashier);

        this.cashierRepository.save(cashier);
        this.shopRepository.save(shop);
        return String.format("Successfully assigned cashier %s %s to shop %s!\n",
                cashier.getFirstName(), cashier.getLastName(), shop.getName());
    }

}
