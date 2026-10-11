package com.ddd.bookverse.model;


import java.time.LocalDate;

/**
 * Entity class representing a Borrow record.
 */
public class Borrow {
    private Long id;

    private User user;
    private Book book;

    private LocalDate borrowDate;
    private LocalDate returnDate;

    private String status; // Only accept "Plan to borrow" | "Borrowing" | "Returned"
    private Integer pagesRead;

    public static final String PLAN = "Plan to borrow";
    public static final String BORROWING = "Borrowing";
    public static final String RETURNED = "Returned";
    public static final String OVERDUE = "Overdue";
    public static final String PAID = "Paid";

    /**
     * Default constructor for Borrow.
     */
    public Borrow() {
    }

    /**
     * Copy constructor for Borrow.
     *
     * @param other The Borrow object to copy.
     */
    public Borrow(Borrow other) {
        this.id = other.id;
        this.user = other.user;
        this.book = other.book;
        this.borrowDate = other.borrowDate;
        this.returnDate = other.returnDate;
        this.status = other.status;
        this.pagesRead = other.pagesRead;
    }

    /**
     * Gets the unique identifier of the borrow record.
     *
     * @return The unique identifier of the borrow record.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the borrow record.
     *
     * @param id The unique identifier to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user associated with the borrow record.
     *
     * @return The user associated with the borrow record.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with the borrow record.
     *
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the book associated with the borrow record.
     *
     * @return The book associated with the borrow record.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book associated with the borrow record.
     *
     * @param book The book to set.
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the borrow date of the borrow record.
     *
     * @return The borrow date of the borrow record.
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * Sets the borrow date of the borrow record.
     *
     * @param borrowDate The borrow date to set.
     */
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Gets the return date of the borrow record.
     *
     * @return The return date of the borrow record.
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date of the borrow record.
     *
     * @param returnDate The return date to set.
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Gets the status of the borrow record.
     *
     * @return The status of the borrow record.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the borrow record.
     *
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the number of pages read in the borrow record.
     *
     * @return The number of pages read in the borrow record.
     */
    public Integer getPagesRead() {
        return pagesRead;
    }

    /**
     * Sets the number of pages read in the borrow record.
     *
     * @param pagesRead The number of pages read to set.
     */
    public void setPagesRead(Integer pagesRead) {
        this.pagesRead = pagesRead;
    }

    /**
     * Returns a string representation of the borrow record.
     *
     * @return A string representation of the borrow record.
     */
    @Override
    public String toString() {
        return "Borrow{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", book=" + book.getTitle() +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                ", pagesRead=" + pagesRead +
                '}';
    }
}