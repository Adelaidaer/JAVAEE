package org.kursach.kursach.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.Person;
import org.kursach.kursach.service.PersonService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named
@ViewScoped
public class PersonController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PersonController.class.getName());
    
    @Inject
    private PersonService personService;
    
    private List<Person> persons;
    private Person person;
    private Long editId;
    private String searchTerm;
    
    @PostConstruct
    public void init() {
        try {
            logger.info("Начало инициализации PersonController");
            
            // Проверяем, что сервисы инжектированы
            if (personService == null) {
                throw new IllegalStateException("PersonService не инжектирован");
            }
            
            logger.info("Сервисы успешно инжектированы");
            
            // Загружаем данные с обработкой ошибок
            try {
                persons = personService.getAllPersons();
                logger.info("Загружено лиц: " + (persons != null ? persons.size() : 0));
            } catch (Exception e) {
                logger.severe("Ошибка загрузки лиц: " + e.getMessage());
                persons = new java.util.ArrayList<>();
            }
            
            // Создаем новый Person только если текущий объект null
            if (person == null) {
                person = new Person();
                logger.info("Создан новый объект Person");
            } else {
                logger.info("Использован существующий объект Person с ID: " + person.getId());
            }
            
            // Пробуем загрузить из параметра
            try {
                loadPerson();
            } catch (Exception e) {
                logger.warning("Ошибка загрузки лица из параметров: " + e.getMessage());
            }
            
            logger.info("PersonController успешно инициализирован");
            
        } catch (Exception e) {
            logger.severe("Критическая ошибка при инициализации PersonController: " + e.getMessage());
            e.printStackTrace();
            
            // Инициализируем минимально необходимые объекты
            if (person == null) {
                person = new Person();
            }
            if (persons == null) {
                persons = new java.util.ArrayList<>();
            }
            
            throw new RuntimeException("Не удалось инициализировать PersonController", e);
        }
    }
    
    public String save() {
        try {
            // Проверяем, есть ли ID для редактирования
            if (editId != null && person.getId() == null) {
                person.setId(editId);
                logger.info("Используется ID из editId: " + editId);
            }
            
            boolean isNew = (person.getId() == null);
            logger.info((isNew ? "Создание нового" : "Обновление существующего") + " лица: " + person.getShiffer());
            
            if (!isNew) {
                logger.info("ID редактируемого лица: " + person.getId());
            }
            
            // Добавляем логирование ID перед сохранением
            logger.info("Перед сохранением лица - ID: " + person.getId() + 
                         ", Шифр: " + person.getShiffer() + 
                         ", ИНН: " + person.getInn());
            
            personService.savePerson(person);
            
            // Логируем ID после сохранения
            logger.info("После сохранения - статус операции: успешно");
            
            // Сбрасываем editId
            editId = null;
            
            // Обновляем список лиц
            persons = personService.getAllPersons();
            // Создаем новый объект для формы
            person = new Person();
            
            logger.info("Лицо " + (isNew ? "создано" : "обновлено") + " успешно");
            return "faculty?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Ошибка при сохранении лица: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public String edit(Long id) {
        try {
            logger.info("Редактирование лица с ID: " + id);
            person = personService.getPersonById(id);
            
            if (person == null) {
                logger.warning("Лицо с ID " + id + " не найдено");
                return "faculty?faces-redirect=true";
            }
            
            // Сохраняем ID для отслеживания
            this.editId = id;
            logger.info("Установлен editId: " + this.editId);
            
            // Передаем ID как параметр в URL
            return "faculty-edit?faces-redirect=true&id=" + id;
        } catch (Exception e) {
            logger.severe("Ошибка при загрузке лица для редактирования: " + e.getMessage());
            e.printStackTrace();
            return "faculty?faces-redirect=true";
        }
    }
    
    // Загрузка лица из параметра URL
    public void loadPerson() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.valueOf(idParam);
                this.editId = id;
                logger.info("Загрузка лица из параметра URL, ID: " + id);
                
                person = personService.getPersonById(id);
                if (person == null) {
                    logger.warning("Лицо с ID " + id + " не найдено при загрузке из параметра");
                    person = new Person();
                } else {
                    logger.info("Лицо загружено из параметра URL: " + person.getShiffer());
                }
            } catch (NumberFormatException e) {
                logger.warning("Некорректный ID в параметре: " + idParam);
                person = new Person();
            }
        }
    }
    
    public String delete(Long id) {
        try {
            logger.info("Удаление лица с ID: " + id);
            personService.deletePerson(id);
            persons = personService.getAllPersons();
            return "faculty?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Ошибка при удалении лица: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public void search() {
        logger.info("Поиск лиц по шифру: " + searchTerm);
        persons = personService.searchPersonsByShiffer(searchTerm);
    }
    
    public void resetSearch() {
        logger.info("Сброс поиска лиц");
        searchTerm = null;
        persons = personService.getAllPersons();
    }
    
    public String prepareNew() {
        logger.info("Подготовка создания нового лица");
        person = new Person();
        editId = null;
        return "faculty-edit?faces-redirect=true";
    }
    
    private void ensureInitialized() {
        if (persons == null) {
            try {
                persons = personService.getAllPersons();
            } catch (Exception e) {
                logger.severe("Ошибка при получении списка лиц: " + e.getMessage());
                persons = new java.util.ArrayList<>();
            }
        }
        
        if (person == null) {
            person = new Person();
        }
    }
    
    // Геттеры и сеттеры
    public List<Person> getPersons() {
        ensureInitialized();
        return persons;
    }
    
    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
    
    public Person getPerson() {
        if (person == null) {
            person = new Person();
        }
        return person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
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
} 