package org.kursach.kursach.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.Document;
import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.service.DocumentService;
import org.kursach.kursach.service.CitizenService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named
@ViewScoped
public class DocumentController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DocumentController.class.getName());
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private CitizenService citizenService;
    
    private List<Document> documents;
    private Document document;
    private Long editId;
    private String searchTerm;
    private List<Citizen> allCitizens;
    private Long selectedCitizenId;
    
    @PostConstruct
    public void init() {
        try {
            logger.info("Начало инициализации DocumentController");
            
            // Проверяем, что сервисы инжектированы
            if (documentService == null) {
                throw new IllegalStateException("DocumentService не инжектирован");
            }
            
            if (citizenService == null) {
                throw new IllegalStateException("CitizenService не инжектирован");
            }
            
            logger.info("Сервисы успешно инжектированы");
            
            // Загружаем данные с обработкой ошибок
            try {
                documents = documentService.getAllDocuments();
                allCitizens = citizenService.getAllCitizens();
                logger.info("Загружено документов: " + (documents != null ? documents.size() : 0));
                logger.info("Загружено граждан: " + (allCitizens != null ? allCitizens.size() : 0));
            } catch (Exception e) {
                logger.severe("Ошибка загрузки данных: " + e.getMessage());
                documents = new java.util.ArrayList<>();
                allCitizens = new java.util.ArrayList<>();
            }
            
            // Создаем новый Document только если текущий объект null
            if (document == null) {
                document = new Document();
                logger.info("Создан новый объект Document");
            } else {
                logger.info("Использован существующий объект Document с ID: " + document.getId());
            }
            
            // Пробуем загрузить из параметра
            try {
                loadDocument();
            } catch (Exception e) {
                logger.warning("Ошибка загрузки документа из параметров: " + e.getMessage());
            }
            
            logger.info("DocumentController успешно инициализирован");
            
        } catch (Exception e) {
            logger.severe("Критическая ошибка при инициализации DocumentController: " + e.getMessage());
            e.printStackTrace();
            
            // Инициализируем минимально необходимые объекты
            if (document == null) {
                document = new Document();
            }
            if (documents == null) {
                documents = new java.util.ArrayList<>();
            }
            if (allCitizens == null) {
                allCitizens = new java.util.ArrayList<>();
            }
            
            throw new RuntimeException("Не удалось инициализировать DocumentController", e);
        }
    }
    
    public String save() {
        try {
            // Проверяем, есть ли ID для редактирования
            if (editId != null && document.getId() == null) {
                document.setId(editId);
                logger.info("Используется ID из editId: " + editId);
            }
            
            // Привязываем выбранного гражданина к документу
            if (selectedCitizenId != null) {
                Citizen selectedCitizen = citizenService.getCitizenById(selectedCitizenId);
                if (selectedCitizen != null) {
                    document.setCitizen(selectedCitizen);
                    logger.info("Привязан гражданин с ID: " + selectedCitizenId + " к документу");
                } else {
                    logger.warning("Гражданин с ID " + selectedCitizenId + " не найден");
                }
            }
            
            boolean isNew = (document.getId() == null);
            logger.info((isNew ? "Создание нового" : "Обновление существующего") + " документа: " + document.getDocumentName());
            
            if (!isNew) {
                logger.info("ID редактируемого документа: " + document.getId());
            }
            
            // Добавляем логирование ID перед сохранением
            logger.info("Перед сохранением документа - ID: " + document.getId() + 
                         ", Название: " + document.getDocumentName() + 
                         ", Серия: " + document.getSeries());
            
            documentService.saveDocument(document);
            
            // Логируем ID после сохранения
            logger.info("После сохранения - статус операции: успешно");
            
            // Сбрасываем editId и selectedCitizenId
            editId = null;
            selectedCitizenId = null;
            
            // Обновляем список документов
            documents = documentService.getAllDocuments();
            // Создаем новый объект для формы
            document = new Document();
            
            logger.info("Документ " + (isNew ? "создан" : "обновлен") + " успешно");
            return "document?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Ошибка при сохранении документа: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public String edit(Long id) {
        try {
            logger.info("Редактирование документа с ID: " + id);
            document = documentService.getDocumentById(id);
            
            if (document == null) {
                logger.warning("Документ с ID " + id + " не найден");
                return "document?faces-redirect=true";
            }
            
            // Сохраняем ID для отслеживания
            this.editId = id;
            logger.info("Установлен editId: " + this.editId);
            
            // Устанавливаем selectedCitizenId из связанного гражданина
            if (document.getCitizen() != null) {
                this.selectedCitizenId = document.getCitizen().getId();
                logger.info("Установлен selectedCitizenId: " + this.selectedCitizenId);
            } else {
                this.selectedCitizenId = null;
                logger.info("Документ не привязан к гражданину");
            }
            
            // Передаем ID как параметр в URL
            return "document-edit?faces-redirect=true&id=" + id;
        } catch (Exception e) {
            logger.severe("Ошибка при загрузке документа для редактирования: " + e.getMessage());
            e.printStackTrace();
            return "document?faces-redirect=true";
        }
    }
    
    // Загрузка документа из параметра URL
    public void loadDocument() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.valueOf(idParam);
                this.editId = id;
                logger.info("Загрузка документа из параметра URL, ID: " + id);
                
                document = documentService.getDocumentById(id);
                if (document == null) {
                    logger.warning("Документ с ID " + id + " не найден при загрузке из параметра");
                    document = new Document();
                    selectedCitizenId = null;
                } else {
                    logger.info("Документ загружен из параметра URL: " + document.getDocumentName());
                    // Устанавливаем selectedCitizenId из связанного гражданина
                    if (document.getCitizen() != null) {
                        this.selectedCitizenId = document.getCitizen().getId();
                        logger.info("Установлен selectedCitizenId из загруженного документа: " + this.selectedCitizenId);
                    } else {
                        this.selectedCitizenId = null;
                        logger.info("Загруженный документ не привязан к гражданину");
                    }
                }
            } catch (NumberFormatException e) {
                logger.warning("Некорректный ID в параметре: " + idParam);
                document = new Document();
            }
        }
    }
    
    public String delete(Long id) {
        try {
            logger.info("Удаление документа с ID: " + id);
            documentService.deleteDocument(id);
            documents = documentService.getAllDocuments();
            return "document?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Ошибка при удалении документа: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public void search() {
        logger.info("Поиск документов по названию: " + searchTerm);
        documents = documentService.searchDocumentsByName(searchTerm);
    }
    
    public void resetSearch() {
        logger.info("Сброс поиска документов");
        searchTerm = null;
        documents = documentService.getAllDocuments();
    }
    
    public String prepareNew() {
        logger.info("Подготовка создания нового документа");
        document = new Document();
        editId = null;
        selectedCitizenId = null;
        return "document-edit?faces-redirect=true";
    }
    
    private void ensureInitialized() {
        if (documents == null) {
            try {
                documents = documentService.getAllDocuments();
            } catch (Exception e) {
                logger.severe("Ошибка при получении списка документов: " + e.getMessage());
                documents = new java.util.ArrayList<>();
            }
        }
        
        if (document == null) {
            document = new Document();
        }
        
        if (allCitizens == null) {
            try {
                allCitizens = citizenService.getAllCitizens();
            } catch (Exception e) {
                logger.severe("Ошибка при получении списка граждан: " + e.getMessage());
                allCitizens = new java.util.ArrayList<>();
            }
        }
    }
    
    // Геттеры и сеттеры
    public List<Document> getDocuments() {
        ensureInitialized();
        return documents;
    }
    
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    public Document getDocument() {
        if (document == null) {
            document = new Document();
        }
        return document;
    }
    
    public void setDocument(Document document) {
        this.document = document;
    }
    
    public Long getEditId() {
        return editId;
    }
    
    public void setEditId(Long editId) {
        this.editId = editId;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public List<Citizen> getAllCitizens() {
        ensureInitialized();
        return allCitizens;
    }
    
    public void setAllCitizens(List<Citizen> allCitizens) {
        this.allCitizens = allCitizens;
    }
    
    public Long getSelectedCitizenId() {
        return selectedCitizenId;
    }
    
    public void setSelectedCitizenId(Long selectedCitizenId) {
        this.selectedCitizenId = selectedCitizenId;
    }
} 