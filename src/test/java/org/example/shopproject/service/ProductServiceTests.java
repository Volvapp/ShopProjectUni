package org.example.shopproject.service;

import org.example.shopproject.model.entity.Product;
import org.example.shopproject.model.entity.Shop;
import org.example.shopproject.model.enums.Category;
import org.example.shopproject.repository.ProductRepository;
import org.example.shopproject.repository.ShopRepository;
import org.example.shopproject.service.impl.ProductServiceImpl;
import org.example.shopproject.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProduct_InvalidProduct() {
        Product product = new Product();
        when(validationUtil.isValid(product)).thenReturn(false);
        String result = productService.addProduct(product);
        assertEquals("Invalid product!\n", result);
        verify(productRepository, never()).findByName(anyString());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testAddProduct_ProductAlreadyExists() {
        Product product = new Product();
        product.setName("Existing Product");
        when(validationUtil.isValid(product)).thenReturn(true);
        when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));
        String result = productService.addProduct(product);
        assertEquals(String.format("Product with name %s already exists!\n", product.getName()), result);
        verify(productRepository).findByName(product.getName());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testAddProduct_InvalidPrice() {
        Product product = new Product();
        product.setName("New Product");
        product.setPrice(-10.0);
        when(validationUtil.isValid(product)).thenReturn(true);
        when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());
        String result = productService.addProduct(product);
        assertEquals("Invalid price!\n", result);
        verify(productRepository).findByName(product.getName());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testAddProduct_ExpiredProduct() {
        Product product = new Product();
        product.setName("Expired Product");
        product.setPrice(10.0);
        product.setExpired(true);
        when(validationUtil.isValid(product)).thenReturn(true);
        when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());
        String result = productService.addProduct(product);
        assertEquals("Expired product!\n", result);
        verify(productRepository).findByName(product.getName());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testAddProduct_Success() {
        Product product = new Product();
        product.setName("New Product");
        product.setPrice(10.0);
        product.setCategory(Category.EDIBLE);
        product.setExpireDate(LocalDate.now().plusDays(30));
        when(validationUtil.isValid(product)).thenReturn(true);
        when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());
        String result = productService.addProduct(product);
        assertEquals(String.format("Successfully added product %s!\n", product.getName()), result);
        verify(productRepository).findByName(product.getName());
        verify(productRepository).save(product);
    }

    @Test
    void testAssignToShop_ProductNotExist() {
        long productId = 1L;
        long shopId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        String result = productService.assignToShop(productId, shopId);
        assertEquals(String.format("Product with id: %d does not exist!\n", productId), result);
    }

    @Test
    void testAssignToShop_ShopNotExist() {
        long productId = 1L;
        long shopId = 1L;
        Product product = new Product();
        product.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());
        String result = productService.assignToShop(productId, shopId);
        assertEquals(String.format("Shop with id: %d does not exist!\n", shopId), result);
        verify(productRepository).findById(productId);
        verify(shopRepository).findById(shopId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void testAssignToShop_ProductExpired_ShopFound() {
        long productId = 1L;
        long shopId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setExpireDate(LocalDate.now().minusDays(1));
        Shop shop = new Shop();
        shop.setId(shopId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        String result = productService.assignToShop(productId, shopId);

        assertEquals(String.format("Product %s is expired!\n", product.getName()), result);
    }


    @Test
    void testAssignToShop_ProductAlreadyInShop() {
        long productId = 1L;
        long shopId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setExpireDate(LocalDate.now().plusDays(1));
        Shop shop = new Shop();
        shop.setId(shopId);
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        shop.setBoughtProducts(productList);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        String result = productService.assignToShop(productId, shopId);
        assertEquals(String.format("Product %s is already in shop %s!\n", product.getName(), shop.getName()), result);
        verify(productRepository).findById(productId);
        verify(shopRepository).findById(shopId);
        verifyNoMoreInteractions(productRepository, shopRepository);
    }

    @Test
    void testAssignToShop_Success() {
        long productId = 1L;
        long shopId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setExpireDate(LocalDate.now().plusDays(1));
        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setBoughtProducts(new ArrayList<>());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        String result = productService.assignToShop(productId, shopId);
        assertEquals(String.format("Product %s successfully added to shop %s!\n", product.getName(), shop.getName()), result);
        verify(productRepository).findById(productId);
        verify(shopRepository).findById(shopId);
        verify(productRepository).save(product);
        verify(shopRepository).save(shop);
    }
}
