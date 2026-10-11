package com.ddd.bookverse.controller;

/**
 * Interface defining the contract for search functionality in the application.
 * This interface provides methods for executing searches, managing search results,
 * and rendering search-related components.
 */
public interface SearchController {

    /**
     * Gets the type of the current search.
     *
     * @return a string representing the search type.
     */
    String getSearchType();

    /**
     * Executes the search process based on the current configuration or parameters.
     * This method typically triggers the search logic and prepares the results.
     */
    void searchContent();

    /**
     * Executes a search query with specified parameters.
     *
     * @param query        the search query as a string.
     * @param type         the type of search to execute (e.g., book, author, genre).
     * @param isAddHistory a boolean indicating whether to add this search to the search history.
     */
    void search(String query, String type, boolean isAddHistory);

    /**
     * Processes and prepares the results of a search query for display.
     * This method is called after the search logic has been executed.
     */
    void searchResult();

    /**
     * Renders the search results in the user interface.
     * This method is responsible for displaying the formatted search results.
     */
    void renderResultSearch();
}
