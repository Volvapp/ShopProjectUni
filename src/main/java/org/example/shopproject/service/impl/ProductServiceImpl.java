package org.example.shopproject.service.impl;

import org.example.shopproject.model.entity.Product;
import org.example.shopproject.model.entity.Shop;
import org.example.shopproject.model.enums.Category;
import org.example.shopproject.repository.ProductRepository;
import org.example.shopproject.repository.ShopRepository;
import org.example.shopproject.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.example.shopproject.service.ProductService;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private static final double EDIBLE_MARKUP_PERCENT_PRICE = 2.0;
    private static final double NON_EDIBLE_MARKUP_PERCENT_PRICE = 2.5;
    private final ProductRepository productRepository;
    private final ValidationUtil validationUtil;
    private final ShopRepository shopRepository;

    public ProductServiceImpl(ProductRepository productRepository, ValidationUtil validationUtil, ShopRepository shopRepository) {
        this.productRepository = productRepository;
        this.validationUtil = validationUtil;
        this.shopRepository = shopRepository;
    }

    @Override
    public String addProduct(Product product) {
        if (!this.validationUtil.isValid(product)) {
            return "Invalid product!\n";
        }

        Optional<Product> optionalProduct = productRepository.findByName(product.getName());
        if (optionalProduct.isPresent()) {
            return String.format("Product with name %s already exists!\n", product.getName());
        }
        if (product.getPrice() < 0) {
            return "Invalid price!\n";
        }

        if (product.isExpired()) {
            return "Expired product!\n";
        }
        giveClientProductPrice(product);
        this.productRepository.save(product);
        return String.format("Successfully added product %s!\n", product.getName());
    }

    private void giveClientProductPrice(Product product) {
        if (product.getCategory().equals(Category.EDIBLE)) {
            product.setClientPrice(product.getPrice() * EDIBLE_MARKUP_PERCENT_PRICE);
        }
        product.setClientPrice(product.getPrice() * NON_EDIBLE_MARKUP_PERCENT_PRICE);
    }

    @Override
    public String assignToShop(long productId, long shopId) {
        Optional<Product> optionalProduct = this.productRepository.findById(productId);
        Optional<Shop> optionalShop = this.shopRepository.findById(shopId);

        if (optionalProduct.isEmpty()) {
            return String.format("Product with id: %d does not exist!\n", productId);
        }
        if (optionalShop.isEmpty()) {
            return String.format("Shop with id: %d does not exist!\n", shopId);
        }
        Product product = optionalProduct.get();

        checkExpireDate(product);

        if (product.isExpired()) {
            this.productRepository.deleteById(productId);
            return String.format("Product %s is expired!\n", product.getName());
        }
        Shop shop = optionalShop.get();
        Product existingProduct = shop.getBoughtProducts()
                .stream()
                .filter(p -> p.getId() == product.getId())
                .findAny()
                .orElse(null);
        if (existingProduct != null) {
            return String.format("Product %s is already in shop %s!\n", product.getName(), shop.getName());
        }
        shop.getBoughtProducts().add(product);
        product.setShop(shop);

        this.productRepository.save(product);
        this.shopRepository.save(shop);

        return String.format("Product %s successfully added to shop %s!\n", product.getName(), shop.getName());
    }

    private static void checkExpireDate(Product product) {
        if (product.getExpireDate().isBefore(LocalDate.now())) {
            product.setExpired(true);
        }
    }
}
