package org.kursach.kursach.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.kursach.kursach.model.User;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {
    
    private static final Logger logger = Logger.getLogger(UserRepositoryImpl.class.getName());
    
    public UserRepositoryImpl() {}
    
    @Inject
    private EntityManager em;
    
    @Override
    public User findById(Long id) {
        return em.find(User.class, id);
    }
    
    @Override
    public List<User> findAll() {
        try {
            logger.info("UserRepository: Выполняется запрос получения всех пользователей");
            if (em == null) {
                throw new IllegalStateException("EntityManager не инжектирован");
            }
            
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            List<User> result = query.getResultList();
            logger.info("UserRepository: Найдено пользователей: " + result.size());
            return result;
        } catch (Exception e) {
            logger.severe("UserRepository: Ошибка при получении списка пользователей: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при получении списка пользователей", e);
        }
    }
    
    @Override
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.username = :username", 
            User.class);
        query.setParameter("username", username);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.email = :email", 
            User.class);
        query.setParameter("email", email);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public List<User> findByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return List.of();
        }
        
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.role = :role", 
            User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }
    
    @Override
    public List<User> findActiveUsers() {
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.active = true", 
            User.class);
        return query.getResultList();
    }
    
    @Override
    public void save(User user) {
        boolean transactionActive = false;
        try {
            logger.info("UserRepository: Начало сохранения пользователя: " + user);
            if (em == null) {
                throw new IllegalStateException("EntityManager не инжектирован");
            }
            
            if (!em.getTransaction().isActive()) {
                logger.info("UserRepository: Начинаем новую транзакцию");
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            if (user.getId() == null) {
                logger.info("UserRepository: Persist нового пользователя");
                em.persist(user);
            } else {
                logger.info("UserRepository: Merge существующего пользователя с ID: " + user.getId());
                user = em.merge(user);
            }
            
            if (transactionActive) {
                logger.info("UserRepository: Коммит транзакции");
                em.getTransaction().commit();
                logger.info("UserRepository: Пользователь успешно сохранен");
            }
        } catch (Exception e) {
            logger.severe("UserRepository: Ошибка при сохранении пользователя: " + e.getMessage());
            e.printStackTrace();
            if (transactionActive && em.getTransaction().isActive()) {
                logger.info("UserRepository: Откат транзакции");
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }
    
    @Override
    public void delete(Long id) {
        boolean transactionActive = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            User user = findById(id);
            if (user != null) {
                em.remove(user);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при удалении пользователя", e);
        }
    }
} 