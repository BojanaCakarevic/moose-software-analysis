package com.ddd.bookverse.service;

import com.ddd.bookverse.dao.UserDAO;
import com.ddd.bookverse.exception.UserNotFoundException;
import com.ddd.bookverse.model.Admin;
import com.ddd.bookverse.model.Member;
import com.ddd.bookverse.model.User;

import java.util.List;

/**
 * Service class that handles user-related operations such as adding, updating,
 * deleting users, and authentication.
 */
public class UserService {
    private  UserDAO userDAO = new UserDAO();

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserService(){}


    /**
     * Adds a new member to the system.
     *
     * @param username the username of the member
     * @param password the password of the member
     * @param email the email of the member
     * @param fullName the full name of the member
     */
    public void addMember(String username, String password,
                          String email, String fullName) {
        Member member = new Member(username, password, email, fullName);
        userDAO.save(member);
    }


    /**
     * Adds a new admin to the system.
     *
     * @param username the username of the admin
     * @param password the password of the admin
     * @param email the email of the admin
     * @param fullName the full name of the admin
     */
    public void addAdmin(String username, String password,
                         String email, String fullName) {
        Admin admin = new Admin(username, password, email, fullName);
        userDAO.save(admin);
    }

    /**
     * Updates an existing user.
     *
     * @param user the user object containing updated information
     */
    public void updateUser(User user) {
        userDAO.update(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to be deleted
     */
    public void deleteUser(Long id) {
        try {
            userDAO.deleteById(id);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user if found, otherwise null
     */
    public User findUserById(Long id) {
        try {
            return userDAO.findById(id);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the user if found, otherwise null
     */
    public User findUserByUsername(String username) {
        try {
            return userDAO.findByUsername(username);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a list of users by their username and role.
     *
     * @param username the username of the users to retrieve
     * @param role the role of the users (e.g., "Admin", "Member")
     * @return a list of users that match the criteria
     */
    public List<User> findByUsernameAndRole(String username, String role) {
        try {
            return userDAO.findByUsernameAndRole(username,role);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a list of users by their ID and role.
     *
     * @param id the ID of the user to search for
     * @param role the role of the users (e.g., "Admin", "Member")
     * @return a list of users that match the criteria
     */
    public List<User> findUserByIDAndRole(Long id, String role) {
        try {
            return userDAO.findByIdAndRole(id, role);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a list of users by their email and role.
     *
     * @param email the email of the users to search for
     * @param role the role of the users (e.g., "Admin", "Member")
     * @return a list of users that match the criteria
     */
    public List<User> findByEmailAndRole(String email, String role) {
        try {
            return userDAO.findByEmailAndRole(email,role);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user to retrieve
     * @return the user if found, otherwise null
     */
    public User findUserByEmail(String email) {
        try {
            return userDAO.findByEmail(email);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves all users by their role.
     *
     * @param role the role of the users (e.g., "Admin", "Member")
     * @return a list of users with the specified role
     */
    public List<User> findAllUsersByRole(String role) {
        return userDAO.findAllByRole(role);
    }

    /**
     * Retrieves a list of users by their full name and role.
     *
     * @param fullName the full name of the users to search for
     * @param role the role of the users (e.g., "Admin", "Member")
     * @return a list of users that match the criteria
     */
    public List<User> findByFullnameAndRole(String fullName, String role) {
        return userDAO.findAllByFullNameAndRole(fullName,role);
    }

    /**
     * Authenticates a user by their username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return true if the username and password match, otherwise false
     */
    public boolean authenticate(String username, String password) {
        try {
            User user = userDAO.findByUsername(username);
            return user != null && user.getPassword().equals(password);
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks if the given username is already taken.
     *
     * @param username the username to check
     * @return true if the username is taken, otherwise false
     */
    public boolean isUsernameTaken(String username) {
        try {
            User user = userDAO.findByUsername(username);
            return user != null;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks if the given email is already taken.
     *
     * @param email the email to check
     * @return true if the email is taken, otherwise false
     */
    public boolean isEmailTaken(String email) {
        try {
            User user = userDAO.findByEmail(email);
            return user != null;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    /**
     * Retrieves the total number of users.
     *
     * @return the total number of users
     */
    public Integer getSum() {
        return userDAO.getSum();
    }

}
