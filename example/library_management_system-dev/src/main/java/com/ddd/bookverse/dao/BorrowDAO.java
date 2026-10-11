package com.ddd.bookverse.dao;

import com.ddd.bookverse.config.DatabaseConnection;
import com.ddd.bookverse.exception.BorrowNotFoundException;
import com.ddd.bookverse.exception.IllegalPagesReadException;
import com.ddd.bookverse.model.Borrow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object (DAO) for interacting with the "borrows" table in the database.
 * Provides methods to save, update, delete, and query borrow records.
 */
public class BorrowDAO {
    private Connection connectionTest;

    private UserDAO userDAO = new UserDAO();
    private BookDAO bookDAO = new BookDAO();

    /**
     * Constructs a BorrowDAO with the provided connection.
     *
     * @param connection the connection to use for database operations
     */
    public BorrowDAO(Connection connection,UserDAO userDAO, BookDAO bookDAO) {
        this.connectionTest = connection;
        this.userDAO = userDAO;
        this.bookDAO = bookDAO;
    }


    /**
     * Creates the "borrows" table if it doesn't exist.
     */
    public BorrowDAO() {
        String sql = "CREATE TABLE IF NOT EXISTS borrows ("
                + "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                + "userId BIGINT NOT NULL,"
                + "bookId VARCHAR(255) NOT NULL,"
                + "borrowDate DATE NOT NULL,"
                + "returnDate DATE,"
                + "status TINYTEXT,"
                + "pagesRead INT DEFAULT 0,"
                + "FOREIGN KEY (userId) REFERENCES users(id), "
                + "FOREIGN KEY (bookId) REFERENCES books(id)"
                + ");";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a borrow record in the database.
     *
     * @param borrow the borrow record to save
     * @return the saved borrow record with generated ID
     */
    public Borrow save(Borrow borrow) {
    String sql = "INSERT INTO borrows "
            + "(userId, bookId, borrowDate, returnDate, status, pagesRead) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
    Connection connection = null;
    boolean shouldCloseConnection = false;
    try {
        if (connectionTest == null) {
            connection = DatabaseConnection.getConnection();
            shouldCloseConnection = true;
        } else {
            connection = connectionTest;
        }
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            int cnt = 0;
            preparedStatement.setLong(++cnt, borrow.getUser().getId());
            preparedStatement.setString(++cnt, borrow.getBook().getId());
            preparedStatement.setDate(++cnt, java.sql.Date.valueOf(borrow.getBorrowDate()));
            if (borrow.getReturnDate() != null) {
                preparedStatement.setDate(++cnt, java.sql.Date.valueOf(borrow.getReturnDate()));
            } else {
                preparedStatement.setNull(++cnt, java.sql.Types.DATE);
            }
            preparedStatement.setString(++cnt, borrow.getStatus());
            preparedStatement.setInt(++cnt, borrow.getPagesRead());

            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    borrow.setId(generatedId);
                }
            }
            System.out.println(preparedStatement);
            return borrow;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    } finally {
        if (shouldCloseConnection && connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

    /**
     * Finds a borrow record by ID.
     *
     * @param id the ID of the borrow record
     * @return the borrow record
     * @throws BorrowNotFoundException if no borrow is found
     */
    public Borrow findById(Long id) {
        String sql = "SELECT * FROM borrows WHERE id = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractBorrowFromResultSet(resultSet);
            } else {
                throw new BorrowNotFoundException("No borrow found with id: " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds all borrow records for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of borrow records for the user
     */
    public List<Borrow> findByUser(Long userId) {
        String sql = "SELECT * FROM borrows WHERE userId = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Borrow> borrows = new ArrayList<>();

            while (resultSet.next()) {
                borrows.add(extractBorrowFromResultSet(resultSet));
            }

            return borrows;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds the latest borrow record for a specific user and book.
     *
     * @param userId the user ID
     * @param bookId the book ID
     * @return the latest borrow record
     * @throws BorrowNotFoundException if no borrow record is found
     */
    public Borrow findLatestBorrowOfABookFromUser(Long userId, String bookId) {
        String sql = "SELECT * FROM borrows WHERE userId = ? AND bookId = ? ORDER BY id DESC LIMIT 1";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractBorrowFromResultSet(resultSet);
            } else {
                throw new BorrowNotFoundException("No borrow found with userId: " + userId + " and bookId: " + bookId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds all distinct book IDs for a user with a specific status.
     *
     * @param userId the user ID
     * @param status the borrow status
     * @return a list of distinct book IDs
     */
    public List<String> findByUserAndStatus(Long userId, String status) {
        String sql = "SELECT DISTINCT bookId FROM borrows WHERE userId = ? AND status = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, status);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> bookIds = new ArrayList<>();

            while (resultSet.next()) {
                bookIds.add(resultSet.getString("bookId"));
            }

            return bookIds;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Counts the number of books borrowed by a user with a specific status.
     *
     * @param userId the user ID
     * @param status the borrow status
     * @return the count of books
     */
    public int countByUserAndStatus(Long userId, String status) {
        String sql = "SELECT COUNT(DISTINCT bookId) FROM borrows "
                + "WHERE userId = ? AND status = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, status);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Counts the number of borrows for a specific book and status.
     *
     * @param bookId the book ID
     * @param status the borrow status
     * @return the count of borrows
     */
    public int countByBookAndStatus(String bookId, String status) {
        String sql = "SELECT COUNT(*) FROM borrows WHERE bookId = ? AND status = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, bookId);
            preparedStatement.setString(2, status);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Finds all borrow records for a specific book.
     *
     * @param bookId the book ID
     * @return a list of borrow records
     */
    public List<Borrow> findByBook(String bookId) {
        String sql = "SELECT * FROM borrows WHERE bookId = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Borrow> borrows = new ArrayList<>();

            while (resultSet.next()) {
                borrows.add(extractBorrowFromResultSet(resultSet));
            }

            return borrows;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Deletes a borrow record by ID.
     *
     * @param id the ID of the borrow record
     * @throws BorrowNotFoundException if no borrow record is found
     */
    public void deleteById (Long id) {
        String sql = "DELETE FROM borrows WHERE id = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new BorrowNotFoundException("No borrow found with id: " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a borrow record.
     *
     * @param id the ID of the borrow record to update
     * @param borrow the borrow record with updated values
     */
    public void updateBorrow(Long id, Borrow borrow) {
        String sql = "UPDATE borrows SET userId = ?, " +
                "bookId = ?, " +
                "borrowDate = ?, " +
                "returnDate = ?, " +
                "status = ?, " +
                "pagesRead = ? " +
                "WHERE id = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            int cnt = 0;
            preparedStatement.setLong(++cnt, borrow.getUser().getId());
            preparedStatement.setString(++cnt, borrow.getBook().getId());
            preparedStatement.setDate(++cnt, java.sql.Date.valueOf(borrow.getBorrowDate()));
            preparedStatement.setDate(++cnt, java.sql.Date.valueOf(borrow.getReturnDate()));
            preparedStatement.setString(++cnt, borrow.getStatus());
            preparedStatement.setInt(++cnt, borrow.getPagesRead());
            preparedStatement.setLong(++cnt, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the status of a borrow record.
     *
     * @param id the ID of the borrow record
     * @param status the new status
     */
    public void updateStatus(Long id, String status) {
        String sql = "UPDATE borrows SET status = ? WHERE id = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status);
            preparedStatement.setLong(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the pages read in a borrow record.
     *
     * @param id the ID of the borrow record
     * @param pagesRead the number of pages read
     * @throws IllegalPagesReadException if pages read is negative
     */
    public void updatePagesRead(Long id, int pagesRead) {
        System.out.println(pagesRead + " dao");
        if (pagesRead < 0) {
            throw new IllegalPagesReadException("Pages read cannot be negative");
        }

        String sql = "UPDATE borrows SET pagesRead = ? WHERE id = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pagesRead);
            preparedStatement.setLong(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts a borrow record from a ResultSet.
     *
     * @param resultSet the ResultSet
     * @return the borrow record
     * @throws SQLException if an SQL error occurs
     */
    private Borrow extractBorrowFromResultSet(ResultSet resultSet) throws SQLException {
        Borrow borrow = new Borrow();
        borrow.setId(resultSet.getLong("id"));
        borrow.setUser(userDAO.findById(resultSet.getLong("userId")));
        borrow.setBook(bookDAO.findById(resultSet.getString("bookId")));
        borrow.setBorrowDate(resultSet.getDate("borrowDate").toLocalDate());
        borrow.setReturnDate(resultSet.getDate("returnDate").toLocalDate());
        borrow.setStatus(resultSet.getString("status"));
        borrow.setPagesRead(resultSet.getInt("pagesRead"));
        return borrow;
    }

    /**
     * Gets the total number of borrow records in the database.
     *
     * @return the total count of borrows
     */
    public Integer getSum() {
        String sql = "SELECT COUNT(*) AS total_count FROM borrows";
        int sum = 0;
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                sum = resultSet.getInt("total_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sum;
    }

    /**
     * Finds the latest N borrow records, excluding 'Plan to borrow' status.
     *
     * @param n the number of records to retrieve
     * @return a list of the latest N borrow records
     */
    public List<Borrow> findLatestNBorrows(int n) {
        String sql = "SELECT * FROM borrows WHERE status != 'Plan to borrow' ORDER BY id DESC LIMIT ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, n);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Borrow> borrows = new ArrayList<>();

            while (resultSet.next()) {
                borrows.add(extractBorrowFromResultSet(resultSet));
            }

            return borrows;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Finds all borrow records for a specific book and user.
     *
     * @param bookId the book ID
     * @param userId the user ID
     * @return a list of borrow records
     */
    public List<Borrow> findAllBorrowsOfABookFromUser(String bookId, Long userId) {
        String sql = "SELECT * FROM borrows WHERE userId = ? AND bookId = ? ORDER BY id DESC";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Borrow> borrows = new ArrayList<>();

            while (resultSet.next()) {
                borrows.add(extractBorrowFromResultSet(resultSet));
            }

            return borrows;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}