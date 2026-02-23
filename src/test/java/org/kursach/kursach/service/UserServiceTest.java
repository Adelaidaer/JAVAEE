package org.kursach.kursach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.kursach.kursach.model.User;
import org.kursach.kursach.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("admin");
        user1.setEmail("admin@example.com");
        user1.setPasswordHash("hashedPassword123");
        user1.setFullName("Администратор Системы");
        user1.setRole("ADMIN");
        user1.setActive(true);
        user1.setDateCreated(LocalDateTime.of(2024, 1, 1, 10, 0));
        
        user2 = new User();
        user2.setId(2L);
        user2.setUsername("operator");
        user2.setEmail("operator@example.com");
        user2.setPasswordHash("hashedPassword456");
        user2.setFullName("Оператор Первый");
        user2.setRole("OPERATOR");
        user2.setActive(true);
        user2.setDateCreated(LocalDateTime.of(2024, 1, 2, 10, 0));
    }

    @Test
    void testGetAllUsers() {
        // Подготовка
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Выполнение
        List<User> result = userService.getAllUsers();

        // Проверка
        assertEquals(2, result.size());
        assertEquals("admin", result.get(0).getUsername());
        assertEquals("operator", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        // Подготовка
        when(userRepository.findById(1L)).thenReturn(user1);

        // Выполнение
        User result = userService.getUserById(1L);

        // Проверка
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchUsersByUsername() {
        // Подготовка
        String searchUsername = "admin";
        when(userRepository.findByUsername(searchUsername)).thenReturn(user1);

        // Выполнение
        User result = userService.getUserByUsername(searchUsername);

        // Проверка
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(userRepository, times(1)).findByUsername(searchUsername);
    }

    @Test
    void testGetUserByEmail() {
        // Подготовка
        when(userRepository.findByEmail("admin@example.com")).thenReturn(user1);

        // Выполнение
        User result = userService.getUserByEmail("admin@example.com");

        // Проверка
        assertNotNull(result);
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("admin", result.getUsername());
        verify(userRepository, times(1)).findByEmail("admin@example.com");
    }

    @Test
    void testGetUsersByRole() {
        // Подготовка
        String role = "ADMIN";
        when(userRepository.findByRole(role)).thenReturn(Arrays.asList(user1));

        // Выполнение
        List<User> result = userService.getUsersByRole(role);

        // Проверка
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getRole());
        verify(userRepository, times(1)).findByRole(role);
    }

    @Test
    void testGetActiveUsers() {
        // Подготовка
        when(userRepository.findActiveUsers()).thenReturn(Arrays.asList(user1, user2));

        // Выполнение
        List<User> result = userService.getActiveUsers();

        // Проверка
        assertEquals(2, result.size());
        assertTrue(result.get(0).getActive());
        assertTrue(result.get(1).getActive());
        verify(userRepository, times(1)).findActiveUsers();
    }

    @Test
    void testSaveUser() {
        // Выполнение
        userService.saveUser(user1);

        // Проверка
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void testDeleteUser() {
        // Выполнение
        userService.deleteUser(1L);

        // Проверка
        verify(userRepository, times(1)).delete(1L);
    }
} 