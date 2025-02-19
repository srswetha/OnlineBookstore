# 📚 OneWorld Bookstore

**OneWorld Bookstore** is a full-stack web application that **mimics a real-world online bookstore**, allowing users to browse books by categories, add them to a shopping cart, and securely complete purchases.  

This project is built using **modern web technologies**, integrating a **Vue.js** frontend, a **Java Spring Boot / Jersey API** backend, and **MySQL** for data storage. It follows best practices in **REST API development, state management, and transactional database operations** to ensure a **smooth, efficient, and secure shopping experience**.

---

## 🔹 Key Functionalities

✅ **Book & Category Management:** Users can browse books organized by categories, fetched dynamically from a RESTful API.  
✅ **Shopping Cart System:** Users can add books to their cart, update quantities, and proceed to checkout.  
✅ **State Management with Pinia:** Ensures a **reactive UI** by managing categories, books, and cart items efficiently.  
✅ **Checkout & Order Processing:** Implements a **secure** checkout process with form validation.  
✅ **Transaction Handling:** Uses **database-backed transactions** to ensure order integrity, preventing data inconsistencies.  
✅ **Error Handling & Edge Cases:** Covers **404 errors, duplicate orders**, and invalid inputs to provide a **seamless user experience**.  
✅ **Local & Session Storage:** Cart data persists across page reloads; order details persist within a session for user convenience.  
✅ **Security Enhancements:** Implements **input sanitization, validation rules, and transaction rollbacks** to prevent **data corruption and unauthorized access**.  

---

## 🔹 Why This Project Matters

In the real world, an e-commerce platform must be:

- **Highly Responsive:** Ensuring fast updates to book listings and cart interactions.
- **Secure:** Protecting sensitive customer information and transactions.
- **Reliable:** Handling failures gracefully, preventing order duplication, and ensuring consistency in database transactions.
- **Scalable:** Designed to be extended with features like **user authentication, admin dashboards, and payment gateway integration**.

**OneWorld Bookstore** lays the foundation for a **robust** e-commerce system by implementing **modern development principles and best practices**.

---

## 🛠️ **Tech Stack**
### **Frontend**
🚀 Vue 3 + TypeScript  
📌 Pinia (State Management)  
🛠️ Vue Router (Navigation)  
📝 Vuelidate (Form Validation)  
🎨 Tailwind CSS / Custom Styling  

### **Backend**
📗 Java (Spring Boot / Jersey API)  
🛢 JDBC (Database Connectivity)  
🗄 MySQL / PostgreSQL  
🔥 Tomcat (Server)  

### **Tools & Dependencies**
📦 Node.js & NPM  
💻 IntelliJ IDEA / VS Code  
🔗 REST API (JSON Processing)  
🔍 GitHub for Version Control  


---

## 🎯 **Future Improvements**
🔹 User Authentication – Allow users to log in and track their orders.
🔹 Admin Panel – Manage books, categories, and orders.
🔹 Payment Gateway – Integrate Stripe/PayPal for real transactions.
🔹 Mobile Optimization – Improve responsiveness for smaller screens.
