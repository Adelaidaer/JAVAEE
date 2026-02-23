package org.kursach.kursach.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.model.Person;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CitizenRepositoryImpl implements CitizenRepository {
    
    private static final Logger logger = Logger.getLogger(CitizenRepositoryImpl.class.getName());
    
    public CitizenRepositoryImpl() {}
    
    @Inject
    private EntityManager em;
    
    @Override
    public Citizen findById(Long id) {
        return em.find(Citizen.class, id);
    }
    
    @Override
    public List<Citizen> findAll() {
        try {
            logger.info("CitizenRepository: Выполняется запрос получения всех граждан");
            if (em == null) {
                throw new IllegalStateException("EntityManager не инжектирован");
            }
            
            TypedQuery<Citizen> query = em.createQuery("SELECT c FROM Citizen c", Citizen.class);
            List<Citizen> result = query.getResultList();
            logger.info("CitizenRepository: Найдено граждан: " + result.size());
            return result;
        } catch (Exception e) {
            logger.severe("CitizenRepository: Ошибка при получении списка граждан: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при получении списка граждан", e);
        }
    }
    
    @Override
    public List<Citizen> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        
        String searchPattern = "%" + name.toLowerCase() + "%";
        TypedQuery<Citizen> query = em.createQuery(
            "SELECT c FROM Citizen c WHERE LOWER(c.firstName) LIKE :name OR LOWER(c.lastName) LIKE :name OR LOWER(c.patronymic) LIKE :name", 
            Citizen.class);
        query.setParameter("name", searchPattern);
        return query.getResultList();
    }
    
    @Override
    public Citizen findByPersonalNumber(String personalNumber) {
        if (personalNumber == null || personalNumber.trim().isEmpty()) {
            return null;
        }
        
        TypedQuery<Citizen> query = em.createQuery(
            "SELECT c FROM Citizen c WHERE c.personalNumber = :personalNumber", 
            Citizen.class);
        query.setParameter("personalNumber", personalNumber);
        List<Citizen> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public List<Citizen> findByPerson(Person person) {
        if (person == null) {
            return List.of();
        }
        
        TypedQuery<Citizen> query = em.createQuery(
            "SELECT c FROM Citizen c WHERE c.person = :person", 
            Citizen.class);
        query.setParameter("person", person);
        return query.getResultList();
    }
    
    @Override
    public void save(Citizen citizen) {
        boolean transactionActive = false;
        try {
            logger.info("CitizenRepository: Начало сохранения гражданина: " + citizen);
            if (em == null) {
                throw new IllegalStateException("EntityManager не инжектирован");
            }
            
            if (!em.getTransaction().isActive()) {
                logger.info("CitizenRepository: Начинаем новую транзакцию");
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            if (citizen.getId() == null) {
                logger.info("CitizenRepository: Persist нового гражданина");
                em.persist(citizen);
            } else {
                logger.info("CitizenRepository: Merge существующего гражданина с ID: " + citizen.getId());
                citizen = em.merge(citizen);
            }
            
            if (transactionActive) {
                logger.info("CitizenRepository: Коммит транзакции");
                em.getTransaction().commit();
                logger.info("CitizenRepository: Гражданин успешно сохранен");
            }
        } catch (Exception e) {
            logger.severe("CitizenRepository: Ошибка при сохранении гражданина: " + e.getMessage());
            e.printStackTrace();
            if (transactionActive && em.getTransaction().isActive()) {
                logger.info("CitizenRepository: Откат транзакции");
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при сохранении гражданина", e);
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
            
            Citizen citizen = findById(id);
            if (citizen != null) {
                em.remove(citizen);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при удалении гражданина", e);
        }
    }
} 