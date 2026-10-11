package com.ddd.bookverse.api;

import com.ddd.bookverse.model.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.ddd.bookverse.constants.Constants.URL_NO_IMG;
import static com.ddd.bookverse.utils.ImageUtil.downloadImage;
import static com.ddd.bookverse.utils.ImageUtil.loadImageFromResources;

/**
 * This class provides methods to parse book information from a JSON object.
 */
public class BookAPIClient {

    /**
     * Parses a JSON object to create a Book object.
     *
     * @param bookId   The ID of the book.
     * @param bookJson The JSON object containing book information.
     * @param category The category to filter books.
     * @return A Book object if parsing is successful, otherwise null.
     */
    public static Book parseBook(String bookId, JsonObject bookJson, String category) {
        Book book = new Book();

        // Extract ISBN-13
        String isbn_13 = null;
        if (bookJson.has("industryIdentifiers")) {
            JsonArray industryIdentifiers = bookJson.getAsJsonArray("industryIdentifiers");
            if (industryIdentifiers.size() > 0) {
                isbn_13 = industryIdentifiers.get(0).getAsJsonObject().get("identifier").getAsString();
                if (isbn_13.length() < 13 && industryIdentifiers.size() > 1) {
                    isbn_13 = industryIdentifiers.get(1).getAsJsonObject().get("identifier").getAsString();
                }
            }
        }

        book.setIsbn(isbn_13);
        book.setId(bookId);
        book.setTitle(bookJson.get("title").getAsString());

        // Extract authors
        JsonArray authorsJson = bookJson.has("authors") ? bookJson.getAsJsonArray("authors") : null;
        String[] authors = authorsJson != null ? new String[authorsJson.size()] : new String[0];
        if (authorsJson != null) {
            for (int i = 0; i < authorsJson.size(); i++) {
                authors[i] = authorsJson.get(i).getAsString();
            }
        }
        book.setAuthors(authors);

        // Extract description
        book.setDescription(bookJson.has("description")
                ? bookJson.get("description").getAsString()
                : "unknown");

        // Extract page count
        if (bookJson.has("pageCount")) {
            int pageCount = bookJson.get("pageCount").getAsInt();
            if (pageCount > 0) {
                book.setPageCount(pageCount);
            } else {
                return null;
            }
        } else {
            return null;
        }

        // Extract publish date and publisher
        book.setPublishDate(bookJson.has("publishedDate")
                ? bookJson.get("publishedDate").getAsString()
                : null);
        book.setPublisher(bookJson.has("publisher")
                ? bookJson.get("publisher").getAsString()
                : "unknown");

        // Extract and download image
        String imageLink = bookJson.has("imageLinks")
                ? bookJson.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                : null;

        byte[] imageBytes;

        if (imageLink != null && !imageLink.isEmpty()) {
            imageBytes = downloadImage(imageLink);
            if (imageBytes == null) {
                System.err.println("Failed to download image from URL. Loading default image.");
                imageBytes = loadImageFromResources(URL_NO_IMG);
            }
        } else {
            imageBytes = loadImageFromResources(URL_NO_IMG);
        }

        if (imageBytes != null) {
            book.setImage(imageBytes);
        } else {
            System.err.println("Failed to load any image.");
        }

        // Extract categories and filter by the given category
        JsonArray categoriesJson = bookJson.has("categories") ? bookJson.getAsJsonArray("categories") : null;
        String[] categories = categoriesJson != null ? new String[categoriesJson.size()] : new String[0];
        boolean isCategory = false;
        if (categoriesJson != null) {
            for (int i = 0; i < categoriesJson.size(); i++) {
                categories[i] = categoriesJson.get(i).getAsString();
                if (categories[i].equals(category)) {
                    isCategory = true;
                }
            }
            if (!isCategory && !category.isEmpty()) return null;
        } else {
            return null;
        }

        book.setCategories(categories);
        book.setQuantityInStock(1);
        book.setRatingCount(0);
        book.setRatingSum(0L);

        return book;
    }
}