package org.kursach.kursach.repository;

import org.kursach.kursach.model.Document;
import org.kursach.kursach.model.Citizen;

import java.util.List;

/**
 * Репозиторий для управления сущностями Document
 */
public interface DocumentRepository extends Repository<Document, Long> {
    
    /**
     * Найти документы по фрагменту названия
     * @param documentName фрагмент названия документа для поиска
     * @return список найденных документов
     */
    List<Document> findByDocumentName(String documentName);
    
    /**
     * Найти документы по серии
     * @param series серия документа
     * @return список найденных документов
     */
    List<Document> findBySeries(String series);
    
    /**
     * Найти документы по гражданину
     * @param citizen гражданин
     * @return список документов гражданина
     */
    List<Document> findByCitizen(Citizen citizen);
} 