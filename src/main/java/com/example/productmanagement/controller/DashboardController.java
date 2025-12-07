package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String showDashboard(Model model) {
        // Total products count
        long totalProducts = productService.getTotalProductCount();
        model.addAttribute("totalProducts", totalProducts);
        
        // Products by category
        String[] categories = {"Electronics", "Clothing", "Books", "Furniture", "Food", "Sports"};
        Map<String, Long> categoryStats = new HashMap<>();
        for (String category : categories) {
            long count = productService.countByCategory(category);
            if (count > 0) {
                categoryStats.put(category, count);
            }
        }
        model.addAttribute("categoryStats", categoryStats);
        
        // Total inventory value
        BigDecimal totalValue = productService.calculateTotalValue();
        model.addAttribute("totalValue", totalValue);
        
        // Average product price
        BigDecimal averagePrice = productService.calculateAveragePrice();
        model.addAttribute("averagePrice", averagePrice);
        
        // Low stock products (quantity < 10)
        List<Product> lowStockProducts = productService.getLowStockProducts(10);
        model.addAttribute("lowStockProducts", lowStockProducts);
        
        // Recent products (last 5 added)
        List<Product> recentProducts = productService.getRecentProducts();
        model.addAttribute("recentProducts", recentProducts);
        
        return "dashboard";
    }
}
