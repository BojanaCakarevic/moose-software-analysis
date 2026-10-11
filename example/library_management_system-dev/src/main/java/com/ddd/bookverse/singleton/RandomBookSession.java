package com.ddd.bookverse.singleton;

import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.service.BookService;

/**
 * Singleton class to manage a random book session.
 */
public class RandomBookSession {
    private static RandomBookSession instance;
    private static final BookService bookService = new BookService();
    private Book randomBook = null;

    /**
     * Private constructor to initialize the random book.
     */
    private RandomBookSession() {
        this.randomBook = bookService.getRandomBook();
    }

    /**
     * Returns the singleton instance of RandomBookSession.
     *
     * @return The singleton instance of RandomBookSession.
     */
    public static RandomBookSession getInstance() {
        if (instance == null) {
            instance = new RandomBookSession();
        }
        return instance;
    }

    /**
     * Retrieves a random book.
     *
     * @return A random Book object.
     */
    public Book getRandomBook() {
        randomBook = bookService.getRandomBook();
        return randomBook;
    }
}