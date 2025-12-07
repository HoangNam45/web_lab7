package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    List<Product> getAllProducts();
    
    // Get all products with sorting
    List<Product> getAllProducts(Sort sort);
    
    Optional<Product> getProductById(Long id);
    
    Product saveProduct(Product product);
    
    void deleteProduct(Long id);
    
    List<Product> searchProducts(String keyword);
    
    // Search products with pagination
    Page<Product> searchProducts(String keyword, Pageable pageable);
    
    List<Product> getProductsByCategory(String category);
    
    // Advanced search with multiple criteria
    List<Product> advancedSearch(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Statistics methods
    long getTotalProductCount();
    
    long countByCategory(String category);
    
    BigDecimal calculateTotalValue();
    
    BigDecimal calculateAveragePrice();
    
    List<Product> getLowStockProducts(int threshold);
    
    List<Product> getRecentProducts();
}
