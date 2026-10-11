package com.ddd.bookverse.service;

import com.ddd.bookverse.dao.ReviewDAO;
import com.ddd.bookverse.exception.ReviewNotFoundException;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.model.Review;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {
    private ReviewService reviewService;
    @Mock
    private ReviewDAO reviewDAO;
    @Mock
    private BookService bookService;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reviewService = new ReviewService(reviewDAO,bookService);
    }

    @Test
    public void testAddReview() {
        Review review = new Review();
        review.setBookId("NWDswEACAAJ");
        review.setUserId(1L);
        review.setContent("Great book!");
        review.setRating(5);
        doNothing().when(bookService).addBookRating("NWDswEACAAJ", 5);
        when(reviewDAO.save(review)).thenReturn(new Review());
        reviewService.addReview(review);
        verify(bookService, times(1)).addBookRating("NWDswEACAAJ", 5);
        verify(reviewDAO, times(1)).save(review);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewDAO).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        assertEquals("NWDswEACAAJ", savedReview.getBookId());
        assertEquals(1L, savedReview.getUserId().longValue());
        assertEquals("Great book!", savedReview.getContent());
        assertEquals(5, savedReview.getRating());
    }

    @Test
    public void testDeleteReviews() {
        Review review = new Review();
        review.setBookId("NWDswEACAAJ");
        review.setUserId(1L);
        review.setContent("Great book!");
        review.setRating(5);
        doNothing().when(reviewDAO).deleteById(1L);
        reviewService.deleteReview(1L);
        verify(reviewDAO, times(1)).deleteById(1L);
        Review deletedReview = reviewService.getReviewsByBookIdAndUserId("NWDswEACAAJ",1L);
        assertNull(deletedReview, "Review should be deleted and not found");
    }

    @Test
    public void testDeleteReview_ReviewNotFoundException() {
        Long reviewId = 1L;
        doThrow(new ReviewNotFoundException("Review not found")).when(reviewDAO).deleteById(reviewId);
        try {
            reviewService.deleteReview(reviewId);
            verify(reviewDAO, times(1)).deleteById(reviewId);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testUpdateReview_Success() {
        Long reviewId = 1L;
        String newContent = "Updated review content";
        int newRating = 4;
        Review review = new Review();
        review.setContent("Great book!");
        review.setRating(5);
        doNothing().when(reviewDAO).update(any(Review.class));
        reviewService.updateReview(reviewId, newContent, newRating);
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewDAO, times(1)).update(reviewCaptor.capture());
        Review capturedReview = reviewCaptor.getValue();

        assertEquals(reviewId, capturedReview.getId());
        assertEquals(newContent, capturedReview.getContent());
        assertEquals(newRating, capturedReview.getRating());
    }

    @Test
    public void testGetReviewsByBookIdAndUserId_Success() {
        String bookId = "NWDswEACAAJ";
        Long userId = 1L;
        Review expectedReview = new Review();
        expectedReview.setBookId(bookId);
        expectedReview.setUserId(userId);
        expectedReview.setContent("Great book!");
        expectedReview.setRating(5);
        when(reviewDAO.findAllByBookIdAndUserId(bookId, userId)).thenReturn(expectedReview);
        Review result = reviewService.getReviewsByBookIdAndUserId(bookId, userId);
        assertNotNull(result);
        assertEquals(expectedReview, result);
        verify(reviewDAO, times(1)).findAllByBookIdAndUserId(bookId, userId);
    }

    @Test
    public void testGetReviewsByBookId_Success() {
        String bookId = "NWDswEACAAJ";
        Review review1 = new Review();
        review1.setBookId(bookId);
        review1.setUserId(1L);
        review1.setContent("Great book!");
        review1.setRating(5);
        Review review2 = new Review();
        review2.setBookId(bookId);
        review2.setUserId(2L);
        review2.setContent("Not bad.");
        review2.setRating(3);
        List<Review> expectedReviews = Arrays.asList(review1, review2);
        when(reviewDAO.findALLByBookId(bookId)).thenReturn(expectedReviews);
        List<Review> result = reviewService.getReviewsByBookId(bookId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedReviews, result);
        verify(reviewDAO, times(1)).findALLByBookId(bookId);
    }

    @Test
    public void testGetReviewsByBookId_EmptyList() {
        String bookId = "NWDswEACAAJ";
        when(reviewDAO.findALLByBookId(bookId)).thenReturn(Collections.emptyList());
        List<Review> result = reviewService.getReviewsByBookId(bookId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewDAO, times(1)).findALLByBookId(bookId);
    }

    @Test
    public void testGetReviewsByBookId_Exception() {
        String bookId = "NWDswEACAAJ";
        when(reviewDAO.findALLByBookId(bookId)).thenThrow(new RuntimeException("Database error"));
        try {
            reviewService.getReviewsByBookId(bookId);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Database error", e.getMessage());
        }
        verify(reviewDAO, times(1)).findALLByBookId(bookId);
    }

}
