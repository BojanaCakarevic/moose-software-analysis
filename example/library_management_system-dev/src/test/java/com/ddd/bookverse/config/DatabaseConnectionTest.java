package com.ddd.bookverse.config;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {

    public static Connection createConnection(Connection connection) throws SQLException {
        connection.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS books (" +
                        "id VARCHAR(255) PRIMARY KEY, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "authors TEXT, " +
                        "description TEXT, " +
                        "image MEDIUMBLOB, " +
                        "pageCount INT, " +
                        "publishDate VARCHAR(255), " +
                        "publisher VARCHAR(255), " +
                        "categories TEXT, " +
                        "quantityInStock INT, " +
                        "isbn VARCHAR(255), " +
                        "ratingSum BIGINT, " +
                        "ratingCount INT" +
                        ");" +
                        "CREATE TABLE IF NOT EXISTS users (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(255) NOT NULL UNIQUE, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "email VARCHAR(255) NOT NULL UNIQUE, " +
                        "fullName VARCHAR(255), " +
                        "role ENUM('ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER'" +
                        ");" +
                        "CREATE TABLE IF NOT EXISTS borrows (" +
                        "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "userId BIGINT NOT NULL, " +
                        "bookId VARCHAR(255) NOT NULL, " +
                        "borrowDate DATE NOT NULL, " +
                        "returnDate DATE, " +
                        "status TINYTEXT, " +
                        "pagesRead INT DEFAULT 0, " +
                        "FOREIGN KEY (userId) REFERENCES users(id), " +
                        "FOREIGN KEY (bookId) REFERENCES books(id)" +
                        ");" +
                        "CREATE TABLE IF NOT EXISTS reviews (" +
                        "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "bookId VARCHAR(255) NOT NULL, " +
                        "userId BIGINT NOT NULL, " +
                        "content TEXT, " +
                        "rating INT CHECK (rating BETWEEN 1 AND 5), " +
                        "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (bookId) REFERENCES books(id), " +
                        "FOREIGN KEY (userId) REFERENCES users(id)" +
                        ");"
        );
        return connection;
    }
}
