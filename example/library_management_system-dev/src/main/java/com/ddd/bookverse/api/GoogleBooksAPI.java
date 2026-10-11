package com.ddd.bookverse.api;

import com.ddd.bookverse.config.DatabaseConnection;
import com.ddd.bookverse.dao.BookDAO;
import com.ddd.bookverse.model.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ddd.bookverse.api.BookAPIClient.parseBook;
import static com.ddd.bookverse.constants.Constants.*;

/**
 * This class provides methods to interact with the Google Books API.
 */
public class GoogleBooksAPI {

    /**
     * Creates a URL for querying the Google Books API.
     *
     * @param query The search query.
     * @return A URL object for the query.
     * @throws IOException If an I/O error occurs.
     */
    public static URL createUrl(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        String urlString = GOOG_BOOK_API_URL + encodedQuery + "&key=" + API_KEY;
        return new URL(urlString);
    }

    /**
     * Searches for a book using a query string.
     *
     * @param query The search query.
     * @return A Book object if found, otherwise null.
     * @throws Exception If an error occurs during the search.
     */
    public static Book searchBook(String query) throws Exception {
        URL url = createUrl(query);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }

        reader.close();
        connection.disconnect();

        JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray items = jsonObject.getAsJsonArray("items");

        if (items != null && items.size() > 0) {
            JsonObject bookJson = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
            String bookId = items.get(0).getAsJsonObject().get("id").getAsString();
            return parseBook(bookId, bookJson, "");
        }
        return null;
    }

    /**
     * Retrieves a list of books by category.
     *
     * @param category The category to search for.
     * @return A list of Book objects in the specified category.
     * @throws IOException If an I/O error occurs.
     */
    public static List<Book> getBooksByCategory(String category) throws IOException {
        String encodedCategory = URLEncoder.encode(category, "UTF-8");
        String urlString = GOOG_BOOK_API_URL + "subject:" + encodedCategory + "&maxResults=40&key=" + API_KEY;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }

        reader.close();
        connection.disconnect();

        JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray items = jsonObject.getAsJsonArray("items");

        if (items == null || items.size() == 0) {
            return Collections.emptyList();
        }

        List<Book> books = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            JsonObject bookJson = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
            String bookId = items.get(i).getAsJsonObject().get("id").getAsString();
            Book newBook = parseBook(bookId, bookJson, category);
            if (newBook != null) books.add(newBook);
        }

        return books;
    }

    /**
     * Searches for a book by its ISBN.
     *
     * @param isbn The ISBN of the book.
     * @return A Book object if found, otherwise null.
     */
    public static Book searchBookByISBN(String isbn) {
        try {
            String urlString = GOOG_BOOK_API_URL + "isbn:" + isbn + "&key=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                connection.disconnect();

                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonArray items = jsonObject.getAsJsonArray("items");

                if (items != null && items.size() > 0) {
                    JsonObject bookJson = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
                    String bookId = items.get(0).getAsJsonObject().get("id").getAsString();
                    return parseBook(bookId, bookJson, "");
                }
            } else {
                System.out.println("Error: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}