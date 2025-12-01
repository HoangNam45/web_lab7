package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String listProducts(Model model) {
        // 1. Get all products from service
        // 2. Add to model
        // 3. Return "product-list"
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
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
public String saveProduct(@ModelAttribute("product") Product product, RedirectAttributes redirectAttributes) {
    // 1. Save product using service
    productService.saveProduct(product);
    
    // 2. Add success message
    redirectAttributes.addFlashAttribute("success", "Product saved successfully!");
    
    // 3. Redirect to list
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

// Search products - GET /products/search
@GetMapping("/search")
public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
    // 1. Search products from service
    List<Product> products = productService.searchProducts(keyword);
    
    // 2. Add results and keyword to model
    model.addAttribute("products", products);
    model.addAttribute("keyword", keyword);
    
    // 3. Return "product-list"
    return "product-list";
}
}
