package com.ddd.bookverse.api;

import com.ddd.bookverse.model.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleBooksAPITest {

    @Test
    void testSearchBook_ValidQuery_ReturnsBook() throws Exception {
        Book book = GoogleBooksAPI.searchBook("Effective Java");
        assertNotNull(book);
    }

    @Test
    void testSearchBook_InvalidQuery_ReturnsNull() throws Exception {
        Book book = GoogleBooksAPI.searchBook("agejonsdzjn");
        assertNull(book);
    }

    @Test
    void test_getBooksByCategory() throws  Exception {
        List<Book> books = GoogleBooksAPI.getBooksByCategory("History");
        for(Book book : books) {
            String[] categories = book.getCategories();
            assertTrue(Arrays.asList(categories).contains("History"), "Category should contain 'History'");
        }
    }
    @Test
    void test_getBooksByCategory_ReturnsNull() throws  Exception {
        List<Book> books = GoogleBooksAPI.getBooksByCategory("rwagjosdfng");
        for(Book book : books) {
            assertNull(book);
        }
    }

    @Test
    void test_searchBookByISBN()throws Exception {
        Book book = GoogleBooksAPI.searchBookByISBN("9783527693252");
        assertEquals(book.getIsbn(),"9783527693252");
    }

    @Test
    void test_searchBookByISBN_ReturnsNull()throws Exception {
        Book book = GoogleBooksAPI.searchBookByISBN("780134685991");
        assertNull(book);
    }
}
