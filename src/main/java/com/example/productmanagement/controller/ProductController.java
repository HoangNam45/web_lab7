package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// TODO: Add @Controller annotation
// TODO: Add @RequestMapping("/products")
@Controller
@RequestMapping("/products")
public class ProductController {
    
    // TODO: Inject ProductService
    private final ProductService productService;
    
    // TODO: Create constructor with @Autowired
    @Autowired
    public ProductController(ProductService productService){
        this.productService=productService;
    }
    
    // TODO: List all products - GET /products
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        List<Product> products;
        
        // Check if sorting is requested
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort sort = sortDir.equals("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
            
            // Filter by category if provided
            if (category != null && !category.isEmpty()) {
                products = productService.getProductsByCategory(category);
                // Apply sorting manually since findByCategory returns List
                products = products.stream()
                        .sorted((p1, p2) -> {
                            int comparison = 0;
                            switch (sortBy) {
                                case "name":
                                    comparison = p1.getName().compareTo(p2.getName());
                                    break;
                                case "price":
                                    comparison = p1.getPrice().compareTo(p2.getPrice());
                                    break;
                                case "quantity":
                                    comparison = Integer.compare(p1.getQuantity(), p2.getQuantity());
                                    break;
                                case "category":
                                    comparison = p1.getCategory().compareTo(p2.getCategory());
                                    break;
                                default:
                                    comparison = p1.getId().compareTo(p2.getId());
                            }
                            return sortDir.equalsIgnoreCase("desc") ? -comparison : comparison;
                        })
                        .toList();
            } else {
                products = productService.getAllProducts(sort);
            }
        } else {
            // No sorting requested
            if (category != null && !category.isEmpty()) {
                products = productService.getProductsByCategory(category);
            } else {
                products = productService.getAllProducts();
            }
        }
        
        model.addAttribute("products", products);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("category", category);
        
        return "product-list";
    }
    
    // TODO: Show new product form - GET /products/new
    @GetMapping("/new")
    public String showNewForm(Model model) {
         // 1. Create empty Product object
    Product product = new Product();
    
    // 2. Add to model
    model.addAttribute("product", product);
    
    // 3. Return "product-form"
    return "product-form";
    }
    
    // TODO: Show edit form - GET /products/edit/{id}
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    // 1. Get product by id from service
        Optional<Product> product = productService.getProductById(id);
    // 2. If found, add to model and return "product-form"
    if (product.isPresent()) {
        model.addAttribute("product", product.get());
        return "product-form";
    }
    // 3. If not found, add error message and redirect to list
    redirectAttributes.addFlashAttribute("error", "Product not found with ID: " + id);
    return "redirect:/products";
}

    
    // Save product - POST /products/save
@PostMapping("/save")
public String saveProduct(
    @Valid @ModelAttribute("product") Product product,
    BindingResult result,
    Model model,
    RedirectAttributes redirectAttributes) {
    
    // Check for validation errors
    if (result.hasErrors()) {
        return "product-form";
    }
    
    try {
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("message", "Product saved successfully!");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
    }
    
    return "redirect:/products";
}

// Delete product - GET /products/delete/{id}
@GetMapping("/delete/{id}")
public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    // 1. Delete product using service
    productService.deleteProduct(id);
    
    // 2. Add success message
    redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
    
    // 3. Redirect to list
    return "redirect:/products";
}

// Search products - GET /products/search (with pagination)
@GetMapping("/search")
public String searchProducts(
    @RequestParam("keyword") String keyword,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "1") int size,
    Model model) {
    
    // Create pageable object
    Pageable pageable = PageRequest.of(page, size);
    
    // Search products with pagination
    Page<Product> productPage = productService.searchProducts(keyword, pageable);
    
    // Add results and pagination info to model
    model.addAttribute("products", productPage.getContent());
    model.addAttribute("keyword", keyword);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productPage.getTotalPages());
    model.addAttribute("totalItems", productPage.getTotalElements());
    
    return "product-list";
}

// Advanced search - GET /products/advanced-search
@GetMapping("/advanced-search")
public String advancedSearch(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String category,
    @RequestParam(required = false) BigDecimal minPrice,
    @RequestParam(required = false) BigDecimal maxPrice,
    Model model) {
    
    // Convert empty strings to null for proper query handling
    String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
    String searchCategory = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
    
    // Search products with multiple criteria
    List<Product> products = productService.advancedSearch(searchName, searchCategory, minPrice, maxPrice);
    
    // Add results and search parameters to model
    model.addAttribute("products", products);
    model.addAttribute("searchName", searchName);
    model.addAttribute("searchCategory", searchCategory);
    model.addAttribute("searchMinPrice", minPrice);
    model.addAttribute("searchMaxPrice", maxPrice);
    
    return "product-list";
}
}
