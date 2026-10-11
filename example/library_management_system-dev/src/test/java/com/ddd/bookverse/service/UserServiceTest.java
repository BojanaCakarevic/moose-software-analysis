package com.ddd.bookverse.service;

import com.ddd.bookverse.dao.UserDAO;
import com.ddd.bookverse.exception.UserNotFoundException;
import com.ddd.bookverse.model.Admin;
import com.ddd.bookverse.model.Member;
import com.ddd.bookverse.model.User;
import com.ddd.bookverse.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    private UserDAO userDAO;

    @Before
    public void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    public void testAddMember() {
        userService.addMember("username", "password", "email@test.com", "Full Name");
        verify(userDAO, times(1)).save(any(Member.class));

    }

    @Test
    public void testAddAdmin() {
        userService.addAdmin("admin", "password", "admin@test.com", "Admin Name");
        verify(userDAO, times(1)).save(any(Admin.class));
    }

    @Test
    public void testUpdateUser() {
        User user = new Member("username", "password", "email@test.com", "Full Name");
        userService.updateUser(user);
        verify(userDAO, times(1)).update(user);
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(1L);
        verify(userDAO, times(1)).deleteById(1L);
    }

    @Test
    public void testFindUserById() throws UserNotFoundException {
        User user = new Member("username", "password", "email@test.com", "Full Name");
        when(userDAO.findById(1L)).thenReturn(user);

        User result = userService.findUserById(1L);
        assertNotNull(result);
        assertEquals("username", result.getUsername());
    }

    @Test
    public void testFindUserByUsername() throws UserNotFoundException {
        User user = new Member("username", "password", "email@test.com", "Full Name");
        when(userDAO.findByUsername("username")).thenReturn(user);

        User result = userService.findUserByUsername("username");
        assertNotNull(result);
        assertEquals("username", result.getUsername());
    }

    @Test
    public void testAuthenticate() throws UserNotFoundException {
        User user = new Member("username", "password", "email@test.com", "Full Name");
        when(userDAO.findByUsername("username")).thenReturn(user);

        assertTrue(userService.authenticate("username", "password"));
        assertFalse(userService.authenticate("username", "wrongpassword"));
    }

    @Test
    public void testIsUsernameTaken() throws UserNotFoundException {
        when(userDAO.findByUsername("username")).thenReturn(new Member());

        assertTrue(userService.isUsernameTaken("username"));
        when(userDAO.findByUsername("username")).thenThrow(UserNotFoundException.class);
        assertFalse(userService.isUsernameTaken("username"));
    }

    @Test
    public void testIsEmailTaken() throws UserNotFoundException {
        when(userDAO.findByEmail("email@test.com")).thenReturn(new Member());

        assertTrue(userService.isEmailTaken("email@test.com"));
        when(userDAO.findByEmail("email@test.com")).thenThrow(UserNotFoundException.class);
        assertFalse(userService.isEmailTaken("email@test.com"));
    }

    @Test
    public void testFindAllUsersByRole() {
        List<User> users = new ArrayList<>();
        users.add(new Member("user1", "password", "email1@test.com", "Full Name"));
        when(userDAO.findAllByRole("member")).thenReturn(users);

        List<User> result = userService.findAllUsersByRole("member");
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSum() {
        when(userDAO.getSum()).thenReturn(42);

        Integer sum = userService.getSum();
        assertEquals((Integer) 42, sum);
    }
}
