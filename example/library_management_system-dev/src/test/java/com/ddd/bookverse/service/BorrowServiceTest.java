package com.ddd.bookverse.service;

import com.ddd.bookverse.dao.BookDAO;
import com.ddd.bookverse.dao.BorrowDAO;
import com.ddd.bookverse.dao.UserDAO;
import com.ddd.bookverse.exception.BorrowNotFoundException;
import com.ddd.bookverse.exception.BorrowStatusException;
import com.ddd.bookverse.exception.IllegalPagesReadException;
import com.ddd.bookverse.model.*;
import com.ddd.bookverse.service.BorrowService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class BorrowServiceTest {

    @InjectMocks
    private BorrowService borrowService;
    @Mock
    private BorrowDAO borrowDAO;
    @Mock
    private BookDAO bookDAO;
    @Mock
    private UserDAO userDAO;

    private Borrow borrow;
    private User user;
    private Book book;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        borrowService = new BorrowService(borrowDAO,bookDAO,userDAO);
        user = new Member();
        user.setId(1L);
        book = new Book();
        book.setId("1NWDswEACAAJ");
        book.setQuantityInStock(5);
        book.setPageCount(500);
        borrow = new Borrow();
        borrow.setId(1L);
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setReturnDate(LocalDate.now().plusDays(7));
        borrow.setStatus("Borrowing");
        borrow.setPagesRead(0);
    }

    @Test
    public void testBorrowBook_ValidBorrow() {
        when(userDAO.findById(2L)).thenReturn(user);
        when(bookDAO.findById("1NWDswEACAAJ")).thenReturn(book);
        when(borrowDAO.save(any(Borrow.class))).thenReturn(borrow);
        borrowService.borrowBook(2L, "1NWDswEACAAJ", "2024-11-25", "2024-12-02");
        verify(borrowDAO, times(1)).save(any(Borrow.class));
        verify(bookDAO, times(1)).updateBook(any(Book.class));
    }

    @Test(expected = BorrowStatusException.class)
    public void testBorrowBook_AlreadyBorrowing() {
        // Mock hành động tìm kiếm người dùng và sách
        when(userDAO.findById(1L)).thenReturn(user);
        when(bookDAO.findById("1NWDswEACAAJ")).thenReturn(book);

        // Giả sử người dùng đã đang mượn sách này
        Borrow existingBorrow = new Borrow();
        existingBorrow.setStatus("Borrowing");
        when(borrowDAO.findLatestBorrowOfABookFromUser(1L, "1NWDswEACAAJ")).thenReturn(existingBorrow);

        // Kiểm tra phương thức borrowBook sẽ ném exception
        borrowService.borrowBook(1L, "1NWDswEACAAJ", "2024-11-25", "2024-12-02");
    }

    @Test
    public void testReturnBook_ValidReturn() {
        when(userDAO.findById(2L)).thenReturn(user);
        when(bookDAO.findById("1NWDswEACAAJ")).thenReturn(book);
        Borrow existingBorrow = new Borrow();
        existingBorrow.setStatus("Borrowing");
        when(borrowDAO.findLatestBorrowOfABookFromUser(2L, "1NWDswEACAAJ")).thenReturn(existingBorrow);
        doNothing().when(borrowDAO).updateBorrow(any(Long.class), any(Borrow.class));
        when(bookDAO.updateBook(any(Book.class))).thenReturn(book);
        ArgumentCaptor<Borrow> borrowCaptor = ArgumentCaptor.forClass(Borrow.class);
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        borrowService.returnBook(2L, "1NWDswEACAAJ", "2024-12-02");
        verify(borrowDAO, times(1)).updateBorrow(eq(existingBorrow.getId()), borrowCaptor.capture());
        verify(bookDAO, times(1)).updateBook(bookCaptor.capture());
        Borrow capturedBorrow = borrowCaptor.getValue();
        assertEquals("Returned", capturedBorrow.getStatus());
        assertEquals("2024-12-02", capturedBorrow.getReturnDate().toString());
    }

    @Test
    public void testAddBorrow() {
        when(borrowDAO.save(any(Borrow.class))).thenReturn(borrow);
        borrowService.addBorrow(borrow);
        verify(borrowDAO, times(1)).save(any(Borrow.class));

        ArgumentCaptor<Borrow> borrowCaptor = ArgumentCaptor.forClass(Borrow.class);
        verify(borrowDAO).save(borrowCaptor.capture());
        Borrow saveBorrow = borrowCaptor.getValue();
        assertEquals(book,saveBorrow.getBook() );
        assertEquals(user, saveBorrow.getUser());
    }
    @Test
    public void testDeleteBorrow() {
        doNothing().when(borrowDAO).deleteById(anyLong());
        borrowService.deleteBorrow(1L);
        verify(borrowDAO, times(1)).deleteById(1L);
        Borrow deleteBorrow = borrowService.getBorrow(1L);
        assertNull(deleteBorrow);
    }

    @Test
    public void testDeleteBorrow_WhenExceptionOccurs() {
        doThrow(new BorrowNotFoundException("Borrow not found")).when(borrowDAO).deleteById(anyLong());
        borrowService.deleteBorrow(1L);
        verify(borrowDAO, times(1)).deleteById(1L);
    }
    @Test
    public void testGetBorrow() {
        Borrow borrow = new Borrow();
        borrow.setId(1L);
        when(borrowDAO.findById(1L)).thenReturn(borrow);
        Borrow result = borrowService.getBorrow(1L);
        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
    }

    @Test
    public void testGetBorrow_WhenBorrowNotFound() {
        when(borrowDAO.findById(1L)).thenThrow(new BorrowNotFoundException("Borrow not found"));
        Borrow result = borrowService.getBorrow(1L);
        assertNull(result);
    }

    @Test
    public void testGetBorrowByUserId() {
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(new Borrow());
        borrows.add(new Borrow());
        when(borrowDAO.findByUser(2L)).thenReturn(borrows);
        List<Borrow> result = borrowService.getBorrowByUserId(2L);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetLatestBorrowOfABookFromUser() {
        Borrow borrow = new Borrow();
        borrow.setId(1L);
        borrow.setStatus("Borrowing");
        when(borrowDAO.findLatestBorrowOfABookFromUser(2L, "1NWDswEACAAJ")).thenReturn(borrow);
        Borrow result = borrowService.getLatestBorrowOfABookFromUser(2L, "1NWDswEACAAJ");
        assertNotNull(result);
        assertEquals(Long.valueOf(1), result.getId());
    }

    @Test
    public void testGetLatestBorrowOfABookFromUser_WhenNotFound() {
        when(borrowDAO.findLatestBorrowOfABookFromUser(2L, "1NWDswEACAAJ")).thenThrow(new BorrowNotFoundException("Borrow not found"));
        Borrow result = borrowService.getLatestBorrowOfABookFromUser(2L, "1NWDswEACAAJ");
        assertNull(result);
    }

    @Test
    public void testUpdateBorrow() {
        Book newBook = new Book("1234", "new book", new String[]{"Author"});
        newBook.setPageCount(500);
        User newUser = new Member();
        newUser.setFullName("Jone");
        borrow.setBook(newBook);
        borrow.setUser(newUser);
        doNothing().when(borrowDAO).updateBorrow(anyLong(), any(Borrow.class));
        borrowService.updateBorrow(1L, borrow);

        ArgumentCaptor<Borrow> borrowCaptor = ArgumentCaptor.forClass(Borrow.class);
        verify(borrowDAO).updateBorrow(eq(1L), borrowCaptor.capture());
        Borrow saveBorrow = borrowCaptor.getValue();
        assertEquals(newBook,saveBorrow.getBook() );
        assertEquals(newUser, saveBorrow.getUser());
    }

    @Test
    public void testUpdatePagesRead() {
        Long borrowId = 1L;
        int initialPagesRead = 0;
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        borrow.setPagesRead(initialPagesRead);
        when(borrowDAO.findById(borrowId)).thenReturn(borrow);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            int pages = invocation.getArgument(1);
            if (borrow.getId().equals(id)) {
                borrow.setPagesRead(pages);
            }
            return null;
        }).when(borrowDAO).updatePagesRead(anyLong(), anyInt());
        borrowService.updatePagesRead(borrowId, 50);
        verify(borrowDAO, times(1)).updatePagesRead(borrowId, 50);
        assertEquals(Long.valueOf(borrow.getPagesRead()),Long.valueOf(50));
    }


    @Test
    public void testUpdatePagesRead_WhenExceptionOccurs() {
        doThrow(new IllegalPagesReadException("Invalid pages read"))
                .when(borrowDAO).updatePagesRead(anyLong(), anyInt());
        borrowService.updatePagesRead(1L, 50);
        verify(borrowDAO, times(1)).updatePagesRead(1L, 50);
    }

    @Test
    public void testUpdateBorrowStatus() {
        Long borrowId = 2L;
        Borrow borrow = new Borrow();
        String status = "Returned";
        borrow.setStatus(status);
        borrow.setId(borrowId);
        when(borrowDAO.findById(borrowId)).thenReturn(borrow);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            String s = invocation.getArgument(1);
            if (borrow.getId().equals(id)) {
                borrow.setStatus(s);
            }
            return null;
        }).when(borrowDAO).updateStatus(borrowId, status);
        borrowService.updateBorrowStatus(borrowId, status);
        verify(borrowDAO, times(1)).updateStatus(borrowId, status);
        assertEquals(borrow.getStatus(),status);

    }

    @Test
    public void testGetSum() {
        when(borrowDAO.getSum()).thenReturn(100);
        Integer result = borrowService.getSum();
        assertNotNull(result);
        assertEquals(Integer.valueOf(100), result);
    }
    @Test
    public void testGetLatestNBorrows() {
        int n = 5;
        List<Borrow> borrows = new ArrayList<>();
        borrows.add(new Borrow());
        borrows.add(new Borrow());
        when(borrowDAO.findLatestNBorrows(n)).thenReturn(borrows);
        List<Borrow> result = borrowService.getLatestNBorrows(n);
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    @Test
    public void testGetNumBorrowsOfBookWithStatus() {
        String bookId = "book1";
        String status = "Borrowing";
        when(borrowDAO.countByBookAndStatus(bookId, status)).thenReturn(3);
        int result = borrowService.getNumBorrowsOfBookWithStatus(bookId, status);
        assertEquals(3, result);
    }
}
