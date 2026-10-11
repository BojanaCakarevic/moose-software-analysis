![banner](/readme_resources/banner3.png)

# **Bookverse: Library Management System**

**Bookverse** is a JavaFX-based library management desktop application designed to streamline library operations, both for administrators and members. It supports essential features like book search, member management, and transaction tracking, along with advanced features such as QR code scanning, book reviews/ratings, and search suggestions

---

## **Features**

### **For Admin Accounts:**
- **Admin Dashboard**:
    - **Overview**: Provides a snapshot of the library’s key metrics, including the number of books, users, and borrows.
    - **Recent Activities**: Displays recent book borrows, returns, and reviews.
    - **Pie chart**: Visualizes the distribution of books by genre.

![Admin dashboard](/readme_resources/admin_dashboard_1.png)

- **Book Management**:
  - **View Books**: Display a list of all books in the library, including information like title, author, and book ID.
  - **Add, Edit, and Delete Books**: Books can be added manually or via external APIs (Google Books API).
  - **Book Details**: View detailed information about a book, including its description, genre, and publication date.
  - **Book Borrows Management**: Manage and modify book borrowing records of users, including dates and status.
  
  
![Book management](/readme_resources/book_management_1.png)
![Book management add book](/readme_resources/book_management_2.png)
![Book management add book api](/readme_resources/book_management_3.png)
![Book details](/readme_resources/book_management_4.png)
![Book borrows management](/readme_resources/book_management_5.png)

- **Account Management**:
  - **View Accounts**: Display a list of all registered accounts.
  - **Add, Edit, and Delete Accounts**: Manage the library’s accounts efficiently.
  - **Account Details**: View detailed information about an account, including their borrows and reviews.
  
![Account management](/readme_resources/account_management_1.png)
![Account update](/readme_resources/account_management_2.png)
![Account details](/readme_resources/account_management_3.png)
![Account delete](/readme_resources/account_management_4.png)

### **For Member Accounts:**
- **Library Access**:
  - **User dashboard**: See a list of books the user intends to read, is currently borrowing, and has returned.
  - **Book list**: View all books in the library in each category.
  - **Borrow and Return Books**: Manage book borrowing and returns seamlessly. Deal with overdue books and fines.
  - **Update Account**: Members can update their personal information, including email, password, and username.

![User dashboard](/readme_resources/user_dashboard_1.png)
![Book list](/readme_resources/book_list_1.png)
![Book borrow](/readme_resources/book_details_1.png)
![Book overdue](/readme_resources/book_details_2.png)

### **Additional Features**:
- **QR Code Integration**: 
  - Each book has a unique QR code for easy identification.
- **Book Reviews and Ratings**:
  - Users can rate books on a scale and leave comments to help future readers.
- **Search Suggestions**:
  - Search suggestions based on title, author, or genre as users type in the search bar.

![QR scanning](/readme_resources/qr_scan_1.png)
![Book review](/readme_resources/book_review_1.png)
![Search suggestions](/readme_resources/search_suggestions_1.png)

For a detailed demonstration of the application, please look into the ```\readme_resources``` folder.

---

## **System Architecture & Design Patterns**

The application follows a modular architecture that ensures maintainability, scalability, and ease of testing. The system is organized into the following key components, each designed to handle specific aspects of the application:

### **Key Packages:**

- **api**: Manages interactions with external APIs, such as retrieving book details.
- **config**: Contains configuration files for database setup.
- **constants**: Defines constant values used across the application, such as error messages and field names.
- **controller**: Facilitates communication between the user interface (UI) and the underlying model. It processes user inputs and updates the UI accordingly.
- **model**: Represents the core data entities, including books, users, reviews and borrows.
- **service**: Encapsulates the business logic, including operations like managing books and users.
- **dao**: Handles database interactions, including CRUD operations for books, users and their activities.
- **exception**: Contains custom exception classes for handling specific errors, such as book not found or API connection failures.
- **item**:  Creates JavaFX UI components for displaying library items.
- **singleton**: Implements the Singleton design pattern to manage unique instances.
- **utils**: Provides utility classes for common operations, such as data validation, animation and QR handling;

### **Design Patterns:**

The following design patterns are used to structure and optimize the application:

- **MVC (Model-View-Controller)**:
   - The MVC pattern separates concerns within the application, ensuring a clear distinction between user interaction, business logic, and data management. The controller processes user input and updates the UI, the service encapsulates the business logic, the model represents core data entities, and the DAO manages database operations.

- **Singleton**:
   - The Singleton pattern ensures that only one instance of the database connection exists throughout the application’s lifecycle. This prevents redundant connections, objects and optimizes system performance.

- **Factory**:
   - The Factory pattern is used to create `User` objects (Admin and Member) based on login credentials. This approach simplifies user management and allows easy extension of user types in the future.

- **DAO (Data Access Object)**:
   - The DAO pattern encapsulates database interaction logic, separating it from the core application. It streamlines CRUD operations (Create, Read, Update, Delete) for entities like books, users, borrows and reviews ensuring clean code organization and maintainability.
  
---

## **Technology Stack**

- **JavaFX**: Used for building the graphical user interface (GUI) of the application.
- **Maven**: For dependency management and building the project.
- **MySQL**: For managing the library's database, storing user data, book records, borrows and reviews.
- **ZXing**: For QR code generation and scanning. ZXing enables quick identification of books via QR codes.
- **JUnit/Mockito**: For unit testing and mocking external dependencies.
- **Ikonli**: Provides rich icons for the JavaFX user interface, enhancing visual appeal.
- **Google API Client**: For integration with external APIs to pull book details or search for books.
  
---

## **Installation**

### **Prerequisites**
- Java 21 or higher
- Maven
- MySQL Database

### **Clone the Repository**
```bash
git clone https://github.com/dinhdat07/library_management_system.git
cd library_management_system
```

### **Build the Project**
```bash
mvn clean install
```

### **Run the Application**
```bash
mvn javafx:run
```

---

## **Database Setup**

1. **Configure MySQL Database**:
   - **Database URL**: `jdbc:mysql://localhost:3306/library_db` (or your custom configuration)
   - **Username**: `root` (or your custom username)
   - **Password**: `12345678` (or your custom password)
   
   Database connection details are dynamically configured using environment variables:
   ```java
   private static final String DB_URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : DATABASE_URL;
   private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : DATABASE_USER;
   private static final String DB_PASS = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : DATABASE_PASSWORD;
   ```

2. **Database Schema**:
   - Create the necessary tables for books, users, borrows, and reviews. The schema and sample data can be found in the `src/main/resources/db/library_db.sql` file.

---

## **Testing**

The application includes unit tests for key components using **JUnit**. Mocking libraries like **Mockito** are used to simulate external dependencies and test the business logic effectively.

To run the tests:
```bash
mvn test
```

---

## **Demo**

During the presentation, we will showcase the following features:

- **Book Search by API**: Searching for books from integrated API.
- **QR Code Scanning**: Scanning a QR code to find a book quickly.
- **Borrow Management**: Checking out and returning books.
- **Book Reviews and Ratings**: Adding and viewing reviews.
- **Search Suggestions**: Showing how the system provides suggestions while typing a search query.

---
## **Contributing**

Feel free to fork the repository, contribute to it, or report any issues you encounter. All contributions are welcome.

---
