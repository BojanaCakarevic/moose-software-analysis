package com.ddd.bookverse.dao;

import com.ddd.bookverse.dao.ReviewDAO;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.model.Member;
import com.ddd.bookverse.model.Review;
import com.ddd.bookverse.model.User;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static com.ddd.bookverse.config.DatabaseConnectionTest.createConnection;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewDAOTest {
    private static Connection connection;
    private ReviewDAO reviewDAO;
    private BookDAO bookDAO;
    private BorrowDAO borrowDAO;
    private UserDAO userDAO;
    private static Book testBook = new Book();

    public static void setUpClass() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @BeforeEach
    public void setUp() throws SQLException {
        setUpClass();
        connection.setAutoCommit(false); // Tắt auto-commit để sử dụng giao dịch
        bookDAO = new BookDAO(connection);
        userDAO = new UserDAO(connection);
        borrowDAO = new BorrowDAO(connection,userDAO,bookDAO);
        reviewDAO = new ReviewDAO(connection);
        connection = createConnection(connection);
        testBook.setId("wkAQAAMAAJ");
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


    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void testSaveReview() {
        User testUser = new Member("testuser", "password123", "test@example.com", "Test User");
        userDAO.save(testUser);

        Review review = new Review();
        review.setBookId(testBook.getId()); // Gán ID sách vào Review
        review.setUserId(testUser.getId()); // Gán ID người dùng vào Review
        review.setContent("This is a great book!"); // Nội dung đánh giá
        review.setRating(5); // Đánh giá 5 sao
        Review savedReview =  reviewDAO.save(review);

        assertNotNull(savedReview, "Review should be saved successfully");
        assertEquals(review.getBookId(), savedReview.getBookId(), "Book ID should match");
        assertEquals(review.getUserId(), savedReview.getUserId(), "User ID should match");
        assertEquals(review.getContent(), savedReview.getContent(), "Review content should match");
        assertEquals(review.getRating(), savedReview.getRating(), "Review rating should match");
    }

    @Test
    public void testDeleteById() {
        User testUser = new Member("testuser", "password123", "test@example.com", "Test User");
        testUser = userDAO.save(testUser); // Giả sử save() trả về user sau khi lưu thành công

        Review review = new Review();
        review.setBookId(testBook.getId());
        review.setUserId(testUser.getId());
        review.setContent("This is a great book!");
        review.setRating(5);

        Review savedReview = reviewDAO.save(review);
        assertNotNull(savedReview, "Review should be saved successfully");

        Long reviewId = savedReview.getId();
        reviewDAO.deleteById(reviewId);
    }

    @Test
    public void testUpdateReview() {
        User testUser = new Member("testuser", "password123", "test@example.com", "Test User");
        testUser = userDAO.save(testUser);
        Review review = new Review();
        review.setBookId(testBook.getId()); // Gán ID sách vào Review
        review.setUserId(testUser.getId()); // Gán ID người dùng vào Review
        review.setContent("This is a great book!"); // Nội dung đánh giá ban đầu
        review.setRating(4); // Đánh giá ban đầu là 4 sao
        Review savedReview = reviewDAO.save(review);

        assertNotNull(savedReview, "Review should be saved successfully");
        Long reviewId = savedReview.getId();
        savedReview.setContent("This is an excellent book!"); // Nội dung cập nhật
        savedReview.setRating(5); // Đánh giá cập nhật là 5 sao
        reviewDAO.update(savedReview);
        Review updatedReview = reviewDAO.findAllByBookIdAndUserId(testBook.getId(), testUser.getId());

        assertNotNull(updatedReview, "Updated review should be found");
        assertEquals("This is an excellent book!", updatedReview.getContent(), "Review content should be updated");
        assertEquals(5, updatedReview.getRating(), "Review rating should be updated to 5");
    }

    @Test
    public void testFindALLByBookId() throws SQLException {
        User testUser = new Member("testuser", "password123", "test@example.com", "Test User");
        testUser = userDAO.save(testUser);
        Review review1 = new Review();
        review1.setBookId(testBook.getId());
        review1.setUserId(testUser.getId());
        review1.setContent("Great book!");
        review1.setRating(5);
        reviewDAO.save(review1);
        Review review2 = new Review();
        review2.setBookId(testBook.getId());
        review2.setUserId(testUser.getId());
        review2.setContent("Good read.");
        review2.setRating(4);
        reviewDAO.save(review2);

        List<Review> reviews = reviewDAO.findALLByBookId(testBook.getId());

        assertNotNull(reviews, "Reviews should not be null");
        assertEquals(2, reviews.size(), "There should be two reviews for the book");
        assertEquals("Great book!", reviews.get(0).getContent(), "First review content should match");
        assertEquals(5, reviews.get(0).getRating(), "First review rating should match");
        assertEquals("Good read.", reviews.get(1).getContent(), "Second review content should match");
        assertEquals(4, reviews.get(1).getRating(), "Second review rating should match");
    }


    @Test
    public void testFindAllByBookIdAndUserId() throws SQLException {
        // Tạo và lưu User
        User testUser = new Member("testuser", "password123", "test@example.com", "Test User");
        testUser = userDAO.save(testUser);

        // Tạo và lưu Review cho Book và User này
        Review review = new Review();
        review.setBookId(testBook.getId());
        review.setUserId(testUser.getId());
        review.setContent("This is a great book!");
        review.setRating(5);
        reviewDAO.save(review);

        // Lấy Review đã lưu từ cơ sở dữ liệu bằng bookId và userId
        Review retrievedReview = reviewDAO.findAllByBookIdAndUserId(testBook.getId(), testUser.getId());

        // Kiểm tra xem Review đã được lấy đúng chưa
        assertNotNull(retrievedReview, "Review should be found");
        assertEquals(testBook.getId(), retrievedReview.getBookId(), "Book ID should match");
        assertEquals(testUser.getId(), retrievedReview.getUserId(), "User ID should match");
        assertEquals("This is a great book!", retrievedReview.getContent(), "Review content should match");
        assertEquals(5, retrievedReview.getRating(), "Review rating should match");
    }

    @Test
    public void testFindAllByBookIdAndUserId_NoReview() {
        // Tạo User mà không tạo Review cho họ
        User testUser = new Member("testuser", "password123", "test@example.com", "Test User");
        testUser = userDAO.save(testUser);
        User finalTestUser = testUser;
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewDAO.findAllByBookIdAndUserId(testBook.getId(), finalTestUser.getId());
        });
        // Kiểm tra xem exception có được ném ra đúng như mong đợi
        assertEquals("No review found with bookId: " + testBook.getId() + " and userId: " + testUser.getId(), exception.getMessage());
    }
}
