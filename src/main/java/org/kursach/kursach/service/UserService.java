package org.kursach.kursach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kursach.kursach.model.User;
import org.kursach.kursach.repository.UserRepository;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class UserService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    
    @Inject
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        try {
            logger.info("UserService: Запрос получения всех пользователей");
            if (userRepository == null) {
                throw new IllegalStateException("UserRepository не инжектирован");
            }
            List<User> result = userRepository.findAll();
            logger.info("UserService: Получено пользователей: " + result.size());
            return result;
        } catch (Exception e) {
            logger.severe("UserService: Ошибка при получении всех пользователей: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public User getUserById(Long id) {
        try {
            logger.info("UserService: Получение пользователя по ID: " + id);
            return userRepository.findById(id);
        } catch (Exception e) {
            logger.severe("UserService: Ошибка при получении пользователя по ID: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public User getUserByUsername(String username) {
        try {
            logger.info("UserService: Получение пользователя по имени пользователя: " + username);
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            logger.severe("UserService: Ошибка при получении пользователя по имени: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public User getUserByEmail(String email) {
        try {
            logger.info("UserService: Получение пользователя по email: " + email);
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            logger.severe("UserService: Ошибка при получении пользователя по email: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<User> getUsersByRole(String role) {
        try {
            logger.info("UserService: Получение пользователей по роли: " + role);
            return userRepository.findByRole(role);
        } catch (Exception e) {
            logger.severe("UserService: Ошибка при получении пользователей по роли: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<User> getActiveUsers() {
        try {
            logger.info("UserService: Получение активных пользователей");
            return userRepository.findActiveUsers();
        } catch (Exception e) {
            logger.severe("UserService: Ошибка при получении активных пользователей: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void saveUser(User user) {
        try {
            logger.info("UserService: Сохранение пользователя: " + user);
            if (userRepository == null) {
                throw new IllegalStateException("UserRepository не инжектирован");
            }
            userRepository.save(user);
            logger.info("UserService: Пользователь успешно сохранен");
        } catch (Exception e) {
            logger.severe("UserService: Ошибка при сохранении пользователя: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void deleteUser(Long id) {
        try {
            logger.info("UserService: Удаление пользователя с ID: " + id);
            userRepository.delete(id);
            logger.info("UserService: Пользователь успешно удален");
        } catch (Exception e) {
            logger.severe("UserService: Ошибка при удалении пользователя: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 