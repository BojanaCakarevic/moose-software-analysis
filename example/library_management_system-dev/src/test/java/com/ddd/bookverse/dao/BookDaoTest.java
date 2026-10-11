package com.ddd.bookverse.dao;

import com.ddd.bookverse.dao.BookDAO;
import com.ddd.bookverse.exception.BookNotFoundException;
import com.ddd.bookverse.model.Book;
import com.ddd.bookverse.model.Borrow;
import com.ddd.bookverse.model.Member;
import com.ddd.bookverse.model.User;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.ddd.bookverse.config.DatabaseConnectionTest.createConnection;
import static org.junit.jupiter.api.Assertions.*;

public class BookDaoTest {

    private static Connection connection;
    private BookDAO bookDAO;
    private BorrowDAO borrowDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        getConnection();
        connection.setAutoCommit(false); // Tắt auto-commit để sử dụng giao dịch
        bookDAO = new BookDAO(connection);
        userDAO = new UserDAO(connection);
        borrowDAO = new BorrowDAO(connection,userDAO,bookDAO);
        connection = createConnection(connection);
    }

    public static void getConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void testSaveBook() throws SQLException {
        // Tạo Book để kiểm tra
        Book book = new Book();
        book.setId("1");
        book.setTitle("Test Book");
        book.setAuthors(new String[] {"Author1", "Author2"});
        book.setDescription("This is a test book.");
        book.setPageCount(100);
        book.setPublishDate("2024-01-01");
        book.setPublisher("Test Publisher");
        book.setCategories(new String[] {"Category1"});
        book.setQuantityInStock(10);
        book.setIsbn("1234567890");
        book.setRatingSum(100L);
        book.setRatingCount(10);

        // Lưu book vào cơ sở dữ liệu giả lập (H2)
        Book savedBook = bookDAO.save(book);
        // Kiểm tra book đã được lưu thành công
        assertNotNull(savedBook);
        assertEquals("Test Book", savedBook.getTitle());
    }

    @Test
    public void testFindById() throws SQLException {
        // Lưu một book vào cơ sở dữ liệu giả lập
        Book book = new Book();
        book.setId("2");
        book.setTitle("Find Book");
        book.setAuthors(new String[] {"Author1"});
        book.setDescription("This book is for finding.");
        book.setPageCount(150);
        book.setPublishDate("2024-02-01");
        book.setPublisher("Publisher1");
        book.setCategories(new String[] {"Category2"});
        book.setQuantityInStock(5);
        book.setIsbn("9876543210");
        book.setRatingSum(200L);
        book.setRatingCount(15);
        bookDAO.save(book);
        Book foundBook = bookDAO.findById("2");

        assertNotNull(foundBook);
        assertEquals("Find Book", foundBook.getTitle());
    }
    @Test
    public void testDeleteById() throws SQLException {
        Book book1 = new Book();
        book1.setId("1");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);
        bookDAO.deleteById("1");
        assertThrows(BookNotFoundException.class, () -> bookDAO.findById("1"));
    }

    @Test
    public void testDeleteById_BookNotFound() throws SQLException {
        assertThrows(BookNotFoundException.class, () -> bookDAO.deleteById("999"));
    }

    @Test
    public void testFindAll() throws SQLException {
        // Lưu một số sách vào cơ sở dữ liệu giả lập
        Book book1 = new Book();
        book1.setId("3");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);
        Book book2 = new Book();
        book2.setId("4");
        book2.setTitle("Book 2");
        book2.setAuthors(new String[] {"Author2"});
        book2.setDescription("Description 2");
        book2.setPageCount(200);
        book2.setPublishDate("2024-02-01");
        book2.setPublisher("Publisher2");
        book2.setCategories(new String[] {"Category2"});
        book2.setQuantityInStock(5);
        book2.setIsbn("0987654321");
        book2.setRatingSum(200L);
        book2.setRatingCount(20);
        bookDAO.save(book2);
        // Kiểm tra phương thức findAll
        List<Book> books = bookDAO.findAll();
        assertNotNull(books);
        assertEquals(2, books.size());
    }

    @Test
    public void testFindAll_NotFound() throws SQLException {
        List<Book> books = bookDAO.findAll();
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    public void testFindRandomBook() throws SQLException {
        // Lưu một số sách vào cơ sở dữ liệu giả lập
        Book book1 = new Book();
        book1.setId("1");
        book1.setTitle("Random Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);

        bookDAO.save(book1);

        Book book2 = new Book();
        book2.setId("2");
        book2.setTitle("Random Book 2");
        book2.setAuthors(new String[] {"Author2"});
        book2.setDescription("Description 2");
        book2.setPageCount(200);
        book2.setPublishDate("2024-02-01");
        book2.setPublisher("Publisher2");
        book2.setCategories(new String[] {"Category2"});
        book2.setQuantityInStock(5);
        book2.setIsbn("0987654321");
        book2.setRatingSum(200L);
        book2.setRatingCount(20);

        bookDAO.save(book2);
        Book randomBook = bookDAO.findRandomBook();
        assertNotNull(randomBook);
        assertTrue(randomBook.getId().equals("1") || randomBook.getId().equals("2"));  // Kiểm tra rằng sách trả về là một trong những cuốn đã lưu
    }

    @Test
    public void testFindByAuthor() throws SQLException {
        // Lưu một số sách vào cơ sở dữ liệu giả lập
        Book book1 = new Book();
        book1.setId("5");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1", "Author2"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);
        // Kiểm tra phương thức findByAuthor
        List<Book> booksByAuthor1 = bookDAO.findByAuthor("Author1");
        assertNotNull(booksByAuthor1);
        assertEquals(1, booksByAuthor1.size());
        assertEquals("Book 1", booksByAuthor1.get(0).getTitle());
    }

    @Test
    public void testFindByAuthor_NotFound() throws SQLException {
        List<Book> booksByAuthorNotExist = bookDAO.findByAuthor("NonExistentAuthor");
        assertNotNull(booksByAuthorNotExist);
        assertTrue(booksByAuthorNotExist.isEmpty());
    }

    @Test
    public void testFindByPublisher_Found() throws SQLException {
        // Lưu sách với nhà xuất bản "Publisher1"
        Book book1 = new Book();
        book1.setId("5");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);

        // Lưu sách với nhà xuất bản "Publisher2"
        Book book2 = new Book();
        book2.setId("6");
        book2.setTitle("Book 2");
        book2.setAuthors(new String[] {"Author2"});
        book2.setDescription("Description 2");
        book2.setPageCount(200);
        book2.setPublishDate("2024-02-01");
        book2.setPublisher("Publisher2");
        book2.setCategories(new String[] {"Category2"});
        book2.setQuantityInStock(5);
        book2.setIsbn("0987654321");
        book2.setRatingSum(200L);
        book2.setRatingCount(20);
        bookDAO.save(book2);

        List<Book> books = bookDAO.findByPublisher("Publisher1");
        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Publisher1", books.get(0).getPublisher());  // Kiểm tra nhà xuất bản đúng
    }

    @Test
    public void testFindByPublisher_NotFound() throws SQLException {
        List<Book> books = bookDAO.findByPublisher("NonExistentPublisher");
        assertNotNull(books);
        assertTrue(books.isEmpty());  // Kiểm tra danh sách rỗng khi không có sách nào
    }

    @Test
    public void testFindByCategory_Found() throws SQLException {
        // Lưu sách với thể loại "Category1"
        Book book1 = new Book();
        book1.setId("7");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);

        // Lưu sách với thể loại "Category2"
        Book book2 = new Book();
        book2.setId("8");
        book2.setTitle("Book 2");
        book2.setAuthors(new String[] {"Author2"});
        book2.setDescription("Description 2");
        book2.setPageCount(200);
        book2.setPublishDate("2024-02-01");
        book2.setPublisher("Publisher2");
        book2.setCategories(new String[] {"Category2"});
        book2.setQuantityInStock(5);
        book2.setIsbn("0987654321");
        book2.setRatingSum(200L);
        book2.setRatingCount(20);
        bookDAO.save(book2);

        // Kiểm tra tìm sách theo thể loại "Category1"
        List<Book> books = bookDAO.findByCategory("Category1");
        assertNotNull(books);
        assertEquals(1, books.size());
        assertTrue(books.get(0).getCategories()[0].equals("Category1"));  // Kiểm tra thể loại đúng
    }

    @Test
    public void testFindByCategory_NotFound() throws SQLException {
        // Kiểm tra khi không có sách nào có thể loại "NonExistentCategory"
        List<Book> books = bookDAO.findByCategory("NonExistentCategory");
        assertNotNull(books);
        assertTrue(books.isEmpty());  // Kiểm tra danh sách rỗng khi không có sách nào
    }

    @Test
    public void testFindByTitle_Found() throws SQLException {
        // Lưu sách với tiêu đề chứa từ "Book"
        Book book1 = new Book();
        book1.setId("9");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);

        // Lưu sách với tiêu đề "Another Book"
        Book book2 = new Book();
        book2.setId("10");
        book2.setTitle("Another Book");
        book2.setAuthors(new String[] {"Author2"});
        book2.setDescription("Description 2");
        book2.setPageCount(200);
        book2.setPublishDate("2024-02-01");
        book2.setPublisher("Publisher2");
        book2.setCategories(new String[] {"Category2"});
        book2.setQuantityInStock(5);
        book2.setIsbn("0987654321");
        book2.setRatingSum(200L);
        book2.setRatingCount(20);
        bookDAO.save(book2);

        // Kiểm tra tìm sách theo tiêu đề "Book"
        List<Book> books = bookDAO.findByTitle("Book");
        assertNotNull(books);
        assertEquals(2, books.size());  // Kiểm tra số sách tìm thấy
        assertTrue(books.get(0).getTitle().contains("Book"));  // Kiểm tra rằng sách có tiêu đề chứa từ "Book"
    }

    @Test
    public void testFindByTitle_NotFound() throws SQLException {
        // Kiểm tra khi không có sách nào có tiêu đề "NonExistentBook"
        List<Book> books = bookDAO.findByTitle("NonExistentBook");
        assertNotNull(books);
        assertTrue(books.isEmpty());  // Kiểm tra danh sách rỗng khi không có sách nào
    }

    @Test
    public void testFindByUserStatusAndBookAttr_Found() throws SQLException {
        // Tạo và lưu sách vào cơ sở dữ liệu
        Book book1 = new Book();
        book1.setId("book123");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);

        // Tạo và lưu người dùng vào cơ sở dữ liệu
        User user = new Member();
        user.setFullName("name");user.setEmail("email@gmail.com");
        user.setPassword("password123");
        user.setUsername("username");
        userDAO.save(user);

        // Tạo và lưu bản ghi Borrow
        Borrow borrow = new Borrow();
        borrow.setUser(user); // Gán user
        borrow.setBook(book1); // Gán book
        borrow.setBorrowDate(LocalDate.now());
        borrow.setReturnDate(null);
        borrow.setStatus("borrowed");
        borrow.setPagesRead(50);
        borrowDAO.save(borrow); // Lưu borrow

        // Gọi phương thức cần kiểm tra
        List<Book> books = bookDAO.findByUserStatusAndBookAttr(user.getId(), "borrowed", "title", "Book");

        // Kiểm tra kết quả
        assertNotNull(books); // Đảm bảo danh sách không null
        assertEquals(1, books.size()); // Đảm bảo chỉ có 1 bản ghi khớp
        assertTrue(books.get(0).getTitle().contains("Book")); // Kiểm tra tiêu đề chứa từ "Book"
    }


    @Test
    public void testFindByUserStatusAndBookAttr_NotFound() throws SQLException {
        List<Book> books = bookDAO.findByUserStatusAndBookAttr(1L, "borrowed",
                "title", "NonExistentBook");
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    public void testUpdateBook_Success() throws SQLException {
        // Giả sử đã lưu một sách vào cơ sở dữ liệu trước đó (book có id = "1")
        Book book = new Book();
        book.setId("1");
        book.setTitle("Old Title");
        book.setAuthors(new String[]{"Old Author"});
        book.setDescription("Old Description");
        book.setImage(new byte[]{1, 2, 3});
        book.setPageCount(100);
        book.setPublishDate("2024-01-01");
        book.setPublisher("Old Publisher");
        book.setCategories(new String[]{"Old Category"});
        book.setQuantityInStock(10);
        book.setIsbn("1234567890");
        book.setRatingSum(50L);
        book.setRatingCount(5);

        // Lưu sách vào cơ sở dữ liệu (giả sử có phương thức save)
        bookDAO.save(book);

        // Cập nhật thông tin sách
        book.setTitle("New Title");
        book.setAuthors(new String[]{"New Author"});
        book.setDescription("New Description");
        book.setPublisher("New Publisher");

        Book updatedBook = bookDAO.updateBook(book);

        // Kiểm tra kết quả cập nhật
        assertNotNull(updatedBook);
        assertEquals("New Title", updatedBook.getTitle());
        assertEquals("New Author", updatedBook.getAuthors()[0]);
        assertEquals("New Description", updatedBook.getDescription());
        assertEquals("New Publisher", updatedBook.getPublisher());
    }

    @Test
    public void testUpdateBook_Fail_BookNotFound() throws SQLException {
        // Tạo đối tượng sách với ID không tồn tại trong cơ sở dữ liệu
        Book book = new Book();
        book.setId("999");
        book.setTitle("Non-Existent Book");
        book.setAuthors(new String[]{"Author"});
        book.setDescription("Description");
        book.setImage(new byte[]{});
        book.setPageCount(100);
        book.setPublishDate("2024-01-01");
        book.setPublisher("Publisher");
        book.setCategories(new String[]{"Category"});
        book.setQuantityInStock(5);
        book.setIsbn("1234567890");
        book.setRatingSum(10L);
        book.setRatingCount(1);
        Book updatedBook = bookDAO.updateBook(book);
        assertNull(updatedBook);
    }

    @Test
    public void testGetSum() throws SQLException {
        // Lưu một số sách vào cơ sở dữ liệu
        Book book1 = new Book();
        book1.setId("3");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);

        Book book2 = new Book();
        book2.setId("4");
        book2.setTitle("Book 2");
        book2.setAuthors(new String[] {"Author2"});
        book2.setDescription("Description 2");
        book2.setPageCount(200);
        book2.setPublishDate("2024-02-01");
        book2.setPublisher("Publisher2");
        book2.setCategories(new String[] {"Category2"});
        book2.setQuantityInStock(5);
        book2.setIsbn("0987654321");
        book2.setRatingSum(200L);
        book2.setRatingCount(20);
        bookDAO.save(book2);

        // Kiểm tra phương thức getSum
        Integer sum = bookDAO.getSum();
        assertNotNull(sum);
        assertEquals(2, sum); // Kiểm tra số lượng sách là 2
    }

    @Test
    public void testGetDataCategory() throws SQLException {
        // Tạo sách với các categories khác nhau
        Book book1 = new Book();
        book1.setId("1");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[] {"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[] {"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(50L);
        book1.setRatingCount(10);
        bookDAO.save(book1);

        Book book2 = new Book();
        book2.setId("2");
        book2.setTitle("Book 2");
        book2.setAuthors(new String[] {"Author2"});
        book2.setDescription("Description 2");
        book2.setPageCount(200);
        book2.setPublishDate("2024-02-01");
        book2.setPublisher("Publisher2");
        book2.setCategories(new String[] {"Category2"});
        book2.setQuantityInStock(5);
        book2.setIsbn("0987654321");
        book2.setRatingSum(60L);
        book2.setRatingCount(20);
        bookDAO.save(book2);

        Book book3 = new Book();
        book3.setId("3");
        book3.setTitle("Book 3");
        book3.setAuthors(new String[] {"Author3"});
        book3.setDescription("Description 3");
        book3.setPageCount(150);
        book3.setPublishDate("2024-03-01");
        book3.setPublisher("Publisher3");
        book3.setCategories(new String[] {"Category1"});
        book3.setQuantityInStock(8);
        book3.setIsbn("1122334455");
        book3.setRatingSum(70L);
        book3.setRatingCount(15);
        bookDAO.save(book3);
        // Gọi phương thức getDataCategory
        Map<String, Integer> categoryData = bookDAO.getDataCategory();
        // Kiểm tra kết quả
        assertNotNull(categoryData);
        assertEquals(2, categoryData.size());
        assertTrue(categoryData.containsKey("Category1"));
        assertTrue(categoryData.containsKey("Category2"));
        assertEquals(2, categoryData.get("Category1").intValue());
        assertEquals(1, categoryData.get("Category2").intValue());
    }

    @Test
    public void testGetDataCategory_NotFound() throws SQLException {
        Map<String, Integer> categoryData = bookDAO.getDataCategory();
        assertNotNull(categoryData);
        assertTrue(categoryData.isEmpty());
    }

    @Test
    public void testGetDistinctCategories() throws SQLException {
        // Thêm dữ liệu mẫu
        Book book1 = new Book();
        book1.setId("1");
        book1.setTitle("Book 1");
        book1.setAuthors(new String[]{"Author1"});
        book1.setDescription("Description 1");
        book1.setPageCount(100);
        book1.setPublishDate("2024-01-01");
        book1.setPublisher("Publisher1");
        book1.setCategories(new String[]{"Category1"});
        book1.setQuantityInStock(10);
        book1.setIsbn("1234567890");
        book1.setRatingSum(100L);
        book1.setRatingCount(10);
        bookDAO.save(book1);

        Book book2 = new Book();
        book2.setId("2");
        book2.setTitle("Book 2");
        book2.setAuthors(new String[]{"Author2"});
        book2.setDescription("Description 2");
        book2.setPageCount(200);
        book2.setPublishDate("2024-02-01");
        book2.setPublisher("Publisher2");
        book2.setCategories(new String[]{"Category2"});
        book2.setQuantityInStock(5);
        book2.setIsbn("0987654321");
        book2.setRatingSum(200L);
        book2.setRatingCount(20);
        bookDAO.save(book2);

        Book book3 = new Book();
        book3.setId("3");
        book3.setTitle("Book 3");
        book3.setAuthors(new String[]{"Author3"});
        book3.setDescription("Description 3");
        book3.setPageCount(300);
        book3.setPublishDate("2024-03-01");
        book3.setPublisher("Publisher3");
        book3.setCategories(new String[]{"Category1"}); // Trùng với Category của book1
        book3.setQuantityInStock(15);
        book3.setIsbn("1122334455");
        book3.setRatingSum(300L);
        book3.setRatingCount(30);
        bookDAO.save(book3);
        List<String> distinctCategories = bookDAO.getDistinctCategories("Category");
        assertNotNull(distinctCategories); // Kết quả không null
        assertEquals(2, distinctCategories.size()); // Chỉ có 2 danh mục duy nhất
        assertTrue(distinctCategories.contains("Category1"));
        assertTrue(distinctCategories.contains("Category2"));
    }

}
