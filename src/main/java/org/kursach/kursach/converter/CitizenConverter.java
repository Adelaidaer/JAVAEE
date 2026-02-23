package org.kursach.kursach.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.service.CitizenService;

import java.util.logging.Logger;

@Named
@ApplicationScoped
@FacesConverter(value = "citizenConverter", managed = true)
public class CitizenConverter implements Converter<Citizen> {
    
    private static final Logger logger = Logger.getLogger(CitizenConverter.class.getName());
    
    @Inject
    private CitizenService citizenService;

    @Override
    public Citizen getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            Long id = Long.valueOf(value);
            Citizen citizen = citizenService.getCitizenById(id);
            logger.info("Converted string '" + value + "' to Citizen: " + (citizen != null ? citizen.getPersonalNumber() : "null"));
            return citizen;
        } catch (NumberFormatException e) {
            logger.warning("Failed to parse Citizen ID from value: " + value);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Citizen citizen) {
        if (citizen == null) {
            logger.fine("Получен null объект в getAsString");
            return "";
        }
        
        try {
            // Защита от неправильного типа
            if (!(citizen instanceof Citizen)) {
                logger.warning("Ожидался объект Citizen, но получен: " + citizen.getClass().getName());
                return "";
            }
            
            if (citizen.getId() == null) {
                logger.warning("Citizen с null ID: " + citizen.getPersonalNumber());
                return "";
            }
            
            String result = citizen.getId().toString();
            logger.info("Конвертирован гражданин '" + citizen.getPersonalNumber() + "' в строку: " + result);
            return result;
        } catch (Exception e) {
            logger.severe("Ошибка в getAsString: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
} 