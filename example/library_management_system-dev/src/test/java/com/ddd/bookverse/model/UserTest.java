package com.ddd.bookverse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User memberUser;
    private User adminUser;

    @BeforeEach
    public void setUp() {
        // Initialize concrete User objects before each test
        memberUser = new Member("memberUser", "password123", "member@example.com", "Member User");
        adminUser = new Admin("adminUser", "adminPass", "admin@example.com", "Admin User");
    }

    @Test
    public void testUserConstructor() {
        // Test Member User constructor
        assertNotNull(memberUser, "Member user should be created successfully");
        assertEquals("memberUser", memberUser.getUsername(), "Member username should be 'memberUser'");
        assertEquals("password123", memberUser.getPassword(), "Member password should be 'password123'");
        assertEquals("member@example.com", memberUser.getEmail(), "Member email should be 'member@example.com'");
        assertEquals("Member User", memberUser.getFullName(), "Member full name should be 'Member User'");

        // Test Admin User constructor
        assertNotNull(adminUser, "Admin user should be created successfully");
        assertEquals("adminUser", adminUser.getUsername(), "Admin username should be 'adminUser'");
        assertEquals("adminPass", adminUser.getPassword(), "Admin password should be 'adminPass'");
        assertEquals("admin@example.com", adminUser.getEmail(), "Admin email should be 'admin@example.com'");
        assertEquals("Admin User", adminUser.getFullName(), "Admin full name should be 'Admin User'");
    }

    @Test
    public void testGetRole() {
        // Test getRole method for Member
        assertEquals("MEMBER", memberUser.getRole(), "Member should have role 'MEMBER'");

        // Test getRole method for Admin
        assertEquals("ADMIN", adminUser.getRole(), "Admin should have role 'ADMIN'");
    }

    @Test
    public void testGettersAndSetters() {
        // Test setters and getters
        memberUser.setUsername("updatedMember");
        memberUser.setPassword("newPassword123");
        memberUser.setEmail("updatedMember@example.com");
        memberUser.setFullName("Updated Member User");

        assertEquals("updatedMember", memberUser.getUsername());
        assertEquals("newPassword123", memberUser.getPassword());
        assertEquals("updatedMember@example.com", memberUser.getEmail());
        assertEquals("Updated Member User", memberUser.getFullName());
    }

    @Test
    public void testAbstractRole() {
        // Verify that calling getRole on an instance of Member and Admin returns the correct role
        assertEquals("MEMBER", memberUser.getRole());
        assertEquals("ADMIN", adminUser.getRole());
    }
}
