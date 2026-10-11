package com.ddd.bookverse.service;

import com.ddd.bookverse.dao.BookDAO;
import com.ddd.bookverse.dao.BorrowDAO;
import com.ddd.bookverse.dao.UserDAO;
import com.ddd.bookverse.exception.BorrowNotFoundException;
import com.ddd.bookverse.exception.BorrowStatusException;
import com.ddd.bookverse.exception.IllegalPagesReadException;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.model.Borrow;
import com.ddd.bookverse.model.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class to handle book borrowing and related operations such as borrowing,
 * returning, and updating borrow status, as well as handling fines.
 */
public class BorrowService {
    private BorrowDAO borrowDAO = new BorrowDAO();
    private BookDAO bookDAO = new BookDAO();
    private UserDAO userDAO = new UserDAO();

    public BorrowService(){}

    public BorrowService(BorrowDAO borrowDAO, BookDAO bookDAO, UserDAO userDAO) {
        this.borrowDAO = borrowDAO;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;
    }

    /**
     * Adds a borrow record with the given details.
     *
     * @param userId the ID of the user borrowing the book
     * @param bookId the ID of the book being borrowed
     * @param borrowDate the date the book was borrowed
     * @param returnDate the date the book should be returned
     * @param status the current status of the borrow (e.g., "Borrowing")
     * @param pagesRead the number of pages read by the user
     */
    public void addBorrow(Long userId, String bookId, String borrowDate,
                          String returnDate, String status, Integer pagesRead) {
        Borrow borrow = new Borrow();
        User user = userDAO.findById(userId);
        Book book = bookDAO.findById(bookId);
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.parse(borrowDate));
        borrow.setReturnDate(LocalDate.parse(returnDate));
        borrow.setStatus(status);
        borrow.setPagesRead(pagesRead);
        borrowDAO.save(borrow);
    }

    /**
     * Adds a new borrow record to the system.
     *
     * @param borrow the Borrow object to be saved
     */
    public void addBorrow(Borrow borrow) {
        borrowDAO.save(borrow);
    }


    /**
     * Borrows a book for a user, ensuring that the book is available.
     *
     * @param userId the ID of the user borrowing the book
     * @param bookId the ID of the book being borrowed
     * @param borrowDate the date the book is borrowed
     * @param returnDate the date the book should be returned
     */
    public void borrowBook(Long userId, String bookId, String borrowDate, String returnDate) {
        Borrow latestBorrow = getLatestBorrowOfABookFromUser(userId, bookId);

        if (latestBorrow != null && latestBorrow.getStatus().equals("Borrowing")) {
            throw new BorrowStatusException("User is already borrowing this book.");
        }

        if (latestBorrow == null || !latestBorrow.getStatus().equals("Plan to borrow")) {
            addBorrow(userId, bookId, borrowDate, returnDate, "Borrowing", 0);
        } else {
            latestBorrow.setBorrowDate(LocalDate.parse(borrowDate));
            latestBorrow.setReturnDate(LocalDate.parse(returnDate));
            latestBorrow.setStatus("Borrowing");
            updateBorrow(latestBorrow.getId(), latestBorrow);
        }

        Book book = bookDAO.findById(bookId);
        book.setQuantityInStock(book.getQuantityInStock() - 1);
        bookDAO.updateBook(book);
    }

    /**
     * Borrows a book by creating a borrow record.
     *
     * @param borrow the borrow object containing all relevant information
     */
    public void borrowBook(Borrow borrow) {
        borrowBook(borrow.getUser().getId(), borrow.getBook().getId(),
                borrow.getBorrowDate().toString(), borrow.getReturnDate().toString());
    }

    /**
     * Returns a borrowed book for a user and updates its status and quantity.
     *
     * @param userId the ID of the user returning the book
     * @param bookId the ID of the book being returned
     * @param returnDate the date the book is being returned
     */
    public void returnBook(Long userId, String bookId, String returnDate) {
        Borrow latestBorrow = getLatestBorrowOfABookFromUser(userId, bookId);

        if (latestBorrow == null || !latestBorrow.getStatus().equals("Borrowing")) {
            throw new BorrowStatusException("User is not borrowing this book.");
        }

        latestBorrow.setStatus("Returned");
        latestBorrow.setReturnDate(LocalDate.parse(returnDate));
        updateBorrow(latestBorrow.getId(), latestBorrow);

        Book book = bookDAO.findById(bookId);
        book.setQuantityInStock(book.getQuantityInStock() + 1);
        bookDAO.updateBook(book);
    }

    /**
     * Returns a borrowed book and updates its status and quantity.
     *
     * @param borrow the borrow object containing the book and user details
     */
    public void returnBook(Borrow borrow) {
        returnBook(borrow.getUser().getId(), borrow.getBook().getId(),
                borrow.getReturnDate().toString());
    }


    /**
     * Marks a fine as paid and updates the borrow status.
     *
     * @param userId the ID of the user paying the fine
     * @param bookId the ID of the book for which the fine is being paid
     * @param returnDate the date the book was returned
     */
    public void paidFine(Long userId, String bookId, String returnDate) {
        Borrow latestBorrow = getLatestBorrowOfABookFromUser(userId, bookId);

        if (latestBorrow == null || !latestBorrow.getStatus().equals("Overdue")) {
            throw new BorrowStatusException("User does not have any overdue.");
        }

        latestBorrow.setStatus("Paid");
        latestBorrow.setReturnDate(LocalDate.parse(returnDate));
        updateBorrow(latestBorrow.getId(), latestBorrow);

        Book book = bookDAO.findById(bookId);
        book.setQuantityInStock(book.getQuantityInStock() + 1);
        bookDAO.updateBook(book);
    }

    /**
     * Marks a fine as paid for the specified borrow record.
     *
     * @param borrow the borrow record to mark as paid
     */
    public void paidFine(Borrow borrow) {
        paidFine(borrow.getUser().getId(), borrow.getBook().getId(),
                borrow.getReturnDate().toString());
    }


    /**
     * Deletes a borrow record by its ID.
     *
     * @param id the ID of the borrow record to delete
     */
    public void deleteBorrow(Long id) {
        try {
            borrowDAO.deleteById(id);
        } catch (BorrowNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a borrow record by its ID.
     *
     * @param id the ID of the borrow record to retrieve
     * @return the Borrow object if found, otherwise null
     */
    public Borrow getBorrow(Long id) {
        try {
            return borrowDAO.findById(id);
        } catch (BorrowNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves all borrow records for a given user.
     *
     * @param userId the ID of the user whose borrow records are to be retrieved
     * @return a list of Borrow objects for the specified user
     */
    public List<Borrow> getBorrowByUserId(Long userId) {
        return borrowDAO.findByUser(userId);
    }

    /**
     * Retrieves all borrow records for a given book.
     *
     * @param bookId the ID of the book whose borrow records are to be retrieved
     * @return a list of Borrow objects for the specified book
     */
    public List<Borrow> getBorrowByBookId(String bookId) {
        return borrowDAO.findByBook(bookId);
    }

    /**
     * Retrieves the latest borrow record for a given user and book.
     *
     * @param userId the ID of the user
     * @param bookId the ID of the book
     * @return the latest Borrow object for the user and book, or null if not found
     */
    public Borrow getLatestBorrowOfABookFromUser(Long userId, String bookId) {
        try {
            return borrowDAO.findLatestBorrowOfABookFromUser(userId, bookId);
        } catch (BorrowNotFoundException e) {
            return null;
        }
    }

    /**
     * Retrieves all borrow records for a given book and user.
     *
     * @param bookId the ID of the book
     * @param userId the ID of the user
     * @return a list of all Borrow objects for the specified book and user
     */
    public List<Borrow> getAllBorrowsOfABookFromUser(String bookId, Long userId) {
        return borrowDAO.findAllBorrowsOfABookFromUser(bookId, userId);
    }


    /**
     * Updates an existing borrow record.
     *
     * @param id the ID of the borrow record to update
     * @param borrow the updated Borrow object
     */
    public void updateBorrow (Long id, Borrow borrow) {
        borrowDAO.updateBorrow(id, borrow);
    }

    /**
     * Updates the pages read for a specific borrow record.
     *
     * @param id the ID of the borrow record
     * @param pagesRead the number of pages read to update
     */
    public void updatePagesRead (Long id, int pagesRead) {
        try {
            borrowDAO.updatePagesRead(id, pagesRead);
        } catch (IllegalPagesReadException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the status of a borrow record.
     *
     * @param id the ID of the borrow record
     * @param status the new status to set
     */
    public void updateBorrowStatus (Long id, String status) {
        borrowDAO.updateStatus(id, status);
    }

    /**
     * Retrieves the total number of borrow records in the system.
     *
     * @return the total count of borrow records
     */
    public Integer getSum() {
        return borrowDAO.getSum();
    }

    /**
     * Retrieves the latest N borrow records.
     *
     * @param n the number of latest borrow records to retrieve
     * @return a list of the latest N borrow records
     */
    public List<Borrow> getLatestNBorrows (int n) {
        return borrowDAO.findLatestNBorrows(n);
    }


    /**
     * Counts the number of borrow records for a specific book with a given status.
     *
     * @param bookId the ID of the book
     * @param status the status of the borrow records to count (e.g., "Borrowing", "Returned")
     * @return the number of borrow records matching the given book and status
     */
    public int getNumBorrowsOfBookWithStatus(String bookId, String status) {
        return borrowDAO.countByBookAndStatus(bookId, status);
    }

    /**
     * Counts the number of borrow records for a specific user with a given status.
     *
     * @param userId the ID of the user
     * @param status the status of the borrow records to count (e.g., "Borrowing", "Returned")
     * @return the number of borrow records matching the given user and status
     */
    public int getNumBorrowsOfUserWithStatus(Long userId, String status) {
        return borrowDAO.countByUserAndStatus(userId, status);
    }

    /**
     * Updates the status of overdue borrow records for a specific book.
     * A borrow record is considered overdue if its return date is before the current date.
     *
     * @param bookId the ID of the book to check for overdue borrows
     */
    public void updateOverdueBorrowsByBookId(String bookId) {
        List<Borrow> borrows = borrowDAO.findByBook(bookId);
        LocalDate currentDate = LocalDate.now();

        for (Borrow borrow : borrows) {
            if (borrow.getStatus().equals("Borrowing")
                    && borrow.getReturnDate().isBefore(currentDate)) {
                borrow.setStatus("Overdue");
                borrowDAO.updateBorrow(borrow.getId(), borrow);
            }
        }
    }

    /**
     * Updates the status of overdue borrow records for a specific user.
     * A borrow record is considered overdue if its return date is before the current date.
     *
     * @param userId the ID of the user to check for overdue borrows
     */
    public void updateOverdueBorrowsByUserId(Long userId) {
        List<Borrow> borrows = borrowDAO.findByUser(userId);
        LocalDate currentDate = LocalDate.now();

        for (Borrow borrow : borrows) {
            if (borrow.getStatus().equals("Borrowing")
                    && borrow.getReturnDate().isBefore(currentDate)) {
                borrow.setStatus("Overdue");
                borrowDAO.updateBorrow(borrow.getId(), borrow);
            }
        }
    }


}
