package com.ddd.bookverse.dao;

import com.ddd.bookverse.config.DatabaseConnection;
import com.ddd.bookverse.exception.UserNotFoundException;
import com.ddd.bookverse.model.Admin;
import com.ddd.bookverse.model.Member;
import com.ddd.bookverse.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for interacting with the users table in the database.
 */
public class UserDAO {
    private Connection connectionTest;

    /**
     * Constructs a UserDAO with a provided database connection for testing.
     *
     * @param connection the connection to be used for database operations.
     */
    public UserDAO(Connection connection) {
        this.connectionTest = connection;
    }

    /**
     * Constructs a UserDAO and initializes the users table if it does not exist.
     */
    public UserDAO() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) NOT NULL UNIQUE," +
                "fullName VARCHAR(255)," +
                "role ENUM('ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER'" +
                ");";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts a user from the given ResultSet.
     *
     * @param resultSet the ResultSet containing user data.
     * @return a User object populated with the ResultSet data.
     * @throws SQLException if an error occurs while reading the ResultSet.
     */
    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        String role = resultSet.getString("role");
        User user;
        if ("ADMIN".equals(role)) {
            user = new Admin();
        } else {
            user = new Member();
        }
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setId(resultSet.getLong("id"));
        user.setPassword(resultSet.getString("password"));
        user.setFullName(resultSet.getString("fullName"));
        user.setId(resultSet.getLong("id"));
        return user;
    }



    /**
     * Saves a new user to the database.
     *
     * @param user the User object to be saved.
     * @return the saved User object with its generated ID, or null if an error occurs.
     */
    public User save(User user) {
        String sql = "INSERT INTO Users (username, password, email, fullName, role) "
                + "VALUES (?, ?, ?, ?, ?)";
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
                preparedStatement.setString(++cnt, user.getUsername());
                preparedStatement.setString(++cnt, user.getPassword());
                preparedStatement.setString(++cnt, user.getEmail());
                preparedStatement.setString(++cnt, user.getFullName());
                preparedStatement.setString(++cnt, user.getRole());

                preparedStatement.executeUpdate();
                System.out.println(preparedStatement);
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);
                        user.setId(generatedId);
                    }
                }
                return user;
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
     * Finds a user by their username.
     *
     * @param username the username to search for.
     * @return the User object if found.
     * @throws UserNotFoundException if no user with the given username is found.
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractUserFromResultSet(resultSet);
            } else {
                throw new UserNotFoundException("No user found with username: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds users by a partial username match and role.
     *
     * @param username the partial username to match.
     * @param role     the role to filter by.
     * @return a list of matching User objects, or an empty list if none are found.
     */
    public List<User> findByUsernameAndRole(String username, String role) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE username LIKE ? AND role = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + username + "%");
            preparedStatement.setString(2, role);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userList.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * Finds users by ID and role.
     *
     * @param id   the ID to search for.
     * @param role the role to filter by.
     * @return a list of matching User objects, or an empty list if none are found.
     */
    public List<User> findByIdAndRole(Long id, String role) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE id = ? AND role = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, String.valueOf(id));
            preparedStatement.setString(2, role);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userList.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }


    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to search for.
     * @return the User object if found.
     * @throws UserNotFoundException if no user with the given ID is found.
     */
    public User findById(Long id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        Connection connection = null;
        boolean isOwnConnection = false;

        try {
            connection = (this.connectionTest != null) ? this.connectionTest : DatabaseConnection.getConnection();
            isOwnConnection = (this.connectionTest == null);

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractUserFromResultSet(resultSet);
            } else {
                throw new UserNotFoundException("No user found with id: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (isOwnConnection && connection != null) {
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
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     * @throws UserNotFoundException if no user with the given ID exists.
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("No user found with id: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the information of an existing user.
     *
     * @param user the User object containing updated details.
     */
    public void update(User user) {
        String sql = "UPDATE Users SET username = ?, password = ?, email = ?, fullName = ?, role = ? WHERE id = ?";
        Connection connection = null;
        boolean shouldCloseConnection = false;
        try {
            if (this.connectionTest == null) {
                connection = DatabaseConnection.getConnection();
                shouldCloseConnection = true;
            } else {
                connection = this.connectionTest;
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getFullName());
                preparedStatement.setString(5, user.getRole());
                preparedStatement.setLong(6, user.getId());

                preparedStatement.executeUpdate();
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
     * Finds a user by their email.
     *
     * @param email the email of the user to search for.
     * @return the User object if found.
     * @throws UserNotFoundException if no user with the given email is found.
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractUserFromResultSet(resultSet);
            } else {
                throw new UserNotFoundException("No user found with email: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds users by their email and role.
     *
     * @param email the email to search for (supports partial matching).
     * @param role the role of the users to search for.
     * @return a list of users matching the given email and role.
     */
    public List<User> findByEmailAndRole(String email, String role) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE email LIKE ? AND role = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + email + "%");
            preparedStatement.setString(2, role );
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userList.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }


    /**
     * Finds users by their full name and role.
     *
     * @param fullName the full name to search for (supports partial matching).
     * @param role the role of the users to search for.
     * @return a list of users matching the given full name and role.
     */
    public List<User> findAllByFullNameAndRole(String fullName, String role) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE fullName LIKE ? AND role = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + fullName + "%");
            preparedStatement.setString(2, role );
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userList.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * Retrieves the total count of users in the database.
     *
     * @return the total number of users.
     */
    public Integer getSum() {
        String sql = "SELECT COUNT(*) AS total_count FROM users";
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
     * Finds all users with the specified role.
     *
     * @param role the role of the users to search for.
     * @return a list of users with the given role.
     */
    public List<User> findAllByRole(String role) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role = ?";
        try (Connection connection = (this.connectionTest != null)
                ? this.connectionTest : DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, role);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userList.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

}
