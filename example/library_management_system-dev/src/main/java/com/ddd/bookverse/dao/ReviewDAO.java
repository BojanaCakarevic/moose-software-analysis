package com.ddd.bookverse.dao;

import com.ddd.bookverse.config.DatabaseConnection;
import com.ddd.bookverse.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for interacting with the reviews table in the database.
 */
public class ReviewDAO {
    private Connection connectionTest;

    /**
     * Constructs a ReviewDAO with a provided database connection for testing.
     *
     * @param connectionTest the connection to be used for database operations.
     */
    public ReviewDAO(Connection connectionTest) {
        this.connectionTest = connectionTest;
    }

    /**
     * Constructs a ReviewDAO and initializes the reviews table if it does not exist.
     */
    public ReviewDAO() {
        String sql = "CREATE TABLE IF NOT EXISTS reviews ("
                + "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                + "bookId VARCHAR(255) NOT NULL,"
                + "userId BIGINT NOT NULL,"
                + "content TEXT,"
                + "rating INT CHECK (rating BETWEEN 1 AND 5),"
                + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (bookId) REFERENCES books(id) ON DELETE CASCADE, "
                + "FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE"
                + ");";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a review in the database.
     *
     * @param review the review to be saved.
     * @return the saved review with the generated ID.
     */
    public Review save(Review review) {
        String sql = "INSERT INTO reviews (bookId, userId, content, rating) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        boolean shouldCloseConnection = false;
        try {
            if (this.connectionTest != null) {
                connection = this.connectionTest;
            } else {
                connection = DatabaseConnection.getConnection();
                shouldCloseConnection = true;
            }
            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, review.getBookId());
            preparedStatement.setLong(2, review.getUserId());
            preparedStatement.setString(3, review.getContent());
            preparedStatement.setInt(4, review.getRating());

            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    review.setId(generatedId);
                }
            }
            System.out.println(preparedStatement);
            return review;
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
        return null;
    }

    /**
     * Deletes a review by its ID.
     *
     * @param id the ID of the review to be deleted.
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM Reviews WHERE id = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No review found with id: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates an existing review.
     *
     * @param review the review to be updated.
     */
    public void update(Review review) {
        String sql = "UPDATE Reviews SET content = ?, rating = ? WHERE id = ?";
        Connection connection = null;
        try {
            if (this.connectionTest != null) {
                connection = this.connectionTest;
            } else {
                connection = DatabaseConnection.getConnection();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, review.getContent());
                preparedStatement.setInt(2, review.getRating());
                preparedStatement.setLong(3, review.getId());

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (this.connectionTest == null && connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Retrieves all reviews for a given book.
     *
     * @param bookId the ID of the book.
     * @return a list of reviews for the book.
     */
    public List<Review> findALLByBookId(String bookId) {
        String sql = "SELECT * FROM Reviews WHERE bookId = ?";
        List<Review> reviewList = new ArrayList<>();
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, bookId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reviewList.add(extractReviewFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviewList;
    }


    /**
     * Extracts a review from the ResultSet.
     *
     * @param resultSet the ResultSet containing review data.
     * @return the extracted review.
     * @throws SQLException if an SQL error occurs while extracting the data.
     */
    private Review extractReviewFromResultSet(ResultSet resultSet) throws SQLException{
        Review review = new Review();
        review.setId(resultSet.getLong("id"));
        review.setBookId(resultSet.getString("bookId"));
        review.setUserId(resultSet.getLong("userId"));
        review.setContent(resultSet.getString("content"));
        review.setRating(resultSet.getInt("rating"));
        return review;
    }


    /**
     * Retrieves a review by book ID and user ID.
     *
     * @param bookId the ID of the book.
     * @param userId the ID of the user.
     * @return the review corresponding to the book and user.
     * @throws RuntimeException if no review is found for the provided IDs.
     */
    public Review findAllByBookIdAndUserId(String bookId, Long userId) {
        String sql = "SELECT * FROM Reviews WHERE bookId = ? AND userId = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, bookId);
            preparedStatement.setLong(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractReviewFromResultSet(resultSet);
            } else {
                throw new RuntimeException("No review found with bookId: " + bookId + " and userId: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

