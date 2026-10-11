
package com.ddd.bookverse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private Book book;

    @BeforeEach
    public void setUp() {
        // Set up a new Book object before each test
        book = new Book("1", "Test Book", new String[]{"Author1"});
    }

    @Test
    public void testBookConstructorAndGetters() {
        // Tạo một đối tượng Book với constructor
        String[] authors = {"Author 1", "Author 2"};
        Book book = new Book("1", "Test Book", authors);

        // Kiểm tra các thuộc tính
        assertEquals("1", book.getId());
        assertEquals("Test Book", book.getTitle());
        assertArrayEquals(authors, book.getAuthors());
    }

    @Test
    public void testBookSetters() {
        // Tạo đối tượng Book rỗng
        Book book = new Book();

        // Sử dụng các setter để gán giá trị
        book.setId("2");
        book.setTitle("Updated Book");
        book.setAuthors(new String[] {"Author 3"});
        book.setDescription("New description");
        book.setPublisher("New Publisher");
        book.setCategories(new String[] {"Category 1"});
        book.setPageCount(200);
        book.setQuantityInStock(5);
        book.setIsbn("0987654321");

        // Kiểm tra xem các giá trị setter có được gán chính xác không
        assertEquals("2", book.getId());
        assertEquals("Updated Book", book.getTitle());
        assertArrayEquals(new String[] {"Author 3"}, book.getAuthors());
        assertEquals("New description", book.getDescription());
        assertEquals("New Publisher", book.getPublisher());
        assertArrayEquals(new String[] {"Category 1"}, book.getCategories());
        assertEquals(200, book.getPageCount());
        assertEquals(5, book.getQuantityInStock());
        assertEquals("0987654321", book.getIsbn());
    }

    @Test
    public void testAddRating() {
        // Tạo đối tượng Book
        Book book = new Book("1", "Test Book", new String[] {"Author 1"});

        // Thêm các đánh giá
        book.addRating(4); // 4 sao
        book.addRating(5); // 5 sao

        // Kiểm tra tổng số lượng đánh giá và tổng điểm
        assertEquals(2, book.getRatingCount());
        assertEquals(9L, book.getRatingSum()); // Tổng điểm là 4 + 5
        assertEquals(4.5, book.getAverageRating(), 0.001); // Đánh giá trung bình là 9 / 2 = 4.5
    }

    @Test
    public void testRemoveRating() {
        // Tạo đối tượng Book và thêm đánh giá
        Book book = new Book("1", "Test Book", new String[] {"Author 1"});
        book.addRating(4); // 4 sao
        book.addRating(5); // 5 sao

        // Kiểm tra trước khi xóa
        assertEquals(2, book.getRatingCount());
        assertEquals(9L, book.getRatingSum());

        // Xóa một đánh giá
        book.removeRating(4); // Xóa 4 sao
        assertEquals(1, book.getRatingCount());
        assertEquals(5L, book.getRatingSum()); // Chỉ còn 5 sao
        assertEquals(5.0, book.getAverageRating(), 0.001); // Đánh giá trung bình còn lại là 5.0
    }

    @Test
    public void testGetAverageRatingWhenNoRatings() {
        // Tạo đối tượng Book mà chưa thêm bất kỳ đánh giá nào
        Book book = new Book("1", "Test Book", new String[] {"Author 1"});

        // Kiểm tra khi chưa có đánh giá
        assertEquals(0.0, book.getAverageRating(), 0.001); // Đánh giá trung bình phải là 0.0
    }

    @Test
    public void testGetId() {
        assertEquals("1", book.getId(), "Book ID should be 1");
    }

    @Test
    public void testSetId() {
        book.setId("2");
        assertEquals("2", book.getId(), "Book ID should be updated to 2");
    }

    @Test
    public void testGetTitle() {
        assertEquals("Test Book", book.getTitle(), "Book title should be 'Test Book'");
    }

    @Test
    public void testSetTitle() {
        book.setTitle("Updated Title");
        assertEquals("Updated Title", book.getTitle(), "Book title should be updated to 'Updated Title'");
    }

    @Test
    public void testGetAuthors() {
        assertArrayEquals(new String[]{"Author1"}, book.getAuthors(), "Book authors should be 'Author1'");
    }

    @Test
    public void testSetAuthors() {
        book.setAuthors(new String[]{"Author2"});
        assertArrayEquals(new String[]{"Author2"}, book.getAuthors(), "Book authors should be updated to 'Author2'");
    }

    @Test
    public void testGetDescription() {
        book.setDescription("Description of the book");
        assertEquals("Description of the book", book.getDescription(), "Book description should be set correctly");
    }

    @Test
    public void testSetDescription() {
        book.setDescription("New Description");
        assertEquals("New Description", book.getDescription(), "Book description should be updated to 'New Description'");
    }

    @Test
    public void testGetImage() {
        byte[] image = {1, 2, 3};
        book.setImage(image);
        assertArrayEquals(image, book.getImage(), "Book image should be set correctly");
    }

    @Test
    public void testSetImage() {
        byte[] image = {4, 5, 6};
        book.setImage(image);
        assertArrayEquals(image, book.getImage(), "Book image should be updated correctly");
    }

    @Test
    public void testGetPageCount() {
        assertEquals(0, book.getPageCount(), "Initial page count should be 0");
    }

    @Test
    public void testSetPageCount() {
        book.setPageCount(200);
        assertEquals(200, book.getPageCount(), "Page count should be updated to 200");
    }

    @Test
    public void testSetPageCountNegative() {
        book.setPageCount(-50);
        assertEquals(0, book.getPageCount(), "Page count should be 0 if set to a negative number");
    }

    @Test
    public void testGetPublishDate() {
        book.setPublishDate("2024-01-01");
        assertEquals("2024-01-01", book.getPublishDate(), "Book publish date should be '2024-01-01'");
    }

    @Test
    public void testSetPublishDate() {
        book.setPublishDate("2025-02-01");
        assertEquals("2025-02-01", book.getPublishDate(), "Book publish date should be '2025-02-01'");
    }

    @Test
    public void testGetPublisher() {
        book.setPublisher("Test Publisher");
        assertEquals("Test Publisher", book.getPublisher(), "Book publisher should be 'Test Publisher'");
    }

    @Test
    public void testSetPublisher() {
        book.setPublisher("Updated Publisher");
        assertEquals("Updated Publisher", book.getPublisher(), "Book publisher should be 'Updated Publisher'");
    }

    @Test
    public void testGetCategories() {
        book.setCategories(new String[]{"Category1", "Category2"});
        assertArrayEquals(new String[]{"Category1", "Category2"}, book.getCategories(), "Book categories should match");
    }

    @Test
    public void testSetCategories() {
        book.setCategories(new String[]{"Category3", "Category4"});
        assertArrayEquals(new String[]{"Category3", "Category4"}, book.getCategories(), "Book categories should be updated correctly");
    }

    @Test
    public void testGetQuantityInStock() {
        assertEquals(0, book.getQuantityInStock(), "Initial quantity in stock should be 0");
    }

    @Test
    public void testSetQuantityInStock() {
        book.setQuantityInStock(10);
        assertEquals(10, book.getQuantityInStock(), "Quantity in stock should be updated to 10");
    }

    @Test
    public void testSetQuantityInStockNegative() {
        book.setQuantityInStock(-5);
        assertEquals(0, book.getQuantityInStock(), "Quantity in stock should be 0 if set to a negative number");
    }

    @Test
    public void testGetIsbn() {
        book.setIsbn("1234567890");
        assertEquals("1234567890", book.getIsbn(), "Book ISBN should be '1234567890'");
    }

    @Test
    public void testSetIsbn() {
        book.setIsbn("0987654321");
        assertEquals("0987654321", book.getIsbn(), "Book ISBN should be updated to '0987654321'");
    }

    @Test
    public void testGetRatingSum() {
        book.setRatingSum(50L);
        assertEquals(50L, book.getRatingSum(), "Book rating sum should be 50");
    }

    @Test
    public void testSetRatingSum() {
        book.setRatingSum(100L);
        assertEquals(100L, book.getRatingSum(), "Book rating sum should be updated to 100");
    }

    @Test
    public void testGetRatingCount() {
        assertEquals(0, book.getRatingCount(), "Initial rating count should be 0");
    }

    @Test
    public void testSetRatingCount() {
        book.setRatingCount(5);
        assertEquals(5, book.getRatingCount(), "Book rating count should be 5");
    }


    @Test
    public void testGetAverageRating() {
        book.addRating(4);
        book.addRating(5);
        assertEquals(4.5, book.getAverageRating(), 0.01, "Average rating should be 4.5");
    }



}
