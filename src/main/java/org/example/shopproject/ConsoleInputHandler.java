package org.example.shopproject;

import org.example.shopproject.core.Controller;
import org.example.shopproject.model.entity.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Scanner;

@Component
public class ConsoleInputHandler implements CommandLineRunner {
    private final Controller controller;


    public ConsoleInputHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String[] tokens = scanner.nextLine().split(" ");

        while (!tokens[0].equals("Exit")) {
            //tokens[0] -> command
            String result;
            try {
                switch (tokens[0]) {
                    case "AddProduct":
                        // tokens[1] -> name, tokens[2] -> price, tokens[3] -> (EDIBLE or NON_EDIBLE),
                        // tokens[4] -> date in format (YYYY-MM-DD), tokens[5] -> quantity
                        boolean isExpired = LocalDate.now().isAfter(LocalDate.parse(tokens[4]));

                        result = this.controller.addProduct(new Product(tokens[1], Double.parseDouble(tokens[2]),
                                0, tokens[3], LocalDate.parse(tokens[4]),
                                Integer.parseInt(tokens[5]), isExpired, null));

                        break;

                    case "AddCheckout":
                        // Adds a checkout (Shop is assigned later)
                        result = this.controller.addCheckout(new Checkout(0, null, null));
                        break;

                    case "AddCashier":
                        // tokens[1] -> firstName, tokens[2] -> lastName, tokens[3] -> salary
                        result = this.controller.addCashier(new Cashier(tokens[1], tokens[2],
                                Double.parseDouble(tokens[3]), null, null));
                        break;

                    case "AddClient":
                        // tokens[1] -> name, tokens[2] -> money
                        result = this.controller.addClient(new Client(tokens[1],
                                Double.parseDouble(tokens[2]), null, null));
                        break;

                    case "AddShop":
                        // tokens[1] -> name
                        result = this.controller.addShop(new Shop(tokens[1]));
                        break;

                    case "AssignToShop":
                        // tokens[1] -> nameOfObject(Client, Cashier, Checkout, Product),
                        // tokens[2] -> objectID, tokens[3] -> shopID
                        result = this.controller.assignToShop(tokens[1], Long.parseLong(tokens[2]), Long.parseLong(tokens[3]));
                        break;

                    case "AddCashierToCheckout":
                        // tokens[1] -> cashierId, tokens[2] -> checkoutId
                        // Assigns a cashier to a checkout
                        result = this.controller.addCashierToCheckout(Long.parseLong(tokens[1]), Long.parseLong(tokens[2]));
                        break;

                    case "AddProductToClient":
                        // tokens[1] -> clientID
                        // Adds a random product from the shop
                        result = this.controller.addProductToClient(Long.parseLong(tokens[1]));
                        break;

                    case "GoToQueue":
                        // tokens[1] -> shopID
                        // assigns all clients with existing product/s to the checkouts
                        // a client must go to a queue before the "Buy" case
                        result = this.controller.goToQueue(Long.parseLong(tokens[1]));
                        break;

                    case "Buy":
                        // tokens[1] -> shopId
                        // note: after every call all clients are unassigned from the shop and the checkout
                        // a .txt file is created in src/main/resources/generatedReceipts if products are bought
                        result = this.controller.buy(Long.parseLong(tokens[1]));
                        break;
                    case "CalculateMoney":
                        // returns all earnings and expenses for every shop in the database
                        result = this.controller.calculateMoney();
                        break;
                    default:
                        result = "Invalid input!\n";
                        break;
                }
            } catch (Exception e) {
                result = e.getMessage() + "\n";
            }
            System.out.print(result);
            tokens = scanner.nextLine().split(" ");
        }
    }
}
