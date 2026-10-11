package com.ddd.bookverse.model;


import java.util.Arrays;

/**
 * Entity class representing a Book.
 */
public class Book {
    private String id;

    private String title;
    private String[] authors;
    private String description;

    private byte[] image;

    private Integer pageCount = 0;
    private String publishDate;
    private String publisher;
    private String[] categories;
    private Integer quantityInStock = 0;
    private String isbn;
    private Long ratingSum = 0L;
    private Integer ratingCount = 0;

    public static final String AUTHOR = "authors";
    public static final String TITLE= "title";
    public static final String CATEGORY  = "categories";
    public static final String PUBLISHER = "publisher";

    /**
     * Default constructor for Book.
     */
    public Book() {
    }

    /**
     * Constructor for Book with specified id, title, and authors.
     *
     * @param id The unique identifier for the book.
     * @param title The title of the book.
     * @param authors The authors of the book.
     */
    public Book(String id, String title, String[] authors) {
        this.id = id;
        this.title = title;
        this.authors = authors;
    }

    /**
     * Gets the unique identifier of the book.
     *
     * @return The unique identifier of the book.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the book.
     *
     * @param id The unique identifier to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the title of the book.
     *
     * @return The title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the authors of the book.
     *
     * @return The authors of the book.
     */
    public String[] getAuthors() {
        return authors != null ? authors : new String[0];
    }

    /**
     * Sets the authors of the book.
     *
     * @param authors The authors to set.
     */
    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    /**
     * Gets the description of the book.
     *
     * @return The description of the book.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the book.
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the image of the book.
     *
     * @return The image of the book.
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Sets the image of the book.
     *
     * @param image The image to set.
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * Gets the page count of the book.
     *
     * @return The page count of the book.
     */
    public Integer getPageCount() {
        return pageCount;
    }

    /**
     * Sets the page count of the book.
     *
     * @param pageCount The page count to set.
     */
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount != null ? Math.max(0, pageCount) : 0;
    }

    /**
     * Gets the publish date of the book.
     *
     * @return The publish date of the book.
     */
    public String getPublishDate() {
        return publishDate;
    }

    /**
     * Sets the publish date of the book.
     *
     * @param publishDate The publish date to set.
     */
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * Gets the publisher of the book.
     *
     * @return The publisher of the book.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the book.
     *
     * @param publisher The publisher to set.
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Gets the categories of the book.
     *
     * @return The categories of the book.
     */
    public String[] getCategories() {
        return categories;
    }

    /**
     * Sets the categories of the book.
     *
     * @param categories The categories to set.
     */
    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    /**
     * Gets the quantity in stock of the book.
     *
     * @return The quantity in stock of the book.
     */
    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    /**
     * Sets the quantity in stock of the book.
     *
     * @param quantityInStock The quantity in stock to set.
     */
    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock != null ? Math.max(0, quantityInStock) : 0;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return The ISBN of the book.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn The ISBN to set.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the sum of ratings of the book.
     *
     * @return The sum of ratings of the book.
     */
    public Long getRatingSum() {
        return ratingSum;
    }

    /**
     * Sets the sum of ratings of the book.
     *
     * @param ratingSum The sum of ratings to set.
     */
    public void setRatingSum(Long ratingSum) {
        this.ratingSum = ratingSum;
    }

    /**
     * Gets the count of ratings of the book.
     *
     * @return The count of ratings of the book.
     */
    public Integer getRatingCount() {
        return ratingCount;
    }

    /**
     * Sets the count of ratings of the book.
     *
     * @param ratingCount The count of ratings to set.
     */
    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    /**
     * Adds a rating to the book.
     *
     * @param rating The rating to add.
     */
    public void addRating(int rating) {
        this.ratingSum += rating;
        this.ratingCount++;
    }

    /**
     * Removes a rating from the book.
     *
     * @param rating The rating to remove.
     */
    public void removeRating(int rating) {
        this.ratingSum -= rating;
        this.ratingCount = Math.max(0, this.ratingCount - 1);
    }

    /**
     * Gets the average rating of the book.
     *
     * @return The average rating of the book.
     */
    public double getAverageRating() {
        if (this.ratingCount == 0) {
            return 0.0;
        }
        return (double) this.ratingSum / this.ratingCount;
    }

    /**
     * Returns a string representation of the book.
     *
     * @return A string representation of the book.
     */
    @Override
    public String toString() {
        return "\nBook{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", authors=" + Arrays.toString(authors) +
                ", description='" + description + '\'' +
                ", pageCount=" + pageCount +
                ", publishDate='" + publishDate + '\'' +
                ", publisher='" + publisher + '\'' +
                ", categories=" + Arrays.toString(categories) +
                ", quantityInStock=" + quantityInStock +
                ", isbn='" + isbn + '\'' +
                "}";
    }
}