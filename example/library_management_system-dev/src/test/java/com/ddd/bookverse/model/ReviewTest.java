package com.ddd.bookverse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest {

    private Review review;

    @BeforeEach
    public void setUp() {
        // Initialize a Review object before each test
        review = new Review("book123", 1L, "Great book!", 5);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("book123", review.getBookId(), "Book ID should be initialized correctly.");
        assertEquals(1L, review.getUserId(), "User ID should be initialized correctly.");
        assertEquals("Great book!", review.getContent(), "Content should be initialized correctly.");
        assertEquals(5, review.getRating(), "Rating should be initialized correctly.");
    }

    @Test
    public void testSetters() {
        // Test the setters for Review class
        review.setBookId("book456");
        review.setUserId(2L);
        review.setContent("Not great, too boring.");
        review.setRating(2);

        assertEquals("book456", review.getBookId(), "Book ID should be updated correctly.");
        assertEquals(2L, review.getUserId(), "User ID should be updated correctly.");
        assertEquals("Not great, too boring.", review.getContent(), "Content should be updated correctly.");
        assertEquals(2, review.getRating(), "Rating should be updated correctly.");
    }

}
