package com.ddd.bookverse.singleton;

/**
 * Singleton class to manage user session.
 */
public class UserSession {
    private static UserSession instance; // Variable to hold the single instance of the class
    private Long userId;
    private String username;
    private String role;
    private boolean loggedIn;

    /**
     * Private constructor to prevent instantiation.
     */
    private UserSession() {
        this.loggedIn = false;
    }

    /**
     * Returns the singleton instance of UserSession.
     *
     * @return The singleton instance of UserSession.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Logs in the user by setting user details and loggedIn status.
     *
     * @param userId   The ID of the user.
     * @param username The username of the user.
     * @param role     The role of the user.
     */
    public void login(Long userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.loggedIn = true;
    }

    /**
     * Logs out the user by clearing user details and setting loggedIn status to false.
     */
    public void logout() {
        this.userId = null;
        this.username = null;
        this.role = null;
        this.loggedIn = false;
    }

    /**
     * Checks if the user is logged in.
     *
     * @return true if the user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Gets the user ID.
     *
     * @return The ID of the user.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Gets the username.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the role of the user.
     *
     * @return The role of the user.
     */
    public String getRole() {
        return role;
    }
}