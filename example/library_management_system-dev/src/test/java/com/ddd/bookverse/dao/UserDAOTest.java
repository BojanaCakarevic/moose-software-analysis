package com.ddd.bookverse.dao;

import com.ddd.bookverse.model.Admin;
import com.ddd.bookverse.model.Member;
import com.ddd.bookverse.model.User;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;

import static com.ddd.bookverse.config.DatabaseConnectionTest.createConnection;
import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO userDao;
    private Connection connection;

    @BeforeEach
    void setup() throws SQLException {
        // Create an in-memory H2 database for testing
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        userDao = new UserDAO(connection);
        connection = createConnection(connection);
    }

    @AfterEach
    void teardown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testDeleteById() throws SQLException {
        User user = userDao.save(new Member("testuserdelete", "password123", "test@exampledelete.com", "Test User"));
        userDao.deleteById(user.getId());
    }

    @Test
    void testSave() throws SQLException {
        User user = new Member("test", "password123", "user@example.com", "Test User");
        User savedUser = userDao.save(user);
        assertNotNull(savedUser);
        assertEquals("test", savedUser.getUsername());
    }

    @Test
    void testFindByUsername() throws SQLException {
        User user = new Member("testnewfind", "password123", "user5@example.com", "Test User");
        userDao.save(user);

        User foundUser = userDao.findByUsername("testnewfind");
        assertNotNull(foundUser);
        assertEquals("testnewfind", foundUser.getUsername());
        System.out.println("hoan thanh");
    }

    @Test
    void testFindAllByUserName() throws SQLException {
        userDao.save(new Member("user1", "pass1", "email1@example.com", "User One"));
        userDao.save(new Member("user2", "pass2", "email2@example.com", "User Two"));
        List<User> users = userDao.findByUsernameAndRole("user","MEMBER");
        assertEquals(2, users.size());
    }


    @Test
    void testUpdate() throws SQLException {
        User user = userDao.save(new Member("testuserupdate", "password123", "test@exampleupdate.com", "Test User"));
        user.setFullName("updatedUser");
        user.setEmail("updated@example.com");
        userDao.update(user);
        User updatedUser = userDao.findByUsername(user.getUsername());
        assertEquals("updatedUser", updatedUser.getFullName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void testFindAllByRole() throws SQLException {
        userDao.save(new Admin("admin1", "pass1", "admin1@example.com", "Admin One"));
        userDao.save(new Admin("admin2", "pass2", "admin2@example.com", "Admin Two"));
        userDao.save(new Member("user1", "pass3", "user1@example.com", "User One"));

        List<User> admins = userDao.findAllByRole("ADMIN");
        assertEquals(2, admins.size());
    }

    @Test
    void testGetSum() throws SQLException {
        userDao.save(new Member("user1", "pass1", "user1@example.com", "User One"));
        userDao.save(new Member("user2", "pass2", "user2@example.com", "User Two"));
        Integer totalUsers = userDao.getSum();
        assertEquals(6, totalUsers);
    }
}
