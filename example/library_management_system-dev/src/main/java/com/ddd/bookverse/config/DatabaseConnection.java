package com.ddd.bookverse.config;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import static com.ddd.bookverse.constants.Constants.*;

/**
 * This class provides a method to establish a connection to the database.
 */
public class DatabaseConnection {
    // Database URL, user, and password are retrieved from environment variables or constants.
    private static final String DB_URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : DATABASE_URL;
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : DATABASE_USER;
    private static final String DB_PASS = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : DATABASE_PASSWORD;

    /**
     * Establishes a connection to the database using the provided URL, user, and password.
     *
     * @return A Connection object to the database.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}