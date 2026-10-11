package com.ddd.bookverse.api;

import com.ddd.bookverse.model.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookAPIClientTest {

    @Test
    void testParseBook_ValidInput_ReturnsBook() {
        // Mock JSON input
        JsonObject bookJson = new JsonObject();
        bookJson.addProperty("title", "Effective Java");
        bookJson.addProperty("description", "A programming book.");
        bookJson.addProperty("publishedDate", "2008-05-08");
        bookJson.addProperty("publisher", "Addison-Wesley");
        bookJson.addProperty("pageCount", 416);
        // Industry Identifiers
        JsonArray industryIdentifiers = new JsonArray();
        JsonObject identifier = new JsonObject();
        identifier.addProperty("identifier", "9780134685991");
        industryIdentifiers.add(identifier);
        bookJson.add("industryIdentifiers", industryIdentifiers);
        // Authors
        JsonArray authors = new JsonArray();
        authors.add("Joshua Bloch");
        bookJson.add("authors", authors);
        // Categories
        JsonArray categories = new JsonArray();
        categories.add("Programming");
        bookJson.add("categories", categories);
        // Image Links
        JsonObject imageLinks = new JsonObject();
        imageLinks.addProperty("thumbnail", "http://books.google.com/books/content?id=CZ-_DAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api");
        bookJson.add("imageLinks", imageLinks);
        // Call parseBook
        Book book = BookAPIClient.parseBook("1", bookJson, "Programming");
        // Assertions
        assertNotNull(book);
        assertEquals("Effective Java", book.getTitle());
        assertEquals("9780134685991", book.getIsbn());
        assertEquals("A programming book.", book.getDescription());
        assertEquals("2008-05-08", book.getPublishDate());
        assertEquals("Addison-Wesley", book.getPublisher());
        assertEquals(416, book.getPageCount());
        assertArrayEquals(new String[]{"Joshua Bloch"}, book.getAuthors());
        assertArrayEquals(new String[]{"Programming"}, book.getCategories());
        assertNotNull(book.getImage());
        assertEquals(1, book.getQuantityInStock());
    }

    @Test
    void testParseBook_InvalidPageCount_ReturnsNull() {
        JsonObject bookJson = new JsonObject();
        bookJson.addProperty("title", "Invalid Book");
        bookJson.addProperty("description", "Invalid page count.");
        bookJson.addProperty("publishedDate", "2020-01-01");
        bookJson.addProperty("publisher", "Unknown");
        bookJson.addProperty("pageCount", -1);
        // Call parseBook
        Book book = BookAPIClient.parseBook("2", bookJson, "Programming");
        // Assertions
        assertNull(book);
    }

    @Test
    void testParseBook_CategoryMismatch_ReturnsNull() {
        JsonObject bookJson = new JsonObject();
        bookJson.addProperty("title", "Book Without Matching Category");
        bookJson.addProperty("description", "A book with mismatched category.");
        bookJson.addProperty("publishedDate", "2022-12-31");
        bookJson.addProperty("publisher", "Random Publisher");
        bookJson.addProperty("pageCount", 123);
        JsonArray categories = new JsonArray();
        categories.add("Science");
        bookJson.add("categories", categories);
        Book book = BookAPIClient.parseBook("3", bookJson, "Programming");
        assertNull(book);
    }

    @Test
    void testParseBook_NoIndustryIdentifiers_ReturnsNull() {
        // Mock JSON input without industryIdentifiers
        JsonObject bookJson = new JsonObject();
        bookJson.addProperty("title", "Book Without ISBN");
        bookJson.addProperty("description", "Missing ISBN.");
        bookJson.addProperty("publishedDate", "2015-06-15");
        bookJson.addProperty("publisher", "No Publisher");
        bookJson.addProperty("pageCount", 250);
        Book book = BookAPIClient.parseBook("4", bookJson, "Programming");
        assertNull(book);
    }
}
