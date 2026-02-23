package org.kursach.kursach.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.kursach.kursach.model.Document;
import org.kursach.kursach.model.Citizen;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class DocumentRepositoryImpl implements DocumentRepository {
    
    private static final Logger logger = Logger.getLogger(DocumentRepositoryImpl.class.getName());
    
    public DocumentRepositoryImpl() {}
    
    @Inject
    private EntityManager em;
    
    @Override
    public Document findById(Long id) {
        return em.find(Document.class, id);
    }
    
    @Override
    public List<Document> findAll() {
        try {
            logger.info("DocumentRepository: Выполняется запрос получения всех документов");
            if (em == null) {
                throw new IllegalStateException("EntityManager не инжектирован");
            }
            
            TypedQuery<Document> query = em.createQuery("SELECT d FROM Document d", Document.class);
            List<Document> result = query.getResultList();
            logger.info("DocumentRepository: Найдено документов: " + result.size());
            return result;
        } catch (Exception e) {
            logger.severe("DocumentRepository: Ошибка при получении списка документов: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при получении списка документов", e);
        }
    }
    
    @Override
    public List<Document> findByDocumentName(String documentName) {
        if (documentName == null || documentName.trim().isEmpty()) {
            return findAll();
        }
        
        String searchPattern = "%" + documentName.toLowerCase() + "%";
        TypedQuery<Document> query = em.createQuery(
            "SELECT d FROM Document d WHERE LOWER(d.documentName) LIKE :documentName", 
            Document.class);
        query.setParameter("documentName", searchPattern);
        return query.getResultList();
    }
    
    @Override
    public List<Document> findBySeries(String series) {
        if (series == null || series.trim().isEmpty()) {
            return List.of();
        }
        
        TypedQuery<Document> query = em.createQuery(
            "SELECT d FROM Document d WHERE d.series = :series", 
            Document.class);
        query.setParameter("series", series);
        return query.getResultList();
    }
    
    @Override
    public List<Document> findByCitizen(Citizen citizen) {
        if (citizen == null) {
            return List.of();
        }
        
        TypedQuery<Document> query = em.createQuery(
            "SELECT d FROM Document d WHERE d.citizen = :citizen", 
            Document.class);
        query.setParameter("citizen", citizen);
        return query.getResultList();
    }
    
    @Override
    public void save(Document document) {
        boolean transactionActive = false;
        try {
            logger.info("DocumentRepository: Начало сохранения документа: " + document);
            if (em == null) {
                throw new IllegalStateException("EntityManager не инжектирован");
            }
            
            if (!em.getTransaction().isActive()) {
                logger.info("DocumentRepository: Начинаем новую транзакцию");
                em.getTransaction().begin();
                transactionActive = true;
            }
            
            if (document.getId() == null) {
                logger.info("DocumentRepository: Persist нового документа");
                em.persist(document);
            } else {
                logger.info("DocumentRepository: Merge существующего документа с ID: " + document.getId());
                document = em.merge(document);
            }
            
            if (transactionActive) {
                logger.info("DocumentRepository: Коммит транзакции");
                em.getTransaction().commit();
                logger.info("DocumentRepository: Документ успешно сохранен");
            }
        } catch (Exception e) {
            logger.severe("DocumentRepository: Ошибка при сохранении документа: " + e.getMessage());
            e.printStackTrace();
            if (transactionActive && em.getTransaction().isActive()) {
                logger.info("DocumentRepository: Откат транзакции");
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при сохранении документа", e);
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
            
            Document document = findById(id);
            if (document != null) {
                em.remove(document);
            }
            
            if (transactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Ошибка при удалении документа", e);
        }
    }
} 