package com.ddd.bookverse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BorrowTest {

    private Borrow borrow;
    private User user;
    private Book book;

    @BeforeEach
    public void setUp() {
        // Create a mock User and Book
        user = new Member("memberUser", "password123", "member@example.com", "Member User");
        book = new Book("1", "Test Book", new String[]{"Author 1"});

        // Create a Borrow instance
        borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.of(2024, 11, 1));
        borrow.setReturnDate(LocalDate.of(2024, 12, 1));
        borrow.setStatus("Borrowing");
        borrow.setPagesRead(50);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(user, borrow.getUser(), "User should be initialized correctly.");
        assertEquals(book, borrow.getBook(), "Book should be initialized correctly.");
        assertEquals(LocalDate.of(2024, 11, 1), borrow.getBorrowDate(), "Borrow date should be initialized correctly.");
        assertEquals(LocalDate.of(2024, 12, 1), borrow.getReturnDate(), "Return date should be initialized correctly.");
        assertEquals("Borrowing", borrow.getStatus(), "Status should be initialized correctly.");
        assertEquals(50, borrow.getPagesRead(), "Pages read should be initialized correctly.");
    }

    @Test
    public void testSetters() {
        // Test the setters for the Borrow class
        borrow.setStatus("Returned");
        borrow.setPagesRead(100);

        assertEquals("Returned", borrow.getStatus(), "Status should be updated correctly.");
        assertEquals(100, borrow.getPagesRead(), "Pages read should be updated correctly.");
    }

    @Test
    public void testToString() {
        // Test the toString method
        String expectedToString = "Borrow{" +
                "id=null" +
                ", user=memberUser" +
                ", book=Test Book" +
                ", borrowDate=2024-11-01" +
                ", returnDate=2024-12-01" +
                ", status='Borrowing'" +
                ", pagesRead=50" +
                '}';
        assertEquals(expectedToString, borrow.toString(), "toString method should return the correct string representation.");
    }

    @Test
    public void testCopyConstructor() {
        // Test the copy constructor
        Borrow copiedBorrow = new Borrow(borrow);

        assertNotNull(copiedBorrow, "Copy constructor should create a non-null Borrow object.");
        assertEquals(borrow.getId(), copiedBorrow.getId(), "ID should be the same.");
        assertEquals(borrow.getUser(), copiedBorrow.getUser(), "User should be the same.");
        assertEquals(borrow.getBook(), copiedBorrow.getBook(), "Book should be the same.");
        assertEquals(borrow.getBorrowDate(), copiedBorrow.getBorrowDate(), "Borrow date should be the same.");
        assertEquals(borrow.getReturnDate(), copiedBorrow.getReturnDate(), "Return date should be the same.");
        assertEquals(borrow.getStatus(), copiedBorrow.getStatus(), "Status should be the same.");
        assertEquals(borrow.getPagesRead(), copiedBorrow.getPagesRead(), "Pages read should be the same.");
    }
}
