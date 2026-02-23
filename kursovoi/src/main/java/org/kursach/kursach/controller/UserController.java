package org.kursach.kursach.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.User;
import org.kursach.kursach.service.UserService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named
@ViewScoped
public class UserController implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    
    @Inject
    private UserService userService;
    
    private List<User> users;
    private User user;
    private Long editId;
    private String searchTerm;
    private String password; // Отдельное поле для пароля
    
    @PostConstruct
    public void init() {
        try {
            logger.info("Начало инициализации UserController");
            
            // Проверяем, что сервисы инжектированы
            if (userService == null) {
                throw new IllegalStateException("UserService не инжектирован");
            }
            
            logger.info("Сервисы успешно инжектированы");
            
            // Загружаем данные с обработкой ошибок
            try {
                users = userService.getAllUsers();
                logger.info("Загружено пользователей: " + (users != null ? users.size() : 0));
            } catch (Exception e) {
                logger.severe("Ошибка загрузки пользователей: " + e.getMessage());
                users = new java.util.ArrayList<>();
            }
            
            // Создаем новый User только если текущий объект null
            if (user == null) {
                user = new User();
                logger.info("Создан новый объект User");
            } else {
                logger.info("Использован существующий объект User с ID: " + user.getId());
            }
            
            // Пробуем загрузить из параметра
            try {
                loadUser();
            } catch (Exception e) {
                logger.warning("Ошибка загрузки пользователя из параметров: " + e.getMessage());
            }
            
            logger.info("UserController успешно инициализирован");
            
        } catch (Exception e) {
            logger.severe("Критическая ошибка при инициализации UserController: " + e.getMessage());
            e.printStackTrace();
            
            // Инициализируем минимально необходимые объекты
            if (user == null) {
                user = new User();
            }
            if (users == null) {
                users = new java.util.ArrayList<>();
            }
            
            throw new RuntimeException("Не удалось инициализировать UserController", e);
        }
    }
    
    public String save() {
        try {
            // Проверяем, есть ли ID для редактирования
            if (editId != null && user.getId() == null) {
                user.setId(editId);
                logger.info("Используется ID из editId: " + editId);
            }
            
            boolean isNew = (user.getId() == null);
            logger.info((isNew ? "Создание нового" : "Обновление существующего") + " пользователя: " + user.getUsername());
            
            if (!isNew) {
                logger.info("ID редактируемого пользователя: " + user.getId());
            }
            
            // Обрабатываем пароль, если он был введен
            if (password != null && !password.trim().isEmpty()) {
                // В реальном приложении здесь должно быть хеширование пароля
                user.setPasswordHash(password.trim());
                logger.info("Пароль обновлен для пользователя: " + user.getUsername());
            }
            
            // Добавляем логирование ID перед сохранением
            logger.info("Перед сохранением пользователя - ID: " + user.getId() + 
                         ", Имя пользователя: " + user.getUsername() + 
                         ", Email: " + user.getEmail());
            
            userService.saveUser(user);
            
            // Логируем ID после сохранения
            logger.info("После сохранения - статус операции: успешно");
            
            // Сбрасываем editId и password
            editId = null;
            password = null;
            
            // Обновляем список пользователей
            users = userService.getAllUsers();
            // Создаем новый объект для формы
            user = new User();
            
            logger.info("Пользователь " + (isNew ? "создан" : "обновлен") + " успешно");
            return "user?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Ошибка при сохранении пользователя: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public String edit(Long id) {
        try {
            logger.info("Редактирование пользователя с ID: " + id);
            user = userService.getUserById(id);
            
            if (user == null) {
                logger.warning("Пользователь с ID " + id + " не найден");
                return "user?faces-redirect=true";
            }
            
            // Сохраняем ID для отслеживания
            this.editId = id;
            logger.info("Установлен editId: " + this.editId);
            
            // Сбрасываем пароль при редактировании (пользователь должен ввести новый, если хочет изменить)
            password = null;
            
            // Передаем ID как параметр в URL
            return "user-edit?faces-redirect=true&id=" + id;
        } catch (Exception e) {
            logger.severe("Ошибка при загрузке пользователя для редактирования: " + e.getMessage());
            e.printStackTrace();
            return "user?faces-redirect=true";
        }
    }
    
    // Загрузка пользователя из параметра URL
    public void loadUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long id = Long.valueOf(idParam);
                this.editId = id;
                logger.info("Загрузка пользователя из параметра URL, ID: " + id);
                
                user = userService.getUserById(id);
                if (user == null) {
                    logger.warning("Пользователь с ID " + id + " не найден при загрузке из параметра");
                    user = new User();
                } else {
                    logger.info("Пользователь загружен из параметра URL: " + user.getUsername());
                }
            } catch (NumberFormatException e) {
                logger.warning("Некорректный ID в параметре: " + idParam);
                user = new User();
            }
        }
    }
    
    public String delete(Long id) {
        try {
            logger.info("Удаление пользователя с ID: " + id);
            userService.deleteUser(id);
            users = userService.getAllUsers();
            return "user?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Ошибка при удалении пользователя: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public void search() {
        logger.info("Поиск пользователей по имени: " + searchTerm);
        // Для пользователей можно использовать фильтрацию по имени пользователя
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            users = userService.getAllUsers();
        } else {
            users = userService.getAllUsers().stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                (u.getFullName() != null && u.getFullName().toLowerCase().contains(searchTerm.toLowerCase())))
                    .toList();
        }
    }
    
    public void resetSearch() {
        logger.info("Сброс поиска пользователей");
        searchTerm = null;
        users = userService.getAllUsers();
    }
    
    public String prepareNew() {
        logger.info("Подготовка создания нового пользователя");
        user = new User();
        editId = null;
        password = null;
        return "user-edit?faces-redirect=true";
    }
    
    private void ensureInitialized() {
        if (users == null) {
            try {
                users = userService.getAllUsers();
            } catch (Exception e) {
                logger.severe("Ошибка при получении списка пользователей: " + e.getMessage());
                users = new java.util.ArrayList<>();
            }
        }
        
        if (user == null) {
            user = new User();
        }
    }
    
    // Геттеры и сеттеры
    public List<User> getUsers() {
        ensureInitialized();
        return users;
    }
    
    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 