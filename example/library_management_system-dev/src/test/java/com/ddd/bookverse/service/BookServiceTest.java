package com.ddd.bookverse.service;
import com.ddd.bookverse.dao.BookDAO;
import com.ddd.bookverse.dao.BorrowDAO;
import com.ddd.bookverse.exception.BookNotFoundException;
import com.ddd.bookverse.model.Book;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.*;

public class BookServiceTest {

    private BookService bookService; // The service you're testing
    @Mock
    private BookDAO bookDAO; // Mock the BookDAO
    @Mock
    private BorrowDAO borrowDAO; // Mock the BorrowDAO

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bookService = new BookService(bookDAO,borrowDAO);
    }

    // Test Add Book
    @Test
    public void testAddBook() {
        // Tạo đối tượng Book
        Book book = new Book();
        book.setId("1");
        book.setTitle("The Great Book");
        book.setAuthors(new String[]{"Author 1", "Author 2"});
        book.setDescription("This is a great book.");
        book.setPageCount(300);
        book.setPublishDate("2024-11-25");
        book.setPublisher("Famous Publisher");
        book.setCategories(new String[]{"Fiction", "Adventure"});
        book.setQuantityInStock(50);
        book.setIsbn("9783161484100");
        book.setRatingSum(200L);
        book.setRatingCount(50);
        when(bookDAO.save(any(Book.class))).thenReturn(book);
        Book result = bookService.addBook(book);
        assertNotNull(result);
        assertEquals("The Great Book", result.getTitle());
        verify(bookDAO, times(1)).save(book);
    }

    // Test Delete Book
    @Test
    public void testDeleteBook_Success() {
        String bookId = "1";
        doNothing().when(bookDAO).deleteById(bookId);
        bookService.deleteBook(bookId);
        verify(bookDAO, times(1)).deleteById(bookId);
    }
    @Test
    public void testDeleteBook_BookNotFound() {
        String bookId = "2";
        doThrow(new BookNotFoundException("Book not found")).when(bookDAO).deleteById(bookId);
        bookService.deleteBook(bookId);
        verify(bookDAO, times(1)).deleteById(bookId);
    }

    // Test get All book
    @Test
    public void testGetAllBooks() {
        // Tạo danh sách sách giả lập (mock data)
        Book book1 = new Book();
        book1.setId("1");
        book1.setTitle("Book 1");
        Book book2 = new Book();
        book2.setId("2");
        book2.setTitle("Book 2");
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookDAO.findAll()).thenReturn(expectedBooks);
        List<Book> books = bookService.getAllBooks();
        // Xác nhận rằng danh sách sách trả về đúng như mong đợi
        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Book 1", books.get(0).getTitle());
        assertEquals("Book 2", books.get(1).getTitle());
        verify(bookDAO, times(1)).findAll();
    }

    // Test getBook
    @Test
    public void testGetBook_Found() {
        Book book = new Book();
        book.setId("1");
        book.setTitle("Book 1");
        when(bookDAO.findById("1")).thenReturn(book);
        Book result = bookService.getBook("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Book 1", result.getTitle());
        verify(bookDAO, times(1)).findById("1");
    }

    @Test
    public void testGetBook_NotFound() {
        when(bookDAO.findById("2")).thenThrow(new BookNotFoundException("Book not found"));
        Book result = bookService.getBook("2");
        assertNull(result);
        verify(bookDAO, times(1)).findById("2");
    }

    // Test getBookRandom
    @Test
    public void testGetRandomBook() {
        Book randomBook = new Book();
        randomBook.setId("1");
        randomBook.setTitle("Random Book");
        when(bookDAO.findRandomBook()).thenReturn(randomBook);
        Book result = bookService.getRandomBook();
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Random Book", result.getTitle());
        verify(bookDAO, times(1)).findRandomBook();
    }
    @Test
    public void testGetRandomBook_Exception() {
        when(bookDAO.findRandomBook()).thenThrow(new RuntimeException("Error fetching random book"));
        Book result = bookService.getRandomBook();
        assertNull(result);
        verify(bookDAO, times(1)).findRandomBook();
    }

    @Test
    public void testGetBookByAuthor() {
        String author = "J.K. Rowling";
        Book book1 = new Book();
        book1.setTitle("Harry Potter and the Sorcerer's Stone");
        book1.setAuthors(new String[] {author});
        Book book2 = new Book();
        book2.setTitle("Harry Potter and the Chamber of Secrets");
        book2.setAuthors(new String[] {author});
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookDAO.findByAuthor(author)).thenReturn(expectedBooks);
        List<Book> result = bookService.getBookByAuthor(author);
        assertEquals(expectedBooks, result);
        verify(bookDAO, times(1)).findByAuthor(author);
    }

    @Test
    public void testGetBookByPublisher() {
        String publisher = "Bloomsbury";
        Book book1 = new Book();
        book1.setTitle("Harry Potter and the Sorcerer's Stone");
        book1.setPublisher(publisher);
        Book book2 = new Book();
        book2.setTitle("Harry Potter and the Chamber of Secrets");
        book2.setPublisher(publisher);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookDAO.findByPublisher(publisher)).thenReturn(expectedBooks);
        List<Book> result = bookService.getBookByPublisher(publisher);
        assertEquals(expectedBooks, result);
        verify(bookDAO, times(1)).findByPublisher(publisher);
    }
    @Test
    public void testGetBookByCategory() {
        String category = "Fantasy";
        Book book1 = new Book();
        book1.setTitle("Harry Potter and the Sorcerer's Stone");
        book1.setCategories(new String[] {category});
        Book book2 = new Book();
        book2.setTitle("The Lord of the Rings");
        book2.setCategories(new String[] {category});
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookDAO.findByCategory(category)).thenReturn(expectedBooks);
        List<Book> result = bookService.getBookByCategory(category);
        assertEquals(expectedBooks, result);
        verify(bookDAO, times(1)).findByCategory(category);
    }

    @Test
    public void testGetBookByTitle() {
        String title = "Harry Potter and the Sorcerer's Stone";
        Book book1 = new Book();
        book1.setTitle(title);
        List<Book> expectedBooks = Arrays.asList(book1);
        when(bookDAO.findByTitle(title)).thenReturn(expectedBooks);
        List<Book> result = bookService.getBookByTitle(title);
        assertEquals(expectedBooks, result);
        verify(bookDAO, times(1)).findByTitle(title);
    }

    @Test
    public void testUpdateBook() {
        String bookId = "1";
        Book oldBook = new Book();
        oldBook.setId("1");
        oldBook.setTitle("Old Book");
        oldBook.setAuthors(new String[] {"Old Author"});
        oldBook.setPublisher("Old Publisher");
        oldBook.setCategories(new String[] {"Old Category"});
        oldBook.setQuantityInStock(5);
        Book updatedBook = new Book();
        updatedBook.setId("1");
        updatedBook.setTitle("Updated Book");
        updatedBook.setAuthors(new String[] {"Author"});
        updatedBook.setPublisher("Publisher");
        updatedBook.setCategories(new String[] {"Category"});
        updatedBook.setQuantityInStock(10);
        when(bookDAO.findById(bookId)).thenReturn(oldBook);
        bookService.updateBook(updatedBook);
        verify(bookDAO, times(1)).updateBook(updatedBook);

        assertEquals("Updated Book", updatedBook.getTitle());
        assertEquals("Author", updatedBook.getAuthors()[0]);
        assertEquals("Publisher", updatedBook.getPublisher());
        assertEquals("Category", updatedBook.getCategories()[0]);
        assertEquals(10, updatedBook.getQuantityInStock());
    }

    @Test
    public void testUpdateBookQuantity_Success() {
        String bookId = "1";
        int newQuantity = 20;
        Book book = new Book();
        book.setId(bookId);
        book.setQuantityInStock(10);
        when(bookDAO.findById(bookId)).thenReturn(book);
        bookService.updateBookQuantity(bookId, newQuantity);
        assertEquals(newQuantity, book.getQuantityInStock());
        verify(bookDAO, times(1)).updateBook(book);
    }

    @Test
    public void testUpdateBookQuantity_IllegalArgumentException() {
        String bookId = "1";
        int invalidQuantity = -5;
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.updateBookQuantity(bookId, invalidQuantity);
        });
    }

    // Test getBooksByUserAndStatus
    @Test
    public void testGetBooksByUserAndStatus_Found() {
        Long userId = 1L;
        String status = "BORROWED";
        List<String> bookIds = Arrays.asList("1", "2");
        when(borrowDAO.findByUserAndStatus(userId, status)).thenReturn(bookIds);
        Book book1 = new Book();
        book1.setId("1");
        book1.setTitle("Book 1");
        Book book2 = new Book();
        book2.setId("2");
        book2.setTitle("Book 2");
        when(bookDAO.findById("1")).thenReturn(book1);
        when(bookDAO.findById("2")).thenReturn(book2);
        List<Book> result = bookService.getBooksByUserAndStatus(userId, status);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Book 1", result.get(0).getTitle());
        assertEquals("Book 2", result.get(1).getTitle());
        verify(borrowDAO, times(1)).findByUserAndStatus(userId, status);
        verify(bookDAO, times(1)).findById("1");
        verify(bookDAO, times(1)).findById("2");
    }

    @Test
    public void testGetBooksByUserAndStatus_NoBooksFound() {
        Long userId = 1L;
        String status = "BORROWED";
        List<String> bookIds = Arrays.asList();
        when(borrowDAO.findByUserAndStatus(userId, status)).thenReturn(bookIds);
        List<Book> result = bookService.getBooksByUserAndStatus(userId, status);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(borrowDAO, times(1)).findByUserAndStatus(userId, status);
    }

    @Test
    public void testGetBooksByUserAndStatus_BookNotFound() {
        Long userId = 1L;
        String status = "BORROWED";
        List<String> bookIds = Arrays.asList("1");
        when(borrowDAO.findByUserAndStatus(userId, status)).thenReturn(bookIds);
        when(bookDAO.findById("1")).thenThrow(new BookNotFoundException("Book not found"));

        List<Book> result = bookService.getBooksByUserAndStatus(userId, status);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(borrowDAO, times(1)).findByUserAndStatus(userId, status);
        verify(bookDAO, times(1)).findById("1");
    }

    @Test
    public void testGetBooksByUserStatusAndBookAttr() {
        Long userId = 1L;
        String status = "BORROWED";
        String query = "title";
        String value = "Harry Potter";
        Book book1 = new Book("1", "Harry Potter and the Sorcerer's Stone",  new String[] {"J.K. Rowling"});
        Book book2 = new Book("2", "Harry Potter and the Chamber of Secrets",  new String[] {"J.K. Rowling"});
        List<Book> mockBooks = Arrays.asList(book1, book2);
        when(bookDAO.findByUserStatusAndBookAttr(userId, status, query, value)).thenReturn(mockBooks);
        List<Book> result = bookService.getBooksByUserStatusAndBookAttr(userId, status, query, value);
        // Kiểm tra kết quả
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Harry Potter and the Sorcerer's Stone", result.get(0).getTitle());
        assertEquals("Harry Potter and the Chamber of Secrets", result.get(1).getTitle());
        // Xác minh rằng phương thức findByUserStatusAndBookAttr của bookDAO đã được gọi một lần
        verify(bookDAO, times(1)).findByUserStatusAndBookAttr(userId, status, query, value);
    }

    @Test
    public void testGetBooksByUserStatusAndBookAttr_NoBooksFound() {
        Long userId = 1L;
        String status = "BORROWED";
        String query = "title";
        String value = "Nonexistent Book";
        when(bookDAO.findByUserStatusAndBookAttr(userId, status, query, value)).thenReturn(Arrays.asList());
        List<Book> result = bookService.getBooksByUserStatusAndBookAttr(userId, status, query, value);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookDAO, times(1)).findByUserStatusAndBookAttr(userId, status, query, value);
    }

    @Test
    public void testRemoveBookRating() {
        String bookId = "1";
        int rating = 5;
        Book book = new Book(bookId, "Harry Potter", new String[] {"J.K. Rowling"});
        book.addRating(rating);  // Giả sử đã có rating ban đầu

        when(bookDAO.findById(bookId)).thenReturn(book);

        bookService.removeBookRating(bookId, rating);

        verify(bookDAO, times(1)).findById(bookId);
        verify(bookDAO, times(1)).updateBook(book);
        assertTrue(book.getRatingSum().equals(0L));
    }

    @Test
    public void testGetSum() {
        int expectedSum = 100;

        when(bookDAO.getSum()).thenReturn(expectedSum);

        Integer sum = bookService.getSum();

        assertNotNull(sum);
        assertEquals(expectedSum, sum.intValue());
        verify(bookDAO, times(1)).getSum();
    }

    @Test
    public void testGetCompletionPercentage() {
        String bookId = "1";
        Long userId = 1L;
        double expectedPercentage = 85.5;

        when(bookDAO.getCompletionPercentage(bookId, userId)).thenReturn(expectedPercentage);

        double completionPercentage = bookService.getCompletionPercentage(bookId, userId);

        assertEquals(expectedPercentage, completionPercentage, 0.01);
        verify(bookDAO, times(1)).getCompletionPercentage(bookId, userId);
    }

    @Test
    public void testGetDataCategory() {
        HashMap<String, Integer> expectedData = new HashMap<>();
        expectedData.put("Fiction", 10);
        expectedData.put("Non-Fiction", 5);
        when(bookDAO.getDataCategory()).thenReturn(expectedData);
        Map<String, Integer> dataCategory = bookService.getDataCategory();
        assertNotNull(dataCategory);
        assertEquals(expectedData, dataCategory);
        verify(bookDAO, times(1)).getDataCategory();
    }

    @Test
    public void testGetNumBooksByUserIdAndStatus() {
        Long userId = 1L;
        String status = "BORROWED";
        int expectedNumBooks = 3;
        when(borrowDAO.countByUserAndStatus(userId, status)).thenReturn(expectedNumBooks);
        int numBooks = bookService.getNumBooksByUserIdAndStatus(userId, status);
        assertEquals(expectedNumBooks, numBooks);
        verify(borrowDAO, times(1)).countByUserAndStatus(userId, status);
    }

    @Test
    public void testGetReadingBooksByUser() {
        Long userId = 1L;
        Book book1 = new Book("1", "Harry Potter and the Sorcerer's Stone", new String[] {"J.K. Rowling"});
        Book book2 = new Book("2", "Harry Potter and the Chamber of Secrets", new String[] {"J.K. Rowling"});
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookDAO.findReadingBookFromUser(userId)).thenReturn(expectedBooks);
        List<Book> result = bookService.getReadingBooksByUser(userId);
        assertNotNull(result);
        assertEquals(expectedBooks, result);
        verify(bookDAO, times(1)).findReadingBookFromUser(userId);
    }

    @Test
    public void testGetTopBorrowedBooks() {
        int n = 2;
        Book book1 = new Book("1", "Harry Potter and the Sorcerer's Stone", new String[] {"J.K. Rowling"});
        Book book2 = new Book("2", "Harry Potter and the Chamber of Secrets", new String[] {"J.K. Rowling"});
        Pair<Book, Integer> pair1 = new Pair<>(book1, 5); // Book 1 borrowed 5 times
        Pair<Book, Integer> pair2 = new Pair<>(book2, 7); // Book 2 borrowed 7 times
        List<Pair<Book, Integer>> expectedBooks = Arrays.asList(pair1, pair2);

        // Mô phỏng hành vi của bookDAO
        when(bookDAO.findTopNBorrowedBooks(n)).thenReturn(expectedBooks);

        // Gọi phương thức cần test
        List<Pair<Book, Integer>> result = bookService.getTopBorrowedBooks(n);

        // Kiểm tra kết quả trả về
        assertNotNull(result);
        assertEquals(expectedBooks, result);

        // Kiểm tra số lần gọi phương thức mock
        verify(bookDAO, times(1)).findTopNBorrowedBooks(n);
    }

    @Test
    public void testGetTopBorrowedBooks_InvalidArgument() {
        int n = -1;
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.getTopBorrowedBooks(n);
        });
    }

    @Test
    public void testGetDistinctCategories() {
        String query = "Harry Potter";
        List<String> expectedCategories = Arrays.asList("Fiction", "Fantasy", "Adventure");
        when(bookDAO.getDistinctCategories(query)).thenReturn(expectedCategories);
        List<String> result = bookService.getDistinctCategories(query);
        assertNotNull(result);
        assertEquals(expectedCategories, result);
        verify(bookDAO, times(1)).getDistinctCategories(query);
    }

}