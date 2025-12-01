# Product Management Application - Flow Documentation

## 1. View All Products (Product List Page)
**URL:** http://localhost:8081/products

**Flow:**
1. User accesses `/products`
2. Browser sends GET request to ProductController
3. Controller calls `productService.getAllProducts()`
4. Service calls `productRepository.findAll()`
5. Repository queries database and returns List<Product>
6. Controller adds products to Model
7. Returns "product-list" view
8. Thymeleaf renders product-list.html with product data
9. User sees table with all products

**Code Path:**
```
Browser → ProductController.listProducts() 
       → ProductService.getAllProducts() 
       → ProductRepository.findAll() 
       → Database
       → product-list.html
```

---

## 2. Search Products
**URL:** http://localhost:8081/products/search?keyword=laptop

**Flow:**
1. User types keyword in search box and clicks Search button
2. Browser sends GET request to `/products/search?keyword=laptop`
3. Controller receives keyword parameter
4. Controller calls `productService.searchProducts(keyword)`
5. Service calls `productRepository.findByNameContainingIgnoreCase(keyword)`
6. Repository searches database for products with name containing keyword
7. Returns filtered List<Product>
8. Controller adds filtered products and keyword to Model
9. Returns "product-list" view
10. User sees only matching products

**Code Path:**
```
Browser → ProductController.searchProducts(keyword) 
       → ProductService.searchProducts(keyword)
       → ProductRepository.findByNameContainingIgnoreCase(keyword)
       → Database
       → product-list.html (with filtered results)
```

---

## 3. Add New Product
**URL:** http://localhost:8081/products/new

**Flow - Part 1 (Show Form):**
1. User clicks "Add New Product" button
2. Browser sends GET request to `/products/new`
3. Controller creates empty Product object
4. Controller adds empty product to Model
5. Returns "product-form" view
6. Thymeleaf renders product-form.html
7. User sees empty form with input fields

**Flow - Part 2 (Submit Form):**
1. User fills in product details (code, name, price, quantity, category, description)
2. User clicks Save button
3. Browser sends POST request to `/products/save` with form data
4. Controller receives Product object with form data
5. Controller calls `productService.saveProduct(product)`
6. Service calls `productRepository.save(product)`
7. Repository inserts new product into database
8. @PrePersist sets createdAt timestamp automatically
9. Controller adds success message
10. Redirects to `/products`
11. User sees updated product list with new product

**Code Path:**
```
Show Form:
Browser → ProductController.showNewForm() 
       → product-form.html (empty form)

Submit Form:
Browser → ProductController.saveProduct(product)
       → ProductService.saveProduct(product)
       → ProductRepository.save(product)
       → Database (INSERT)
       → Redirect to /products
```

---

## 4. Edit Product
**URL:** http://localhost:8081/products/edit/{id}

**Flow - Part 1 (Show Edit Form):**
1. User clicks "Edit" button next to a product in the list
2. Browser sends GET request to `/products/edit/1` (where 1 is the product ID)
3. Controller receives id from URL path
4. Controller calls `productService.getProductById(id)`
5. Service calls `productRepository.findById(id)`
6. Repository queries database and returns Optional<Product>
7. If product exists:
   - Controller adds product to Model
   - Returns "product-form" view
   - User sees form pre-filled with existing product data
8. If product not found:
   - Controller adds error message
   - Redirects to `/products`

**Flow - Part 2 (Submit Changes):**
1. User modifies product details in the form
2. User clicks Save button
3. Browser sends POST request to `/products/save` with updated data
4. Controller receives Product object with id and updated data
5. Controller calls `productService.saveProduct(product)`
6. Service calls `productRepository.save(product)`
7. Repository updates existing product in database (because id exists)
8. Controller adds success message
9. Redirects to `/products`
10. User sees updated product list

**Code Path:**
```
Show Edit Form:
Browser → ProductController.showEditForm(id)
       → ProductService.getProductById(id)
       → ProductRepository.findById(id)
       → Database (SELECT)
       → product-form.html (pre-filled form)

Submit Changes:
Browser → ProductController.saveProduct(product)
       → ProductService.saveProduct(product)
       → ProductRepository.save(product)
       → Database (UPDATE)
       → Redirect to /products
```

---

## 5. Delete Product
**URL:** http://localhost:8081/products/delete/{id}

**Flow:**
1. User clicks "Delete" button next to a product in the list
2. Browser sends GET request to `/products/delete/1` (where 1 is the product ID)
3. Controller receives id from URL path
4. Controller calls `productService.deleteProduct(id)`
5. Service calls `productRepository.deleteById(id)`
6. Repository deletes product from database
7. Controller adds success message "Product deleted successfully!"
8. Redirects to `/products`
9. User sees updated product list without the deleted product

**Code Path:**
```
Browser → ProductController.deleteProduct(id)
       → ProductService.deleteProduct(id)
       → ProductRepository.deleteById(id)
       → Database (DELETE)
       → Redirect to /products
```

---

