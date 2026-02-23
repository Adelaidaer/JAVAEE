package org.kursach.kursach.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.model.Person;
import org.kursach.kursach.service.CitizenService;
import org.kursach.kursach.service.PersonService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CitizenController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CitizenController.class.getName());
    
    @Inject
    private CitizenService citizenService;
    
    @Inject
    private PersonService personService;
    
    private List<Citizen> citizens;
    private Citizen citizen;
    private Long editId;
    private String searchTerm;
    private List<Person> allPersons;
    private Long selectedPersonId;
    
    @PostConstruct
    public void init() {
        try {
            logger.info("Начало инициализации CitizenController");
            
            // Проверяем, что сервисы инжектированы
            if (citizenService == null) {
                throw new IllegalStateException("CitizenService не инжектирован");
            }
            
            if (personService == null) {
                throw new IllegalStateException("PersonService не инжектирован");
            }
            
            logger.info("Сервисы успешно инжектированы");
            
            // Загружаем данные с обработкой ошибок
            try {
                citizens = citizenService.getAllCitizens();
                allPersons = personService.getAllPersons();
                logger.info("Загружено граждан: " + (citizens != null ? citizens.size() : 0));
                logger.info("Загружено лиц: " + (allPersons != null ? allPersons.size() : 0));
            } catch (Exception e) {
                logger.severe("Ошибка загрузки данных: " + e.getMessage());
                citizens = new java.util.ArrayList<>();
                allPersons = new java.util.ArrayList<>();
            }
            
            // Создаем новый Citizen только если текущий объект null
            if (citizen == null) {
                citizen = new Citizen();
                logger.info("Создан новый объект Citizen");
            } else {
                logger.info("Использован существующий объект Citizen с ID: " + citizen.getId());
            }
            
            // Пробуем загрузить из параметра
            try {
                loadCitizen();
            } catch (Exception e) {
                logger.warning("Ошибка загрузки гражданина из параметров: " + e.getMessage());
            }
            
            logger.info("CitizenController успешно инициализирован");
            
        } catch (Exception e) {
            logger.severe("Критическая ошибка при инициализации CitizenController: " + e.getMessage());
            e.printStackTrace();
            
            // Инициализируем минимально необходимые объекты
            if (citizen == null) {
                citizen = new Citizen();
            }
            if (citizens == null) {
                citizens = new java.util.ArrayList<>();
            }
            if (allPersons == null) {
                allPersons = new java.util.ArrayList<>();
            }
            
            throw new RuntimeException("Не удалось инициализировать CitizenController", e);
        }
    }
    
    public String save() {
        try {
            // Проверяем, есть ли ID для редактирования
            if (editId != null && citizen.getId() == null) {
                citizen.setId(editId);
                logger.info("Используется ID из editId: " + editId);
            }
            
            boolean isNew = (citizen.getId() == null);
            logger.info((isNew ? "Создание нового" : "Обновление существующего") + " гражданина: " + citizen.getPersonalNumber());
            
            if (!isNew) {
                logger.info("ID редактируемого гражданина: " + citizen.getId());
            }
            
            // Устанавливаем связанную персону по selectedPersonId
            if (selectedPersonId != null) {
                Person selectedPerson = personService.getPersonById(selectedPersonId);
                if (selectedPerson != null) {
                    citizen.setPerson(selectedPerson);
                    logger.info("Установлена связанная персона с ID: " + selectedPersonId);
                } else {
                    logger.warning("Персона с ID " + selectedPersonId + " не найдена");
                }
            } else {
                citizen.setPerson(null);
                logger.info("Персона не выбрана (selectedPersonId is null)");
            }
            
            // Добавляем логирование ID перед сохранением
            logger.info("Перед сохранением гражданина - ID: " + citizen.getId() + 
                         ", Персональный номер: " + citizen.getPersonalNumber() + 
                         ", Имя: " + citizen.getFirstName() +
                         ", Связанная персона ID: " + (citizen.getPerson() != null ? citizen.getPerson().getId() : "null"));
            
            citizenService.saveCitizen(citizen);
            
            // Логируем ID после сохранения
            logger.info("После сохранения - статус операции: успешно");
            
            // Сбрасываем editId и selectedPersonId
            editId = null;
            selectedPersonId = null;
            
            // Обновляем список граждан
            citizens = citizenService.getAllCitizens();
            // Создаем новый объект для формы
            citizen = new Citizen();
            
            logger.info("Гражданин " + (isNew ? "создан" : "обновлен") + " успешно");
            return "citizen?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Ошибка при сохранении гражданина: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public String edit(Long id) {
        try {
            logger.info("Редактирование гражданина с ID: " + id);
            citizen = citizenService.getCitizenById(id);
            
            if (citizen == null) {
                logger.warning("Гражданин с ID " + id + " не найден");
                return "citizen?faces-redirect=true";
            }
            
            // Сохраняем ID для отслеживания
            this.editId = id;
            logger.info("Установлен editId: " + this.editId);
            
            // Устанавливаем selectedPersonId из связанной персоны
            if (citizen.getPerson() != null) {
                selectedPersonId = citizen.getPerson().getId();
                logger.info("Установлен selectedPersonId: " + selectedPersonId);
            } else {
                selectedPersonId = null;
            }
            
            // Передаем ID как параметр в URL
            return "citizen-edit?faces-redirect=true&id=" + id;
        } catch (Exception e) {
            logger.severe("Ошибка при загрузке гражданина для редактирования: " + e.getMessage());
            e.printStackTrace();
            return "citizen?faces-redirect=true";
        }
    }
    
    // Загрузка гражданина из параметра URL
    public void loadCitizen() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.valueOf(idParam);
                this.editId = id;
                logger.info("Загрузка гражданина из параметра URL, ID: " + id);
                
                citizen = citizenService.getCitizenById(id);
                if (citizen == null) {
                    logger.warning("Гражданин с ID " + id + " не найден при загрузке из параметра");
                    citizen = new Citizen();
                    selectedPersonId = null;
                } else {
                    logger.info("Гражданин загружен из параметра URL: " + citizen.getPersonalNumber());
                    // Устанавливаем selectedPersonId из связанной персоны
                    if (citizen.getPerson() != null) {
                        selectedPersonId = citizen.getPerson().getId();
                        logger.info("Установлен selectedPersonId: " + selectedPersonId);
                    } else {
                        selectedPersonId = null;
                    }
                }
            } catch (NumberFormatException e) {
                logger.warning("Некорректный ID в параметре: " + idParam);
                citizen = new Citizen();
            }
        }
    }
    
    public String delete(Long id) {
        try {
            logger.info("Удаление гражданина с ID: " + id);
            citizenService.deleteCitizen(id);
            citizens = citizenService.getAllCitizens();
            return "citizen?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Ошибка при удалении гражданина: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public void search() {
        logger.info("Поиск граждан по имени: " + searchTerm);
        citizens = citizenService.searchCitizensByName(searchTerm);
    }
    
    public void resetSearch() {
        logger.info("Сброс поиска граждан");
        searchTerm = null;
        citizens = citizenService.getAllCitizens();
    }
    
    public String prepareNew() {
        logger.info("Подготовка создания нового гражданина");
        citizen = new Citizen();
        editId = null;
        selectedPersonId = null;
        return "citizen-edit?faces-redirect=true";
    }
    
    private void ensureInitialized() {
        if (citizens == null) {
            try {
                citizens = citizenService.getAllCitizens();
            } catch (Exception e) {
                logger.severe("Ошибка при получении списка граждан: " + e.getMessage());
                citizens = new java.util.ArrayList<>();
            }
        }
        
        if (citizen == null) {
            citizen = new Citizen();
        }
        
        if (allPersons == null) {
            try {
                allPersons = personService.getAllPersons();
            } catch (Exception e) {
                logger.severe("Ошибка при получении списка лиц: " + e.getMessage());
                allPersons = new java.util.ArrayList<>();
            }
        }
    }
    
    // Геттеры и сеттеры
    public List<Citizen> getCitizens() {
        ensureInitialized();
        return citizens;
    }
    
    public void setCitizens(List<Citizen> citizens) {
        this.citizens = citizens;
    }
    
    public Citizen getCitizen() {
        if (citizen == null) {
            citizen = new Citizen();
        }
        return citizen;
    }
    
    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
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
    
    public List<Person> getAllPersons() {
        ensureInitialized();
        return allPersons;
    }
    
    public void setAllPersons(List<Person> allPersons) {
        this.allPersons = allPersons;
    }

    public Long getSelectedPersonId() {
        return selectedPersonId;
    }

    public void setSelectedPersonId(Long selectedPersonId) {
        this.selectedPersonId = selectedPersonId;
    }
} 