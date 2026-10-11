package com.ddd.bookverse.model;


/**
 * Entity class representing an Admin user.
 */
public class Admin extends User {

    /**
     * Default constructor for Admin.
     */
    public Admin() {
        super();
    }

    /**
     * Constructor for Admin with specified details.
     *
     * @param username The username of the admin.
     * @param password The password of the admin.
     * @param email The email of the admin.
     * @param fullName The full name of the admin.
     */
    public Admin(String username, String password, String email, String fullName) {
        super(username, password, email, fullName);
    }

    /**
     * Getter for the role of the admin.
     *
     * @return The role of the admin.
     */
    public String getRole() {
        // Role of the Admin
        return adminRole;
    }
}