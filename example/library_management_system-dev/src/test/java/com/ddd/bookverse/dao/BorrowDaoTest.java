package com.ddd.bookverse.dao;

import com.ddd.bookverse.exception.BorrowNotFoundException;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.model.Borrow;
import com.ddd.bookverse.model.Member;
import com.ddd.bookverse.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static com.ddd.bookverse.config.DatabaseConnectionTest.createConnection;
import static javafx.beans.binding.Bindings.when;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class BorrowDaoTest {
    private static Connection connection;
    private BookDAO bookDAO;
    private BorrowDAO borrowDAO;
    private UserDAO userDAO;
    private static Book testBook = new Book();
    @BeforeEach
    public void setUp() throws SQLException {
        getConnection();
        connection.setAutoCommit(false);
        bookDAO = new BookDAO(connection);
        userDAO = new UserDAO(connection);
        borrowDAO = new BorrowDAO(connection,userDAO,bookDAO);
        connection = createConnection(connection);
        testBook.setId("1RKjZlrYouEC");
        testBook.setTitle("Book 1");
        testBook.setAuthors(new String[] {"Author1"});
        testBook.setDescription("Description 1");
        testBook.setPageCount(100);
        testBook.setPublishDate("2024-01-01");
        testBook.setPublisher("Publisher1");
        testBook.setCategories(new String[] {"Category1"});
        testBook.setQuantityInStock(10);
        testBook.setIsbn("1234567890");
        testBook.setRatingSum(100L);
        testBook.setRatingCount(10);
        bookDAO.save(testBook);

    }

    public static void getConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @Test
    public void testSave() {
        User testUser = userDAO.save(new Member("testuser", "password123", "test@example.com", "Test User"));
        Borrow borrow = new Borrow();
        borrow.setUser(testUser);
        borrow.setBook(testBook);
        borrow.setBorrowDate(LocalDate.of(2024, 11, 29));
        borrow.setReturnDate(LocalDate.of(2024, 12, 29));
        borrow.setStatus("Borrowing");
        borrow.setPagesRead(50);

        Borrow savedBorrow = borrowDAO.save(borrow);
        assertNotNull(savedBorrow, "Borrow should be saved successfully");
        assertEquals(borrow.getUser().getId(), savedBorrow.getUser().getId(), "User ID should match");
        assertEquals(borrow.getBook().getId(), savedBorrow.getBook().getId(), "Book ID should match");
    }


    @Test
    public void testFindById() {
        // Insert a borrow record first
        User testUser = userDAO.save(new Member("testuser",
                "password123", "test@example.com", "Test User"));
        Borrow borrow = new Borrow();
        borrow.setUser(testUser);
        borrow.setBook(testBook);
        borrow.setBorrowDate(LocalDate.of(2024, 11, 29));
        borrow.setReturnDate(LocalDate.of(2024, 12, 29));
        borrow.setStatus("Borrowing");
        borrow.setPagesRead(100);
        Borrow savedBorrow = borrowDAO.save(borrow);
        Borrow foundBorrow = borrowDAO.findById(borrow.getId());
        assertNotNull(foundBorrow, "Borrow should be found by ID");
        assertEquals(savedBorrow.getId(), foundBorrow.getId(), "Borrow ID should match");
    }

    @Test
    public void testFindByUser() {
        // Insert a borrow record first
        User testUser = userDAO.save(new Member("testuser",
                "password123", "test@example.com", "Test User"));
        Borrow borrow = new Borrow();
        borrow.setUser(testUser);
        borrow.setBook(testBook);
        borrow.setBorrowDate(LocalDate.of(2024, 11, 29));
        borrow.setReturnDate(LocalDate.of(2024, 12, 29));
        borrow.setStatus("Borrowing");
        borrow.setPagesRead(200);
        Borrow savedBorrow = borrowDAO.save(borrow);
        List<Borrow> borrows = borrowDAO.findByUser(testUser.getId());
        assertNotNull(borrows, "Borrows should be found by user ID");
        assertFalse("Borrows list should not be empty", borrows.isEmpty());
    }

    @Test
    public void testUpdateStatus() {
        // Insert a borrow record first
        User testUser = userDAO.save(new Member("testuser", "password123", "test@example.com", "Test User"));
        Borrow borrow = new Borrow();
        borrow.setUser(testUser);
        borrow.setBook(testBook);
        borrow.setBorrowDate(LocalDate.of(2024, 11, 29));
        borrow.setReturnDate(LocalDate.of(2024, 12, 29));
        borrow.setStatus("Borrowing");
        borrow.setPagesRead(150);
        Borrow savedBorrow = borrowDAO.save(borrow);
        borrowDAO.updateStatus(savedBorrow.getId(), "Returned");
    }


    @Test
    public void testDeleteById() {
        // Insert a borrow record first
        User testUser = userDAO.save(new Member("testuser", "password123", "test@example.com", "Test User"));
        Borrow borrow = new Borrow();
        borrow.setUser(testUser);
        borrow.setBook(testBook);
        borrow.setBorrowDate(LocalDate.of(2024, 11, 29));
        borrow.setReturnDate(LocalDate.of(2024, 12, 29));
        borrow.setStatus("Borrowing");
        borrow.setPagesRead(150);
        Borrow savedBorrow = borrowDAO.save(borrow);
        borrowDAO.deleteById(savedBorrow.getId());
    }

    @Test
    public void testFindLatestBorrowOfABookFromUser() {
        // Tạo dữ liệu người dùng và sách
        User testUser = userDAO.save(new Member("testuser", "password123", "test@example.com", "Test User"));
        String bookId = testBook.getId();

        // Tạo và lưu các bản ghi mượn sách
        Borrow borrow1 = new Borrow();
        borrow1.setUser(testUser);
        borrow1.setBook(testBook);
        borrow1.setBorrowDate(LocalDate.of(2024, 11, 1));
        borrow1.setReturnDate(LocalDate.of(2024, 11, 15));
        borrow1.setStatus("Returned");
        borrow1.setPagesRead(50);
        borrowDAO.save(borrow1);

        Borrow borrow2 = new Borrow();
        borrow2.setUser(testUser);
        borrow2.setBook(testBook);
        borrow2.setBorrowDate(LocalDate.of(2024, 11, 20));
        borrow2.setReturnDate(LocalDate.of(2024, 12, 5));
        borrow2.setStatus("Borrowing");
        borrow2.setPagesRead(50);
        borrowDAO.save(borrow2);

        // Gọi phương thức cần kiểm tra
        Borrow latestBorrow = borrowDAO.findLatestBorrowOfABookFromUser(testUser.getId(), bookId);

        // Kiểm tra kết quả
        assertNotNull(latestBorrow, "Latest borrow record should not be null");
        assertEquals(borrow2.getId(), latestBorrow.getId(), "The latest borrow record should match the expected record");
        assertEquals(testUser.getId(), latestBorrow.getUser().getId(), "User ID should match");
        assertEquals(bookId, latestBorrow.getBook().getId(), "Book ID should match");
    }

    @Test
    public void testFindByUserAndStatus() {
        // Tạo dữ liệu người dùng và sách
        User testUser = userDAO.save(new Member("testuser", "password123", "test@example.com", "Test User"));

        // Tạo và lưu các bản ghi mượn sách với các trạng thái khác nhau
        Borrow borrow1 = new Borrow();
        borrow1.setUser(testUser);
        borrow1.setBook(testBook);
        borrow1.setBorrowDate(LocalDate.of(2024, 11, 1));
        borrow1.setReturnDate(LocalDate.of(2024, 11, 15));
        borrow1.setStatus("Borrowing");
        borrow1.setPagesRead(50);
        borrowDAO.save(borrow1);

        Borrow borrow2 = new Borrow();
        borrow2.setUser(testUser);
        borrow2.setBook(testBook);
        borrow2.setBorrowDate(LocalDate.of(2024, 11, 16));
        borrow2.setReturnDate(LocalDate.of(2024, 12, 5));
        borrow2.setStatus("Returned");
        borrow2.setPagesRead(100);
        borrowDAO.save(borrow2);

        // Tạo thêm sách khác và lưu bản ghi mượn
        Book anotherBook = new Book();
        anotherBook.setId("AnotherBookID");
        anotherBook.setTitle("Book 2");
        anotherBook.setAuthors(new String[] {"Author2"});
        anotherBook.setDescription("Description 2");
        anotherBook.setPageCount(150);
        anotherBook.setPublishDate("2023-01-01");
        anotherBook.setPublisher("Publisher2");
        anotherBook.setCategories(new String[] {"Category2"});
        anotherBook.setQuantityInStock(5);
        anotherBook.setIsbn("9876543210");
        anotherBook.setRatingSum(200L);
        anotherBook.setRatingCount(20);
        bookDAO.save(anotherBook);

        Borrow borrow3 = new Borrow();
        borrow3.setUser(testUser);
        borrow3.setBook(anotherBook);
        borrow3.setBorrowDate(LocalDate.of(2024, 11, 20));
        borrow3.setReturnDate(LocalDate.of(2024, 12, 10));
        borrow3.setStatus("Borrowing");
        borrow3.setPagesRead(30);
        borrowDAO.save(borrow3);

        // Gọi phương thức cần kiểm tra với trạng thái "Borrowing"
        List<String> borrowedBookIds = borrowDAO.findByUserAndStatus(testUser.getId(), "Borrowing");

        // Kiểm tra kết quả
        assertNotNull(borrowedBookIds, "Borrowing book IDs list should not be null");
        assertEquals(2, borrowedBookIds.size(), "There should be 2 borrowed books");
        assertTrue(borrowedBookIds.contains(testBook.getId()), "Book 1 should be in the list of borrowed books");
        assertTrue(borrowedBookIds.contains(anotherBook.getId()), "Book 2 should be in the list of borrowed books");

    }

    @Test
    public void testCountByUserAndStatus() {
        // Tạo dữ liệu người dùng và sách
        User testUser = userDAO.save(new Member("testuser", "password123", "test@example.com", "Test User"));

        // Tạo và lưu các bản ghi mượn sách với các trạng thái khác nhau
        Borrow borrow1 = new Borrow();
        borrow1.setUser(testUser);
        borrow1.setBook(testBook);
        borrow1.setBorrowDate(LocalDate.of(2024, 11, 1));
        borrow1.setReturnDate(LocalDate.of(2024, 11, 15));
        borrow1.setStatus("Borrowing");
        borrow1.setPagesRead(50);
        borrowDAO.save(borrow1);

        // Tạo thêm sách khác và lưu bản ghi mượn
        Book anotherBook = new Book();
        anotherBook.setId("AnotherBookID");
        anotherBook.setTitle("Book 2");
        anotherBook.setAuthors(new String[] {"Author2"});
        anotherBook.setDescription("Description 2");
        anotherBook.setPageCount(150);
        anotherBook.setPublishDate("2023-01-01");
        anotherBook.setPublisher("Publisher2");
        anotherBook.setCategories(new String[] {"Category2"});
        anotherBook.setQuantityInStock(5);
        anotherBook.setIsbn("9876543210");
        anotherBook.setRatingSum(200L);
        anotherBook.setRatingCount(20);
        bookDAO.save(anotherBook);

        Borrow borrow2 = new Borrow();
        borrow2.setUser(testUser);
        borrow2.setBook(anotherBook);
        borrow2.setBorrowDate(LocalDate.of(2024, 11, 20));
        borrow2.setReturnDate(LocalDate.of(2024, 12, 10));
        borrow2.setStatus("Borrowing");
        borrow2.setPagesRead(30);
        borrowDAO.save(borrow2);

        Borrow borrow3 = new Borrow();
        borrow3.setUser(testUser);
        borrow3.setBook(testBook);
        borrow3.setBorrowDate(LocalDate.of(2024, 11, 16));
        borrow3.setReturnDate(LocalDate.of(2024, 12, 5));
        borrow3.setStatus("Returned");
        borrow3.setPagesRead(100);
        borrowDAO.save(borrow3);

        // Gọi phương thức cần kiểm tra với trạng thái "Borrowing"
        int borrowedCount = borrowDAO.countByUserAndStatus(testUser.getId(), "Borrowing");

        // Kiểm tra kết quả
        assertEquals(2, borrowedCount, "There should be 2 unique borrowed books for the user");
    }

    @Test
    public void testCountByUserAndStatus_NoBorrows() {
        int count = borrowDAO.countByUserAndStatus(999L, "Borrowing");
        assertEquals(0, count, "Count should be 0 for a non-existent user");
    }

    @Test
    public void testCountByUserAndStatus_NoMatchStatus() {
        User testUser = userDAO.save(new Member("testuser2", "password123", "test2@example.com", "Test User 2"));
        int count = borrowDAO.countByUserAndStatus(testUser.getId(), "Borrowing");
        assertEquals(0, count, "Count should be 0 for a valid user with no borrows for the given status");
    }

    @Test
    public void testGetSum() {
        User testUser1 = userDAO.save(new Member("testuser1", "password123", "user1@example.com", "Test User 1"));
        User testUser2 = userDAO.save(new Member("testuser2", "password123", "user2@example.com", "Test User 2"));
        Borrow borrow1 = new Borrow();
        borrow1.setUser(testUser1);
        borrow1.setBook(testBook);
        borrow1.setBorrowDate(LocalDate.of(2024, 11, 1));
        borrow1.setReturnDate(LocalDate.of(2024, 11, 15));
        borrow1.setStatus("Borrowing");
        borrow1.setPagesRead(50);
        borrowDAO.save(borrow1);

        Borrow borrow2 = new Borrow();
        borrow2.setUser(testUser2);
        borrow2.setBook(testBook);
        borrow2.setBorrowDate(LocalDate.of(2024, 11, 16));
        borrow2.setReturnDate(LocalDate.of(2024, 12, 1));
        borrow2.setStatus("Returned");
        borrow2.setPagesRead(30);
        borrowDAO.save(borrow2);

        // Thêm một bản ghi mượn nữa
        Borrow borrow3 = new Borrow();
        borrow3.setUser(testUser1);
        borrow3.setBook(testBook);
        borrow3.setBorrowDate(LocalDate.of(2024, 12, 1));
        borrow3.setReturnDate(LocalDate.of(2024, 12, 15));
        borrow3.setStatus("Borrowing");
        borrow3.setPagesRead(70);
        borrowDAO.save(borrow3);

        Integer finalSum = borrowDAO.getSum();
        assertEquals(3, finalSum, "Total count should be 3 after adding 3 borrows");
    }

    @Test
    public void testFindLatestNBorrows() {
        User testUser1 = userDAO.save(new Member("testuser1", "password123", "user1@example.com", "Test User 1"));
        User testUser2 = userDAO.save(new Member("testuser2", "password123", "user2@example.com", "Test User 2"));

        Borrow borrow1 = new Borrow();
        borrow1.setUser(testUser1);
        borrow1.setBook(testBook);
        borrow1.setBorrowDate(LocalDate.of(2024, 11, 1));
        borrow1.setReturnDate(LocalDate.of(2024, 11, 15));
        borrow1.setStatus("Borrowing");
        borrow1.setPagesRead(50);
        borrowDAO.save(borrow1);

        Borrow borrow2 = new Borrow();
        borrow2.setUser(testUser2);
        borrow2.setBook(testBook);
        borrow2.setBorrowDate(LocalDate.of(2024, 11, 16));
        borrow2.setReturnDate(LocalDate.of(2024, 12, 1));
        borrow2.setStatus("Returned");
        borrow2.setPagesRead(100);
        borrowDAO.save(borrow2);

        Borrow borrow3 = new Borrow();
        borrow3.setUser(testUser1);
        borrow3.setBook(testBook);
        borrow3.setBorrowDate(LocalDate.of(2024, 12, 1));
        borrow3.setReturnDate(LocalDate.of(2024, 12, 15));
        borrow3.setStatus("Plan to borrow"); // Sẽ bị bỏ qua
        borrow3.setPagesRead(0);
        borrowDAO.save(borrow3);

        Borrow borrow4 = new Borrow();
        borrow4.setUser(testUser2);
        borrow4.setBook(testBook);
        borrow4.setBorrowDate(LocalDate.of(2024, 12, 16));
        borrow4.setReturnDate(LocalDate.of(2024, 12, 31));
        borrow4.setStatus("Borrowing");
        borrow4.setPagesRead(200);
        borrowDAO.save(borrow4);

        // Kiểm tra: Lấy 2 bản ghi mượn sách gần nhất (ngoại trừ "Plan to borrow")
        List<Borrow> latestBorrows = borrowDAO.findLatestNBorrows(2);

        assertNotNull(latestBorrows, "Latest borrows list should not be null");
        assertEquals(2, latestBorrows.size(), "There should be 2 latest borrows");

        // Kiểm tra thứ tự và nội dung
        assertEquals(borrow4.getId(), latestBorrows.get(0).getId(), "The latest borrow should be the first in the list");
        assertEquals(borrow2.getId(), latestBorrows.get(1).getId(), "The second latest borrow should be the second in the list");
    }

    @Test
    public void testFindAllBorrowsOfABookFromUser() {

        // Tạo dữ liệu giả
        User testUser = userDAO.save(new Member("testuserborrows", "password123", "userborrows@example.com", "Test User"));

        Borrow borrow1 = new Borrow();
        borrow1.setUser(testUser);
        borrow1.setBook(testBook);
        borrow1.setBorrowDate(LocalDate.of(2024, 11, 1));
        borrow1.setReturnDate(LocalDate.of(2024, 11, 15));
        borrow1.setStatus("Borrowing");
        borrow1.setPagesRead(50);
        borrowDAO.save(borrow1);

        Borrow borrow2 = new Borrow();
        borrow2.setUser(testUser);
        borrow2.setBook(testBook);
        borrow2.setBorrowDate(LocalDate.of(2024, 11, 16));
        borrow2.setReturnDate(LocalDate.of(2024, 12, 1));
        borrow2.setStatus("Returned");
        borrow2.setPagesRead(100);
        borrowDAO.save(borrow2);

        Borrow borrow3 = new Borrow();
        borrow3.setUser(testUser);
        borrow3.setBook(testBook);
        borrow3.setBorrowDate(LocalDate.of(2024, 12, 1));
        borrow3.setReturnDate(LocalDate.of(2024, 12, 15));
        borrow3.setStatus("Plan to borrow");
        borrow3.setPagesRead(0);
        borrowDAO.save(borrow3);

        // Gọi phương thức để kiểm tra
        List<Borrow> borrows = borrowDAO.findAllBorrowsOfABookFromUser(testBook.getId(), testUser.getId());

        // Kiểm tra danh sách trả về
        assertNotNull(borrows, "The borrows list should not be null");
        assertEquals(3, borrows.size(), "There should be 3 borrows for this user and book");

        // Kiểm tra thứ tự
        assertEquals(borrow3.getId(), borrows.get(0).getId(), "The latest borrow should be first in the list");
        assertEquals(borrow2.getId(), borrows.get(1).getId(), "The second latest borrow should be second in the list");
        assertEquals(borrow1.getId(), borrows.get(2).getId(), "The third latest borrow should be last in the list");

    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

}
