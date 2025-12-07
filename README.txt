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

## 9. Sorting Products
**URL:** http://localhost:8081/products?sortBy=name&sortDir=asc

**Flow:**
1. User clicks on a column header (e.g., Name, Price, Quantity) in the product table
2. Browser sends GET request with sortBy and sortDir parameters
   - Example: `/products?sortBy=name&sortDir=asc`
3. Controller receives sortBy (column name) and sortDir (asc/desc) parameters
4. Controller creates Sort object: `Sort.by(sortBy).ascending()` or `Sort.by(sortBy).descending()`
5. Controller calls `productService.getAllProducts(sort)`
6. Service calls `productRepository.findAll(sort)`
7. Repository queries database with ORDER BY clause
8. Returns sorted List<Product>
9. Controller adds products, sortBy, sortDir, and reverseSortDir to Model
10. Returns "product-list" view
11. Thymeleaf renders table with sort indicators (↑/↓ arrows)
12. User sees products sorted by selected column

**Features:**
- Click column header to sort by that column
- Click again to reverse sort direction (asc ↔ desc)
- Visual indicators (↑ for ascending, ↓ for descending)
- Default sort: by ID ascending

**Sortable Columns:**
- ID
- Name
- Price
- Quantity
- Category

**Code Path:**
```
Browser → ProductController.listProducts(sortBy, sortDir)
       → Create Sort object
       → ProductService.getAllProducts(sort)
       → ProductRepository.findAll(sort)
       → Database (SELECT with ORDER BY)
       → product-list.html (with sort indicators)
```

---

## 10. Filter Products by Category
**URL:** http://localhost:8081/products?category=Electronics

**Flow:**
1. User clicks a category button (e.g., Electronics, Clothing, Books)
2. Browser sends GET request with category parameter
   - Example: `/products?category=Electronics`
3. Controller receives category parameter
4. Controller calls `productService.getProductsByCategory(category)`
5. Service calls `productRepository.findByCategory(category)`
6. Repository queries database: `SELECT * FROM product WHERE category = ?`
7. Returns filtered List<Product>
8. Controller adds filtered products and currentCategory to Model
9. Returns "product-list" view
10. Thymeleaf highlights active category button
11. User sees only products from selected category

**Features:**
- Category buttons: All Categories, Electronics, Clothing, Books, Furniture, Food, Sports
- Active category button highlighted in blue
- "All Categories" button shows all products
- Category filter persists when sorting

**Code Path:**
```
Browser → ProductController.listProducts(category)
       → ProductService.getProductsByCategory(category)
       → ProductRepository.findByCategory(category)
       → Database (SELECT with WHERE)
       → product-list.html (with category highlight)
```

---

## 11. Combined Sorting and Filtering
**URL:** http://localhost:8081/products?category=Electronics&sortBy=price&sortDir=desc

**Flow:**
1. User selects a category filter (e.g., Electronics)
2. User clicks a column header to sort (e.g., Price descending)
3. Browser sends GET request with both category and sort parameters
   - Example: `/products?category=Electronics&sortBy=price&sortDir=desc`
4. Controller receives category, sortBy, and sortDir parameters
5. Controller calls `productService.getProductsByCategory(category)`
6. Service returns filtered List<Product> by category
7. Controller applies manual sorting using Java Stream API:
   - Compares products based on sortBy field (name, price, quantity, category)
   - Applies sortDir (ascending or descending)
8. Controller adds sorted and filtered products to Model
9. Returns "product-list" view
10. User sees products filtered by category AND sorted by selected column

**Features:**
- Category buttons preserve current sort order
- Sort links preserve current category filter
- Visual indicators show both active category and sort direction
- Can sort any column while filtering by category

**Example Scenarios:**
- Show all Electronics sorted by price (low to high)
- Show all Books sorted by name (A-Z)
- Show all Clothing sorted by quantity (high to low)

**Code Path:**
```
Browser → ProductController.listProducts(category, sortBy, sortDir)
       → ProductService.getProductsByCategory(category)
       → ProductRepository.findByCategory(category)
       → Database (SELECT with WHERE)
       → Manual sorting in Controller using Stream API
       → product-list.html (with category + sort indicators)
```

**Technical Details:**
- Sorting is applied manually using Java Comparator when filtering by category
- Repository method findByCategory() returns List (not pageable)
- Switch statement handles different sortBy fields (name, price, quantity, category, id)
- Comparator direction reversed for descending order

---

## 12. Statistics Dashboard
**URL:** http://localhost:8081/dashboard

**Flow:**
1. User clicks "Dashboard" button from product list page
2. Browser sends GET request to `/dashboard`
3. Controller calls multiple service methods to gather statistics:
   - `getTotalProductCount()` - Total number of products
   - `countByCategory(category)` - Count products per category
   - `calculateTotalValue()` - Sum of (price × quantity) for all products
   - `calculateAveragePrice()` - Average price across all products
   - `getLowStockProducts(10)` - Products with quantity < 10
   - `getRecentProducts()` - Last 5 products added
4. Service calls corresponding repository methods with @Query annotations
5. Controller adds all statistics to Model
6. Returns "dashboard" view
7. Thymeleaf renders dashboard with statistics cards and tables
8. User sees comprehensive overview of inventory status

**Features:**
- **Statistics Cards**: Display key metrics (total products, total value, average price, low stock count)
- **Products by Category**: Visual breakdown showing product count per category
- **Low Stock Alerts**: Table of products with quantity < 10 (highlighted in red)
- **Recent Products**: Table showing last 5 products added with timestamps

**Repository Queries:**
- `@Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")` - Count by category
- `@Query("SELECT SUM(p.price * p.quantity) FROM Product p")` - Total inventory value
- `@Query("SELECT AVG(p.price) FROM Product p")` - Average product price
- `@Query("SELECT p FROM Product p WHERE p.quantity < :threshold")` - Low stock products
- `findTop5ByOrderByCreatedAtDesc()` - Recent products using Spring Data JPA naming convention

**Code Path:**
```
Browser → DashboardController.showDashboard()
       → Multiple ProductService method calls:
         - getTotalProductCount() → ProductRepository.count()
         - countByCategory() → ProductRepository.countByCategory(@Query)
         - calculateTotalValue() → ProductRepository.calculateTotalValue(@Query)
         - calculateAveragePrice() → ProductRepository.calculateAveragePrice(@Query)
         - getLowStockProducts(10) → ProductRepository.findLowStockProducts(@Query)
         - getRecentProducts() → ProductRepository.findTop5ByOrderByCreatedAtDesc()
       → Database (SELECT with aggregations)
       → dashboard.html (with statistics visualization)
```

**Technical Details:**
- Uses JPQL @Query annotations for custom aggregation queries (COUNT, SUM, AVG)
- Null-safe handling: returns BigDecimal.ZERO if no products exist
- Category statistics dynamically generated for predefined categories
- Low stock threshold configurable (currently 10 items)
- Recent products sorted by createdAt timestamp in descending order
- Dashboard accessible from main product list via green "Dashboard" button

---

