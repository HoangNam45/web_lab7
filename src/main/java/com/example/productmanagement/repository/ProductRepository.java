package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find products by category
    List<Product> findByCategory(String category);
    
    // Find products by name containing keyword (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    // Find products by name containing keyword with pagination
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // Check if product code exists
    boolean existsByProductCode(String productCode);
    
    // Advanced search with multiple criteria
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProducts(@Param("name") String name,
                                @Param("category") String category,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice);
    
    // Statistics queries
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    long countByCategory(@Param("category") String category);
    
    @Query("SELECT SUM(p.price * p.quantity) FROM Product p")
    BigDecimal calculateTotalValue();
    
    @Query("SELECT AVG(p.price) FROM Product p")
    BigDecimal calculateAveragePrice();
    
    @Query("SELECT p FROM Product p WHERE p.quantity < :threshold ORDER BY p.quantity ASC")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
    
    // Find recent products
    List<Product> findTop5ByOrderByCreatedAtDesc();
    
    // Note: Basic CRUD methods are inherited from JpaRepository
    // - save(Product product)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)
    // - count()
    // etc.
}
