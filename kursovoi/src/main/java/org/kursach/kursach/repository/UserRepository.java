package org.kursach.kursach.repository;

import org.kursach.kursach.model.User;

import java.util.List;

/**
 * Репозиторий для управления сущностями User
 */
public interface UserRepository extends Repository<User, Long> {
    
    /**
     * Найти пользователя по имени пользователя
     * @param username имя пользователя
     * @return найденный пользователь или null
     */
    User findByUsername(String username);
    
    /**
     * Найти пользователя по email
     * @param email email пользователя
     * @return найденный пользователь или null
     */
    User findByEmail(String email);
    
    /**
     * Найти пользователей по роли
     * @param role роль пользователя
     * @return список пользователей с указанной ролью
     */
    List<User> findByRole(String role);
    
    /**
     * Найти активных пользователей
     * @return список активных пользователей
     */
    List<User> findActiveUsers();
} 