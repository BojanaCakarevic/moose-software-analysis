package com.ddd.bookverse.service;

import com.ddd.bookverse.dao.BorrowDAO;
import com.ddd.bookverse.exception.BookNotFoundException;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.dao.BookDAO;
import com.ddd.bookverse.model.Borrow;
import javafx.util.Pair;


import java.io.IOException;
import java.util.*;

import static com.ddd.bookverse.api.GoogleBooksAPI.getBooksByCategory;

/**
 * Service layer for handling operations related to books.
 */
public class BookService {
    private BookDAO bookDAO = new BookDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();

    public static Stack<Book> stackBook = new Stack<>();

    public BookService() {}

    public BookService(BookDAO bookDAO,BorrowDAO borrowDAO) {
        this.bookDAO = bookDAO;
        this.borrowDAO = borrowDAO;
    }

    /**
     * Adds a new book to the database.
     *
     * @param book the book to add
     * @return the saved book
     */
    public Book addBook(Book book) {
        return bookDAO.save(book);
    }

    /**
     * Deletes a book from the database by its ID.
     *
     * @param id the ID of the book to delete
     */
    public void deleteBook (String id) {
        try {
            bookDAO.deleteById(id);
        } catch (BookNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all books stored in the database.
     *
     * @return a list of all books
     */
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }


    /**
     * Retrieves a specific book by its ID.
     *
     * @param id the ID of the book to retrieve
     * @return the book with the specified ID, or null if not found
     */
    public Book getBook(String id) {
        try {
            return bookDAO.findById(id);
        } catch (BookNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a random book from the database.
     *
     * @return a random book, or null if an error occurs
     */
    public Book getRandomBook() {
        try {
            return bookDAO.findRandomBook();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves books by a specific author.
     *
     * @param author the author to search for
     * @return a list of books by the specified author
     */
    public List<Book> getBookByAuthor(String author) {
        return bookDAO.findByAuthor(author);
    }

    /**
     * Retrieves books published by a specific publisher.
     *
     * @param publisher the publisher to search for
     * @return a list of books by the specified publisher
     */
    public List<Book> getBookByPublisher(String publisher) {
        return bookDAO.findByPublisher(publisher);
    }

    /**
     * Retrieves books in a specific category.
     *
     * @param category the category to search for
     * @return a list of books in the specified category
     */
    public List<Book> getBookByCategory(String category) {
        return bookDAO.findByCategory(category);
    }

    /**
     * Retrieves books with a specific title.
     *
     * @param title the title to search for
     * @return a list of books with the specified title
     */
    public List<Book> getBookByTitle(String title) {
        return bookDAO.findByTitle(title);
    }

    /**
     * Updates information for an existing book.
     *
     * @param book the book with updated information
     */
    public void updateBook (Book book) {
        bookDAO.updateBook(book);
    }

    /**
     * Updates the quantity in stock for a specific book.
     *
     * @param id the ID of the book to update
     * @param quantity the new quantity to set
     * @throws IllegalArgumentException if the quantity is negative
     */
    public void updateBookQuantity(String id, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be greater than or equal to 0");
        }
        Book book = bookDAO.findById(id);
        book.setQuantityInStock(quantity);
        bookDAO.updateBook(book);
    }


    /**
     * Adds books to the database by category using an external API.
     *
     * @param category the category to fetch books for
     * @throws IOException if an error occurs while fetching books from the API
     */
    public void addBooksToDatabaseByCategory(String category) throws IOException {
        List<Book> books = getBooksByCategory(category);
        for (Book book : books) {
            bookDAO.save(book);
        }
    }


    /**
     * Fetches books from an external API by category and saves them to the database.
     *
     * @param category the category of books to fetch
     * @return a list of books fetched from the API and saved to the database
     * @throws IOException if an error occurs while fetching the books from the API
     */
    public List<Book> getBooksToAPI(String category) throws IOException {
        List<Book> books = getBooksByCategory(category);
        for(Book book : books) {
            bookDAO.save(book);
        }
        return  books;
    }

    /**
     * Retrieves books borrowed by a user and sorts them by pages read.
     *
     * @param userId the ID of the user
     * @return a list of books borrowed by the user
     */
    public List<Book> getBooksBorrowedByUser(Long userId) {
        List<Book> borrowedBooks = new ArrayList<>();

        List<Borrow> borrowList = borrowDAO.findByUser(userId);
        borrowList.sort(Comparator.comparingInt(Borrow::getPagesRead).reversed());

        for (Borrow borrow : borrowList) {
            borrowedBooks.add(borrow.getBook());
        }

        return borrowedBooks;
    }


    /**
     * Retrieves books borrowed by a user filtered by a specific status.
     *
     * @param userId the ID of the user
     * @param status the status of the borrow (e.g., "borrowed", "returned")
     * @return a list of books matching the specified status
     */
    public List<Book> getBooksByUserAndStatus(Long userId, String status) {
        List<String> bookIds = borrowDAO.findByUserAndStatus(userId, status);
        List<Book> books = new ArrayList<>();
        for (String bookId : bookIds) {
            try {
                Book book = bookDAO.findById(bookId);
                books.add(book);
            } catch (BookNotFoundException e) {
                // Handle the case where a book is not found (could log or continue)
                // Optionally log or ignore, based on the expected behavior
            }
        }
        return books;
    }

    /**
     * Retrieves a list of books based on the user's ID, status, and specific book attribute.
     *
     * @param userId the ID of the user
     * @param status the status of the book
     * @param query the book attribute to filter by
     * @param value the value of the attribute to match
     * @return a list of books that match the specified criteria
     */
    public List<Book> getBooksByUserStatusAndBookAttr(Long userId, String status,
                                                      String query, String value ) {
        return bookDAO.findByUserStatusAndBookAttr(userId, status, query, value);
    }

    /**
     * Adds a rating to a book.
     *
     * @param bookId the ID of the book
     * @param rating the rating to add
     */
    public void addBookRating(String bookId, int rating) {
        Book book = bookDAO.findById(bookId);
        book.addRating(rating);
        bookDAO.updateBook(book);
    }

    /**
     * Removes a rating from a book.
     *
     * @param bookId the ID of the book
     * @param rating the rating to remove
     */
    public void removeBookRating(String bookId, int rating) {
        Book book = bookDAO.findById(bookId);
        book.removeRating(rating);
        bookDAO.updateBook(book);
    }

    /**
     * Retrieves the total count of books in the database.
     *
     * @return the total number of books
     */
    public Integer getSum() {
        return bookDAO.getSum();
    }

    /**
     * Calculates the completion percentage for a user on a specific book.
     *
     * @param bookId the ID of the book
     * @param userId the ID of the user
     * @return the completion percentage
     */
    public double getCompletionPercentage(String bookId, Long userId) {
        return bookDAO.getCompletionPercentage(bookId, userId);
    }

    /**
     * Retrieves data for each book category and its count.
     *
     * @return a map of category names and their respective book counts
     */
    public Map<String, Integer> getDataCategory() {
        return bookDAO.getDataCategory();
    }


    /**
     * Retrieves the number of books a user has borrowed with a specific status.
     *
     * @param id the ID of the user
     * @param status the status of the borrowed books
     * @return the number of books with the specified status
     */
    public int getNumBooksByUserIdAndStatus(Long id, String status) {
        return borrowDAO.countByUserAndStatus(id, status);
    }

    /**
     * Retrieves books marked as "reading" by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of books that the user is currently reading
     */
    public List<Book> getReadingBooksByUser(Long userId) {
        return bookDAO.findReadingBookFromUser(userId);
    }

    /**
     * Retrieves the top N most borrowed books.
     *
     * @param n the number of books to fetch
     * @return a list of top N borrowed books along with their borrow count
     */
    public List<Pair<Book, Integer>> getTopBorrowedBooks(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The number of books to "
                    + "fetch must be positive.");
        }
        return bookDAO.findTopNBorrowedBooks(n);
    }

    /**
     * Retrieves distinct categories for a book query.
     *
     * @param query the query to filter categories
     * @return a list of distinct categories
     */
    public List<String> getDistinctCategories(String query) {
        return bookDAO.getDistinctCategories(query);
    }

    /**
     * Checks if a book with the specified ISBN is already present in the database.
     *
     * @param isbn the ISBN of the book to check
     * @return true if the ISBN is already taken, false otherwise
     */
    public boolean isIsbnTaken(String isbn) {
        try {
            Book book = bookDAO.findByISBN(isbn);
            return book != null;
        } catch (BookNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks if a book with the specified ID is already present in the database.
     *
     * @param id the ID of the book to check
     * @return true if the ID is already taken, false otherwise
     */
    public boolean isIdTaken(String id) {
        try {
            Book book = bookDAO.findById(id);
            return book != null;
        } catch (BookNotFoundException e) {
            return false;
        }
    }

}