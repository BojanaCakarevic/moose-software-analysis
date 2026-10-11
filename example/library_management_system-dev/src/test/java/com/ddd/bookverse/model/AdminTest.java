package com.ddd.bookverse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    private Admin admin;

    @BeforeEach
    public void setUp() {
        // Set up a new Admin object before each test
        admin = new Admin("adminUser", "password123", "admin@example.com", "Admin User");
    }

    @Test
    public void testAdminConstructor() {
        assertNotNull(admin, "Admin object should be created successfully");
        assertEquals("adminUser", admin.getUsername(), "Admin username should be 'adminUser'");
        assertEquals("password123", admin.getPassword(), "Admin password should be 'password123'");
        assertEquals("admin@example.com", admin.getEmail(), "Admin email should be 'admin@example.com'");
        assertEquals("Admin User", admin.getFullName(), "Admin full name should be 'Admin User'");
    }

    @Test
    public void testGetRole() {
        String role = admin.getRole();
        assertEquals("ADMIN", role, "Admin role should be 'adminRole'");  // Assuming the role is stored in adminRole
    }

    @Test
    public void testInheritedUserProperties() {
        // Check if Admin inherits User's properties correctly
        assertEquals("adminUser", admin.getUsername(), "Admin should inherit 'username' property from User");
        assertEquals("password123", admin.getPassword(), "Admin should inherit 'password' property from User");
        assertEquals("admin@example.com", admin.getEmail(), "Admin should inherit 'email' property from User");
        assertEquals("Admin User", admin.getFullName(), "Admin should inherit 'fullName' property from User");
    }

    @Test
    public void testDefaultConstructor() {
        Admin defaultAdmin = new Admin();
        assertNotNull(defaultAdmin, "Default Admin object should be created successfully");
        assertNull(defaultAdmin.getUsername(), "Username should be null for default Admin");
        assertNull(defaultAdmin.getPassword(), "Password should be null for default Admin");
        assertNull(defaultAdmin.getEmail(), "Email should be null for default Admin");
        assertNull(defaultAdmin.getFullName(), "Full name should be null for default Admin");
    }
}
