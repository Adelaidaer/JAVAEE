package org.kursach.kursach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kursach.kursach.model.Document;
import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.repository.DocumentRepository;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class DocumentService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DocumentService.class.getName());
    
    @Inject
    private DocumentRepository documentRepository;
    
    public List<Document> getAllDocuments() {
        try {
            logger.info("DocumentService: Запрос получения всех документов");
            if (documentRepository == null) {
                throw new IllegalStateException("DocumentRepository не инжектирован");
            }
            List<Document> result = documentRepository.findAll();
            logger.info("DocumentService: Получено документов: " + result.size());
            return result;
        } catch (Exception e) {
            logger.severe("DocumentService: Ошибка при получении всех документов: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Document> searchDocumentsByName(String documentName) {
        try {
            logger.info("DocumentService: Поиск документов по названию: " + documentName);
            return documentRepository.findByDocumentName(documentName);
        } catch (Exception e) {
            logger.severe("DocumentService: Ошибка при поиске документов: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public Document getDocumentById(Long id) {
        try {
            logger.info("DocumentService: Получение документа по ID: " + id);
            return documentRepository.findById(id);
        } catch (Exception e) {
            logger.severe("DocumentService: Ошибка при получении документа по ID: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Document> getDocumentsBySeries(String series) {
        try {
            logger.info("DocumentService: Получение документов по серии: " + series);
            return documentRepository.findBySeries(series);
        } catch (Exception e) {
            logger.severe("DocumentService: Ошибка при получении документов по серии: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Document> getDocumentsByCitizen(Citizen citizen) {
        try {
            logger.info("DocumentService: Получение документов по гражданину: " + citizen);
            return documentRepository.findByCitizen(citizen);
        } catch (Exception e) {
            logger.severe("DocumentService: Ошибка при получении документов по гражданину: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void saveDocument(Document document) {
        try {
            logger.info("DocumentService: Сохранение документа: " + document);
            if (documentRepository == null) {
                throw new IllegalStateException("DocumentRepository не инжектирован");
            }
            documentRepository.save(document);
            logger.info("DocumentService: Документ успешно сохранен");
        } catch (Exception e) {
            logger.severe("DocumentService: Ошибка при сохранении документа: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void deleteDocument(Long id) {
        try {
            logger.info("DocumentService: Удаление документа с ID: " + id);
            documentRepository.delete(id);
            logger.info("DocumentService: Документ успешно удален");
        } catch (Exception e) {
            logger.severe("DocumentService: Ошибка при удалении документа: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 