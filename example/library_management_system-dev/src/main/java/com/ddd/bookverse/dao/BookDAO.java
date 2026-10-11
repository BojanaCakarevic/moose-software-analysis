
package com.ddd.bookverse.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ddd.bookverse.config.DatabaseConnection;
import com.ddd.bookverse.exception.BookNotFoundException;
import com.ddd.bookverse.model.Book;
import javafx.util.Pair;


/**
 * Data Access Object (DAO) for interacting with the books table in the database.
 * Provides methods for creating the table and performing database operations on books.
 */
public class BookDAO {

    private Connection connectionTest;

    /**
     * Constructs a BookDao with a custom database connection.
     *
     * @param connection the database connection to be used by the DAO.
     */
    public BookDAO(Connection connection) {
        this.connectionTest = connection;
    }

    /**
     * Default constructor that initializes the books table if it doesn't exist.
     * This constructor executes the SQL statement to create the table with required columns.
     */
    public BookDAO() {
        String sql = "CREATE TABLE IF NOT EXISTS books ("
                + "id VARCHAR(255) PRIMARY KEY, "
                + "title VARCHAR(255) NOT NULL, "
                + "authors TEXT, "
                + "description TEXT, "
                + "image MEDIUMBLOB, "
                + "pageCount INT, "
                + "publishDate VARCHAR(255), "
                + "publisher VARCHAR(255), "
                + "categories TEXT, "
                + "quantityInStock INT, "
                + "isbn VARCHAR(255) UNIQUE, "
                + "ratingSum BIGINT, "
                + "ratingCount INT"
                + ");";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Saves a book to the database.
     * If the book is not already in the database, it will be inserted.
     *
     * @param book the book to be saved.
     * @return the saved book, or null if an error occurs.
     */
    public Book save(Book book) {
        String sql = "INSERT INTO books "
                + "(id, title, authors, description, image, pageCount, publishDate, publisher, "
                + "categories, quantityInStock, isbn, ratingSum, ratingCount) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        boolean shouldCloseConnection = false;
        try {
            if (connectionTest == null) {
                connection = DatabaseConnection.getConnection();
                shouldCloseConnection = true;
            } else {
                connection = connectionTest;  // Utilize testing connection
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int cnt = 0;
                preparedStatement.setString(++cnt, book.getId());
                preparedStatement.setString(++cnt, book.getTitle());
                preparedStatement.setString(++cnt, String.join(",", book.getAuthors()));
                preparedStatement.setString(++cnt, book.getDescription());
                preparedStatement.setBytes(++cnt, book.getImage());
                preparedStatement.setInt(++cnt, book.getPageCount());
                preparedStatement.setString(++cnt, book.getPublishDate());
                preparedStatement.setString(++cnt, book.getPublisher());
                preparedStatement.setString(++cnt, String.join(",", book.getCategories()));
                preparedStatement.setInt(++cnt, book.getQuantityInStock());
                preparedStatement.setString(++cnt, book.getIsbn());
                preparedStatement.setLong(++cnt, book.getRatingSum());
                preparedStatement.setInt(++cnt, book.getRatingCount());
                preparedStatement.executeUpdate();

                System.out.println(preparedStatement);
                return book;
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
     * Deletes a book from the database by its ID.
     *
     * @param id the ID of the book to be deleted.
     * @throws BookNotFoundException if no book is found with the specified ID.
     */
    public void deleteById(String id)  {
        String sql = "DELETE FROM books WHERE id = ?";

        Connection connection = null;
        boolean shouldCloseConnection = false;

        try {
            if (connectionTest == null) {
                connection = DatabaseConnection.getConnection();
                shouldCloseConnection = true;
            } else {
                connection = connectionTest;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, id);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new BookNotFoundException("No book found with id " + id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
     * Finds all books in the database.
     *
     * @return a list of all books in the database.
     */
    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }
            return books;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Finds a random book from the database.
     *
     * @return a randomly selected book, or null if no books are found.
     */
    public Book findRandomBook() {
        String sql = "SELECT * FROM books ORDER BY RAND() LIMIT 1";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractBookFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Finds a book in the database by its ISBN.
     *
     * @param isbn the ISBN of the book to find.
     * @return the book with the specified ISBN, or null if no such book is found.
     * @throws BookNotFoundException if no book is found with the given ISBN.
     */
    public Book findByISBN (String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractBookFromResultSet(resultSet);
            } else {
                throw new BookNotFoundException("No book found with isbn " + isbn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Finds a book in the database by its ID.
     *
     * @param id the ID of the book to find.
     * @return the book with the specified ID, or null if no such book is found.
     * @throws BookNotFoundException if no book is found with the given ID.
     */
    public Book findById (String id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = (connectionTest ==null)
                    ? connection.prepareStatement(sql) : connectionTest.prepareStatement(sql);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractBookFromResultSet(resultSet);
            } else {
                throw new BookNotFoundException("No book found with id " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds books in the database by the author's name.
     *
     * @param author the name of the author to search for.
     * @return a list of books by the specified author, or null if no books are found.
     */
    public List<Book> findByAuthor(String author) {
        String sql = "SELECT * FROM books WHERE authors LIKE ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + author + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Book> books = new ArrayList<>();
                while (resultSet.next()) {
                    books.add(extractBookFromResultSet(resultSet));
                }
                return books;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds books in the database by the publisher name.
     *
     * @param publisher the name of the publisher to search for.
     * @return a list of books by the specified publisher, or null if no books are found.
     */
    public List<Book> findByPublisher(String publisher) {
        String sql = "SELECT * FROM books WHERE publisher LIKE ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + publisher + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Book> books = new ArrayList<>();
                while (resultSet.next()) {
                    books.add(extractBookFromResultSet(resultSet));
                }
                return books;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds books in the database by category.
     *
     * @param category the category to search for.
     * @return a list of books in the specified category, or null if no books are found.
     */
    public List<Book> findByCategory(String category) {
        String sql = "SELECT * FROM books WHERE categories LIKE ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + category + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();

            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }

            return books;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds books in the database by title.
     *
     * @param title the title to search for.
     * @return a list of books with the specified title, or null if no books are found.
     */
    public List<Book> findByTitle(String title) {
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + title + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Book> books = new ArrayList<>();

            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }

            return books;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Finds books borrowed by a specific user, filtered by the user's borrow status
     * and an additional book attribute (such as title, publisher, etc.).
     *
     * @param userId the ID of the user.
     * @param status the borrow status of the book.
     * @param query  the book attribute (e.g., title, publisher).
     * @param value  the value of the attribute to search for.
     * @return a list of books matching the user's borrow status and book attribute.
     */
    public List<Book> findByUserStatusAndBookAttr(Long userId, String status, String query, String value) {
        String sql = "SELECT b.* " +
                "FROM books b " +
                "JOIN borrows br ON br.bookId = b.id " +
                "WHERE br.userId = ? AND br.status = ? AND b." + query + " LIKE ?";

        List<Book> books = new ArrayList<>();

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, status);
            preparedStatement.setString(3,"%" + value + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }
            return books;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds the top N most borrowed books from the database based on the number of times
     * they have been borrowed, where the book status is 'Borrowed' or 'Returned'.
     *
     * @param n the number of top borrowed books to retrieve.
     * @return a list of pairs, where each pair consists of a Book and its borrow count.
     */
    public List<Pair<Book, Integer>> findTopNBorrowedBooks(int n) {
        String sql = "SELECT b.*, COUNT(br.bookId) AS borrowCount "
                + "FROM books b "
                + "LEFT JOIN borrows br ON b.id = br.bookId "
                + "AND (br.status = 'Borrowed' OR br.status = 'Returned') "
                + "GROUP BY b.id "
                + "ORDER BY borrowCount DESC "
                + "LIMIT ?";

        List<Pair<Book, Integer>> topBooks = new ArrayList<>();

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

             preparedStatement.setInt(1, n);

             ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Book book = extractBookFromResultSet(resultSet);
                int borrowCount = resultSet.getInt("borrowCount");

                topBooks.add(new Pair<>(book, borrowCount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topBooks;
    }

    /**
     * Updates the details of an existing book in the database.
     *
     * @param book the Book object containing the updated information.
     * @return the updated Book object if the update is successful, or null if no rows were updated.
     */
    public Book updateBook(Book book) {
        String sql = "UPDATE books " +
                "SET title = ?, authors = ?, description = ?, image = ?, pageCount = ?, publishDate = ?, publisher = ?, categories = ?, quantityInStock = ?, isbn = ?, ratingSum = ?, ratingCount = ? " +
                "WHERE id = ?";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            int cnt = 0;
            preparedStatement.setString(++cnt, book.getTitle());
            preparedStatement.setString(++cnt, String.join(",", book.getAuthors()));
            preparedStatement.setString(++cnt, book.getDescription());
            preparedStatement.setBytes(++cnt, book.getImage());
            preparedStatement.setInt(++cnt, book.getPageCount());
            preparedStatement.setString(++cnt, book.getPublishDate());
            preparedStatement.setString(++cnt, book.getPublisher());
            preparedStatement.setString(++cnt, String.join(",", book.getCategories()));
            preparedStatement.setInt(++cnt, book.getQuantityInStock());
            preparedStatement.setString(++cnt, book.getIsbn());
            preparedStatement.setLong(++cnt, book.getRatingSum());
            preparedStatement.setInt(++cnt, book.getRatingCount());
            preparedStatement.setString(++cnt, book.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Extracts a Book object from the given ResultSet.
     *
     * @param resultSet the ResultSet containing book data.
     * @return a Book object populated with data from the ResultSet.
     * @throws SQLException if there is an issue with extracting data from the ResultSet.
     */
    private Book extractBookFromResultSet(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getString("id"));
        book.setTitle(resultSet.getString("title"));
        book.setAuthors(resultSet.getString("authors").split(","));
        book.setDescription(resultSet.getString("description"));
        book.setImage(resultSet.getBytes("image"));
        book.setPageCount(resultSet.getInt("pageCount"));
        book.setPublishDate(resultSet.getString("publishDate"));
        book.setPublisher(resultSet.getString("publisher"));
        book.setCategories(resultSet.getString("categories").split(","));
        book.setQuantityInStock(resultSet.getInt("quantityInStock"));
        book.setIsbn(resultSet.getString("isbn"));
        book.setRatingSum(resultSet.getLong("ratingSum"));
        book.setRatingCount(resultSet.getInt("ratingCount"));
        return book;
    }


    /**
     * Retrieves the total count of books in the database.
     *
     * @return the total count of books.
     */
    public Integer getSum() {
        String sql = "SELECT COUNT(*) AS total_count FROM books";
        int sum = 0;
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
     * Retrieves the count of books grouped by their categories.
     *
     * @return a map where the keys are category names and the values are the count of books
     *         in each category.
     */
    public Map<String, Integer> getDataCategory() {
        String sql = "SELECT categories, COUNT(*) AS count FROM books GROUP BY categories";
        HashMap<String, Integer> categoryData = new HashMap<>();

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String category = resultSet.getString("categories");
                int count = resultSet.getInt("count");
                categoryData.put(category, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryData;
    }

    /**
     * Retrieves distinct categories from the books table based on a search query.
     *
     * @param query the search query used to filter the categories.
     * @return a list of distinct categories matching the query.
     */
    public List<String> getDistinctCategories(String query) {
        String sql = "SELECT DISTINCT categories FROM books WHERE categories LIKE ?";
        List<String> listCategories = new ArrayList<>();
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + query + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String category = resultSet.getString("categories");
                listCategories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listCategories;
    }

    /**
     * Retrieves a list of books that the user is currently reading (books with non-zero
     * pages read).
     *
     * @param userId the ID of the user.
     * @return a list of books the user is currently reading.
     */
    public List<Book> findReadingBookFromUser(Long userId) {
        String sql = "SELECT b.* "
                + "FROM borrows r "
                + "JOIN books b ON r.bookId = b.id "
                + "WHERE r.userId = ? AND r.status = 'Borrowing' "
                + "AND r.pagesRead > 0 "
                + "ORDER BY (CASE WHEN b.pageCount IS NOT NULL AND b.pageCount > 0 "
                + "THEN (r.pagesRead / b.pageCount) ELSE 0 END) DESC";

        List<Book> readingBooks = new ArrayList<>();

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                readingBooks.add(extractBookFromResultSet(resultSet));
            }
            return readingBooks;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves the reading completion percentage of a specific book for a given user.
     *
     * @param bookId the ID of the book.
     * @param userId the ID of the user.
     * @return the completion percentage of the book (0 if no progress or data is found).
     */
    public double getCompletionPercentage(String bookId, Long userId) {
        String sql = "SELECT (CASE WHEN b.pageCount IS NOT NULL AND b.pageCount != 0 "
                + "THEN (r.pagesRead / b.pageCount) ELSE 0 END) AS completionPercentage "
                + "FROM borrows r "
                + "JOIN books b ON r.bookId = b.id "
                + "WHERE r.userId = ? AND r.bookId = ? AND r.status = 'Borrowing'";

        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, bookId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("completionPercentage");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;

    }

}