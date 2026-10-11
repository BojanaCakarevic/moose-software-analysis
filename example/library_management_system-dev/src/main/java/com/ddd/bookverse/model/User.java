package com.ddd.bookverse.model;


/**
 * Abstract entity class representing a User.
 */
public abstract class User {
    private Long id;

    private String username;
    private String password;
    private String email;
    private String fullName;

    public static final String memberRole = "MEMBER";
    public static final String adminRole = "ADMIN";

    /**
     * Default constructor for User.
     */
    public User() {
    }

    /**
     * Constructor for User with specified details.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param email The email of the user.
     * @param fullName The full name of the user.
     */
    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return The unique identifier of the user.
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id The unique identifier to set.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email of the user.
     *
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the full name of the user.
     *
     * @return The full name of the user.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the user.
     *
     * @param fullName The full name to set.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Abstract method to get the role of the user.
     *
     * @return The role of the user.
     */
    public abstract String getRole();

}