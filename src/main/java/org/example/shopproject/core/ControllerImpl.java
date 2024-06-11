package org.example.shopproject.core;

import org.example.shopproject.model.entity.*;
import org.example.shopproject.service.*;
import org.springframework.stereotype.Component;

@Component
public class ControllerImpl implements Controller {
    private final ShopService shopService;
    private final CheckoutService checkoutService;
    private final ClientService clientService;
    private final ProductService productService;
    private final ReceiptService receiptService;
    private final CashierService cashierService;

    public ControllerImpl(ShopService shopService, CheckoutService checkoutService, ClientService clientService, ProductService productService, ReceiptService receiptService, CashierService cashierService) {
        this.shopService = shopService;
        this.checkoutService = checkoutService;
        this.clientService = clientService;
        this.productService = productService;
        this.receiptService = receiptService;
        this.cashierService = cashierService;
    }

    @Override
    public String addShop(Shop shop) {
            return this.shopService.addShop(shop);
    }

    @Override
    public String addCheckout(Checkout checkout) {
        return this.checkoutService.addCheckout(checkout);
    }

    @Override
    public String addClient(Client client) {
        return this.clientService.addClient(client);
    }

    @Override
    public String addProduct(Product product) {
        return this.productService.addProduct(product);
    }

    @Override
    public String addCashier(Cashier cashier) {
        return this.cashierService.addCashier(cashier);
    }

    @Override
    public String addReceipt(Receipt receipt) {
        return this.receiptService.addReceipt(receipt);
    }

    @Override
    public String addCashierToCheckout(long cashierId, long checkoutId) {
        return this.checkoutService.addCashierToCheckout(cashierId, checkoutId);
    }

    @Override
    public String assignToShop(String nameOfObject, long objectId, long shopId) {
        String result = "";
        switch (nameOfObject) {
            case "Client":
                result = this.clientService.assignToShop(objectId, shopId);
                break;
            case "Product":
                result = this.productService.assignToShop(objectId, shopId);
                break;
            case "Cashier":
                result = this.cashierService.assignToShop(objectId, shopId);
                break;
            case "Checkout":
                result = this.checkoutService.assignToShop(objectId, shopId);
                break;
            default:
                result = "Wrong type of object!\n";
        }
        return result;
    }

    @Override
    public String addProductToClient(long clientId) {
        return this.clientService.addProductToClient(clientId);
    }

    @Override
    public String goToQueue(long shopId) {
        return this.checkoutService.goToQueue(shopId);
    }

    @Override
    public String buy(long clientId) {
        return this.shopService.buy(clientId);
    }

    @Override
    public String calculateMoney() {
        return this.shopService.calculateMoney();
    }
}
