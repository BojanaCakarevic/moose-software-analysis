package com.ddd.bookverse.model;


/**
 * Entity class representing a Member user.
 */
public class Member extends User {

    /**
     * Default constructor for Member.
     */
    public Member() {
        super();
    }

    /**
     * Constructor for Member with specified details.
     *
     * @param username The username of the member.
     * @param password The password of the member.
     * @param email The email of the member.
     * @param fullName The full name of the member.
     */
    public Member(String username, String password, String email, String fullName) {
        super(username, password, email, fullName);
    }

    /**
     * Getter for the role of the member.
     *
     * @return The role of the member.
     */
    public String getRole() {
        // Role of the Member
        return memberRole;
    }

}