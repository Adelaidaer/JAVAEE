package org.kursach.kursach.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.Document;
import org.kursach.kursach.service.DocumentService;

import java.util.logging.Logger;

@Named
@ApplicationScoped
@FacesConverter(value = "documentConverter", managed = true)
public class DocumentConverter implements Converter<Document> {
    
    private static final Logger logger = Logger.getLogger(DocumentConverter.class.getName());
    
    @Inject
    private DocumentService documentService;

    @Override
    public Document getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            Long id = Long.valueOf(value);
            Document document = documentService.getDocumentById(id);
            logger.info("Converted string '" + value + "' to Document: " + (document != null ? document.getDocumentName() : "null"));
            return document;
        } catch (NumberFormatException e) {
            logger.warning("Failed to parse Document ID from value: " + value);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Document document) {
        if (document == null) {
            logger.fine("Получен null объект в getAsString");
            return "";
        }
        
        try {
            // Защита от неправильного типа
            if (!(document instanceof Document)) {
                logger.warning("Ожидался объект Document, но получен: " + document.getClass().getName());
                return "";
            }
            
            if (document.getId() == null) {
                logger.warning("Document с null ID: " + document.getDocumentName());
                return "";
            }
            
            String result = document.getId().toString();
            logger.info("Конвертирован документ '" + document.getDocumentName() + "' в строку: " + result);
            return result;
        } catch (Exception e) {
            logger.severe("Ошибка в getAsString: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
} 