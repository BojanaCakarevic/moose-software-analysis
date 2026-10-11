package com.ddd.bookverse.service;

import com.ddd.bookverse.dao.ReviewDAO;
import com.ddd.bookverse.exception.ReviewNotFoundException;
import com.ddd.bookverse.model.Borrow;
import com.ddd.bookverse.model.Review;

import java.util.List;

/**
 * Service class that handles review-related operations, including adding, updating, deleting,
 * and retrieving reviews for books and users.
 */
public class ReviewService {
    private ReviewDAO reviewDAO = new ReviewDAO();
    private BookService bookService = new BookService();

    /**
     * Default constructor.
     */
    public ReviewService() {}

    /**
     * Constructor with custom dependencies.
     *
     * @param reviewDAO the ReviewDAO to be used by this service
     * @param bookService the BookService to interact with book-related data
     */
    public ReviewService(ReviewDAO reviewDAO,BookService bookService) {
        this.bookService = bookService;
        this.reviewDAO = reviewDAO;
    }

    /**
     * Adds a new review to the system and updates the book rating.
     *
     * @param review the Review object containing details of the review
     */
    public void addReview(Review review) {
        String bookId = review.getBookId();
        int rating = review.getRating();
        bookService.addBookRating(bookId, rating);
        reviewDAO.save(review);
    }

    /**
     * Deletes a review from the system based on its ID.
     *
     * @param id the ID of the review to be deleted
     */
    public void deleteReview(Long id) {
        try {
            reviewDAO.deleteById(id);
        } catch (ReviewNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the content and rating of an existing review.
     *
     * @param reviewId the ID of the review to be updated
     * @param content the new content for the review
     * @param rating the new rating for the review
     */
    public void updateReview(Long reviewId, String content, int rating) {
        Review review = new Review();
        review.setId(reviewId);
        review.setContent(content);
        review.setRating(rating);
        reviewDAO.update(review);
    }

    /**
     * Retrieves a review by the book ID and user ID.
     *
     * @param bookId the ID of the book
     * @param userId the ID of the user
     * @return the Review object if found, otherwise null
     */
    public Review getReviewsByBookIdAndUserId(String bookId, Long userId) {
        try {
            return reviewDAO.findAllByBookIdAndUserId(bookId, userId);
        } catch (ReviewNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves all reviews for a given book.
     *
     * @param bookId the ID of the book whose reviews are to be retrieved
     * @return a list of Review objects for the specified book
     */
    public List<Review> getReviewsByBookId(String bookId) {
        return reviewDAO.findALLByBookId(bookId);
    }
}

