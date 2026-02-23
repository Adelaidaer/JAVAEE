package org.kursach.kursach.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.User;
import org.kursach.kursach.service.UserService;

import java.util.logging.Logger;

@Named
@ApplicationScoped
@FacesConverter(value = "userConverter", managed = true)
public class UserConverter implements Converter<User> {
    
    private static final Logger logger = Logger.getLogger(UserConverter.class.getName());
    
    @Inject
    private UserService userService;

    @Override
    public User getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            Long id = Long.valueOf(value);
            User user = userService.getUserById(id);
            logger.info("Converted string '" + value + "' to User: " + (user != null ? user.getUsername() : "null"));
            return user;
        } catch (NumberFormatException e) {
            logger.warning("Failed to parse User ID from value: " + value);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, User user) {
        if (user == null) {
            logger.fine("Получен null объект в getAsString");
            return "";
        }
        
        try {
            // Защита от неправильного типа
            if (!(user instanceof User)) {
                logger.warning("Ожидался объект User, но получен: " + user.getClass().getName());
                return "";
            }
            
            if (user.getId() == null) {
                logger.warning("User с null ID: " + user.getUsername());
                return "";
            }
            
            String result = user.getId().toString();
            logger.info("Конвертирован пользователь '" + user.getUsername() + "' в строку: " + result);
            return result;
        } catch (Exception e) {
            logger.severe("Ошибка в getAsString: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
} 