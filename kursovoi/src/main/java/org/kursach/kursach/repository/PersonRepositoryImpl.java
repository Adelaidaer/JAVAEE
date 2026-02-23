package org.kursach.kursach.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.kursach.kursach.model.Person;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class PersonRepositoryImpl implements PersonRepository {
    
    private static final Logger logger = Logger.getLogger(PersonRepositoryImpl.class.getName());
    
    public PersonRepositoryImpl() {}
    
    @Inject
    private EntityManager em;
    
    @Override
    public Person findById(Long id) {
        return em.find(Person.class, id);
    }
    
    @Override
    public List<Person> findAll() {
        try {
            logger.info("PersonRepository: Выполняется запрос получения всех лиц");
            if (em == null) {
                throw new IllegalStateException("EntityManager не инжектирован");
            }
            
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
            List<Person> result = query.getResultList();
            logger.info("PersonRepository: Найдено лиц: " + result.size());
            return result;
        } catch (Exception e) {
            logger.severe("PersonRepository: Ошибка при получении списка лиц: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при получении списка лиц", e);
        }
    }
    
    @Override
    public List<Person> findByShiffer(String shiffer) {
        if (shiffer == null || shiffer.trim().isEmpty()) {
            return findAll();
        }
        
        String searchPattern = "%" + shiffer.toLowerCase() + "%";
        TypedQuery<Person> query = em.createQuery(
            "SELECT p FROM Person p WHERE LOWER(p.shiffer) LIKE :shiffer", 
            Person.class);
        query.setParameter("shiffer", searchPattern);
        return query.getResultList();
    }
    
    @Override
    public Person findByInn(String inn) {
        if (inn == null || inn.trim().isEmpty()) {
            return null;
        }
        
        TypedQuery<Person> query = em.createQuery(
            "SELECT p FROM Person p WHERE p.inn = :inn", 
            Person.class);
        query.setParameter("inn", inn);
        List<Person> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public List<Person> findByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return findAll();
        }
        
        TypedQuery<Person> query = em.createQuery(
            "SELECT p FROM Person p WHERE p.type = :type", 
            Person.class);
        query.setParameter("type", type);
        return query.getResultList();
    }
    
    @Override
    public void save(Person person) {
        boolean transactionActive = false;
        try {
            logger.info("PersonRepository: Начало сохранения лица: " + person);
            if (em == null) {
                throw new IllegalStateException("EntityManager не инжектирован");
            }
            
            if (!em.getTransaction().isActive()) {
                logger.info("PersonRepository: Начинаем новую транзакцию");
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            if (person.getId() == null) {
                logger.info("PersonRepository: Persist нового лица");
                em.persist(person);
            } else {
                logger.info("PersonRepository: Merge существующего лица с ID: " + person.getId());
                person = em.merge(person);
            }
            
            if (transactionActive) {
                logger.info("PersonRepository: Коммит транзакции");
                em.getTransaction().commit();
                logger.info("PersonRepository: Лицо успешно сохранено");
            }
        } catch (Exception e) {
            logger.severe("PersonRepository: Ошибка при сохранении лица: " + e.getMessage());
            e.printStackTrace();
            if (transactionActive && em.getTransaction().isActive()) {
                logger.info("PersonRepository: Откат транзакции");
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при сохранении лица", e);
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
            
            Person person = findById(id);
            if (person != null) {
                em.remove(person);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при удалении лица", e);
        }
    }
} 