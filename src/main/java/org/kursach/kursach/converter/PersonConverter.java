package org.kursach.kursach.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.Person;
import org.kursach.kursach.service.PersonService;

import java.util.logging.Logger;

@Named
@ApplicationScoped
@FacesConverter(value = "personConverter", managed = true)
public class PersonConverter implements Converter<Person> {
    
    private static final Logger logger = Logger.getLogger(PersonConverter.class.getName());
    
    @Inject
    private PersonService personService;

    @Override
    public Person getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            Long id = Long.valueOf(value);
            Person person = personService.getPersonById(id);
            logger.info("Converted string '" + value + "' to Person: " + (person != null ? person.getShiffer() : "null"));
            return person;
        } catch (NumberFormatException e) {
            logger.warning("Failed to parse Person ID from value: " + value);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Person person) {
        if (person == null) {
            logger.fine("Получен null объект в getAsString");
            return "";
        }
        
        try {
            // Защита от неправильного типа
            if (!(person instanceof Person)) {
                logger.warning("Ожидался объект Person, но получен: " + person.getClass().getName());
                return "";
            }
            
            if (person.getId() == null) {
                logger.warning("Person с null ID: " + person.getShiffer());
                return "";
            }
            
            String result = person.getId().toString();
            logger.info("Конвертировано лицо '" + person.getShiffer() + "' в строку: " + result);
            return result;
        } catch (Exception e) {
            logger.severe("Ошибка в getAsString: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
} 