package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find products by category
    List<Product> findByCategory(String category);
    
    // Find products by name containing keyword (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    // Check if product code exists
    boolean existsByProductCode(String productCode);
    
    // Note: Basic CRUD methods are inherited from JpaRepository
    // - save(Product product)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)
    // - count()
    // etc.
}
