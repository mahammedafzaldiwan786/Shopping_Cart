# 🛒 Shopping Cart Web Application

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/) 
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot) 
  <img src="https://img.shields.io/badge/Spring%20MVC-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Framework-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white"/>
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"/>
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com/)

A **full-stack E-commerce Shopping Cart** web application built with **Spring Boot**, **Thymeleaf**, and **Spring Data JPA**.  
Designed with **MVC architecture**, this project allows users to browse products, filter by category, add items to a cart, and manage products/categories through an admin panel.

---

## 🔧 Features

### 🧑‍💻 User Features
- User registration, authentication, and session management
- Browse products with category filters
- Search products by name
- Add, remove, and update items in the shopping cart
- View detailed product information

### 🔧 Admin Features
- CRUD operations for products and categories
- Upload product and category images
- Manage stock, visibility, and product details
- Admin dashboard for monitoring products and categories

---

## 🏗️ Tech Stack

| Layer          | Technology |
|----------------|------------|
| Backend        | Java · Spring Boot · Hibernet ORM |
| MVC Framework  | Spring Web MVC |
| Templating     | Thymeleaf |
| Persistence    | Spring Data JPA|
| Database       | MySQL |
| Frontend       | HTML · CSS · Bootstrap 5 |
| Build & Dev    | Maven, Spring Boot DevTools |

---
## 🗂️ Project Structure
Shopping_Cart/<br>
├─ src/<br>
│ ├─ main/<br>
│ │ ├─ java/<br>
│ │ │ ├─ controller/ # Controllers for User & Admin<br>
│ │ │ ├─ model/ # Entities: Product, Category, User<br>
│ │ │ ├─ repository/ # Spring Data JPA Repositories<br>
│ │ │ ├─ service/ # Business Logic / Service Layer<br>
│ │ │ └─ util/ # Utility classes (file upload, helpers)<br>
│ │ ├─ resources/<br>
│ │ │ ├─ static/ # CSS, JS, Images<br>
│ │ │ ├─ templates/ # Thymeleaf HTML views<br>
│ │ │ └─ application.properties<br>
├─ pom.xml<br>
└─ README.md<br>

---

## 🚀 Getting Started

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/mahammedafzaldiwan786/Shopping_Cart.git
cd Shopping_Cart
```
2️⃣ Configure Database

Update application.properties in src/main/resources:
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/shopping_cart_db
spring.datasource.username=<YOUR_DB_USER>
spring.datasource.password=<YOUR_DB_PASSWORD>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
3️⃣ Run Application<br>
Using Maven:
```bash
mvn spring-boot:run
```
Or Build a JAR:
```bash
mvn clean package
java -jar target/Shopping_Cart-0.0.1-SNAPSHOT.jar
```
🧩 Application Flow
---
<ul>
<li>Landing Page: Displays homepage with categories and featured products.</li>

<li>Category Navigation: Click a category → filtered product list.</li>

<li>Product Search: Search bar filters products by keyword.</li>

<li>Shopping Cart: Add, update, remove products.</li>

<li>Admin Panel: Admin can manage products, categories, and stock.</li>
</ul>

🎨 UI / UX Highlights
---
<ul>
<li>Fully responsive design using Bootstrap 5</li>

<li>Dark mode support with custom CSS</li>

<li>Smooth hover effects on cards and buttons</li>

<li>Category sidebar with active selection highlight</li>

<li>Clean product detail view with image preview</li>
</ul>

<!--
🖼️ Screenshots
---

![alt text](image.png)
![alt text](image-3.png)
![alt text](image-1.png)
![alt text](image-2.png)
-->

🔗 Important URLs
---
| Feature          | URL |
|----------------|------------|
| Home Page        | / |
| Products List  | /products |
| Product Details     | /product/{id} |
| Admin Categories    | /admin/category |
| Admin Products       | /admin/products |








⚙️ Recommended Enhancements
---

<ul>
<li>Implement checkout workflow with order history</li>

<li>Integrate Spring Security for role-based authentication</li>

<li>Add product ratings and reviews</li>

<li>Pagination and sorting on product lists</li>

<li>API documentation using Swagger / OpenAPI</li>
</ul>

📫 Contact
---
<ul>
<li>Author : Mahammedafzal Diwan</li>
 <li>Portfolio : https://mahammedafzal-diwan-portfolio.vercel.app/</li> 
<li>Linkedin : https://www.linkedin.com/in/mahammedafzal-diwan-31b450236/</li>
<li>GitHub : https://github.com/mahammedafzaldiwan786</li>
<li>Email : mahammedafzaldiwan786@gmail.com</li>
</ul>




