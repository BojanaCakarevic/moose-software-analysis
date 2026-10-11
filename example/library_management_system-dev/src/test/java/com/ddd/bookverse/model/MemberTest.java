package com.ddd.bookverse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MemberTest {

    private Member member;

    @BeforeEach
    public void setUp() {
        // Initialize the Member object before each test
        member = new Member("memberUser", "password123", "member@example.com", "Member User");
    }

    @Test
    public void testConstructorAndGetters() {

        assertEquals("memberUser", member.getUsername(), "Username should be initialized correctly.");
        assertEquals("password123", member.getPassword(), "Password should be initialized correctly.");
        assertEquals("member@example.com", member.getEmail(), "Email should be initialized correctly.");
        assertEquals("Member User", member.getFullName(), "Full name should be initialized correctly.");
    }

    @Test
    public void testGetRole() {
        // Test getRole method for Member
        assertEquals("MEMBER", member.getRole(), "Role of the Member should be 'MEMBER'");
    }

    @Test
    public void testSetters() {
        // Test setters and verify the changes
        member.setUsername("updatedMember");
        member.setPassword("newPassword123");
        member.setEmail("updatedMember@example.com");
        member.setFullName("Updated Member User");

        assertEquals("updatedMember", member.getUsername(), "Username should be updated.");
        assertEquals("newPassword123", member.getPassword(), "Password should be updated.");
        assertEquals("updatedMember@example.com", member.getEmail(), "Email should be updated.");
        assertEquals("Updated Member User", member.getFullName(), "Full name should be updated.");
    }
}
