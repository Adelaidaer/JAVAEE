package org.kursach.kursach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.model.Person;
import org.kursach.kursach.repository.CitizenRepository;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CitizenService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CitizenService.class.getName());
    
    @Inject
    private CitizenRepository citizenRepository;
    
    public List<Citizen> getAllCitizens() {
        try {
            logger.info("CitizenService: Запрос получения всех граждан");
            if (citizenRepository == null) {
                throw new IllegalStateException("CitizenRepository не инжектирован");
            }
            List<Citizen> result = citizenRepository.findAll();
            logger.info("CitizenService: Получено граждан: " + result.size());
            return result;
        } catch (Exception e) {
            logger.severe("CitizenService: Ошибка при получении всех граждан: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Citizen> searchCitizensByName(String name) {
        try {
            logger.info("CitizenService: Поиск граждан по имени: " + name);
            return citizenRepository.findByName(name);
        } catch (Exception e) {
            logger.severe("CitizenService: Ошибка при поиске граждан: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public Citizen getCitizenById(Long id) {
        try {
            logger.info("CitizenService: Получение гражданина по ID: " + id);
            return citizenRepository.findById(id);
        } catch (Exception e) {
            logger.severe("CitizenService: Ошибка при получении гражданина по ID: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public Citizen getCitizenByPersonalNumber(String personalNumber) {
        try {
            logger.info("CitizenService: Получение гражданина по персональному номеру: " + personalNumber);
            return citizenRepository.findByPersonalNumber(personalNumber);
        } catch (Exception e) {
            logger.severe("CitizenService: Ошибка при получении гражданина по персональному номеру: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Citizen> getCitizensByPerson(Person person) {
        try {
            logger.info("CitizenService: Получение граждан по лицу: " + person);
            return citizenRepository.findByPerson(person);
        } catch (Exception e) {
            logger.severe("CitizenService: Ошибка при получении граждан по лицу: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void saveCitizen(Citizen citizen) {
        try {
            logger.info("CitizenService: Сохранение гражданина: " + citizen);
            if (citizenRepository == null) {
                throw new IllegalStateException("CitizenRepository не инжектирован");
            }
            citizenRepository.save(citizen);
            logger.info("CitizenService: Гражданин успешно сохранен");
        } catch (Exception e) {
            logger.severe("CitizenService: Ошибка при сохранении гражданина: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void deleteCitizen(Long id) {
        try {
            logger.info("CitizenService: Удаление гражданина с ID: " + id);
            citizenRepository.delete(id);
            logger.info("CitizenService: Гражданин успешно удален");
        } catch (Exception e) {
            logger.severe("CitizenService: Ошибка при удалении гражданина: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 