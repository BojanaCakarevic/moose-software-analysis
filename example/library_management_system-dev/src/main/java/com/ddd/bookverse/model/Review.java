package com.ddd.bookverse.model;


/**
 * Entity class representing a Review.
 */
public class Review {
    private Long id;
    private String bookId;
    private Long userId;
    private String content;
    private int rating;

    /**
     * Default constructor for Review.
     */
    public Review() {
    }

    /**
     * Constructor for Review with specified details.
     *
     * @param bookId The ID of the book being reviewed.
     * @param userId The ID of the user who wrote the review.
     * @param content The content of the review.
     * @param rating The rating given in the review.
     */
    public Review(String bookId, Long userId, String content, int rating) {
        this.bookId = bookId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
    }

    /**
     * Gets the unique identifier of the review.
     *
     * @return The unique identifier of the review.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the review.
     *
     * @param id The unique identifier to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the book being reviewed.
     *
     * @return The ID of the book being reviewed.
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * Sets the ID of the book being reviewed.
     *
     * @param bookId The ID of the book to set.
     */
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    /**
     * Gets the ID of the user who wrote the review.
     *
     * @return The ID of the user who wrote the review.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who wrote the review.
     *
     * @param userId The ID of the user to set.
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the content of the review.
     *
     * @return The content of the review.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the review.
     *
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the rating given in the review.
     *
     * @return The rating given in the review.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating given in the review.
     *
     * @param rating The rating to set.
     */
    public void setRating(int rating) {
        this.rating = rating;
    }
}