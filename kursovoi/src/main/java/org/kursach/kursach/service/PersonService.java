package org.kursach.kursach.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kursach.kursach.model.Person;
import org.kursach.kursach.repository.PersonRepository;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class PersonService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PersonService.class.getName());
    
    @Inject
    private PersonRepository personRepository;
    
    public List<Person> getAllPersons() {
        try {
            logger.info("PersonService: Запрос получения всех лиц");
            if (personRepository == null) {
                throw new IllegalStateException("PersonRepository не инжектирован");
            }
            List<Person> result = personRepository.findAll();
            logger.info("PersonService: Получено лиц: " + result.size());
            return result;
        } catch (Exception e) {
            logger.severe("PersonService: Ошибка при получении всех лиц: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Person> searchPersonsByShiffer(String shiffer) {
        try {
            logger.info("PersonService: Поиск лиц по шифру: " + shiffer);
            return personRepository.findByShiffer(shiffer);
        } catch (Exception e) {
            logger.severe("PersonService: Ошибка при поиске лиц: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public Person getPersonById(Long id) {
        try {
            logger.info("PersonService: Получение лица по ID: " + id);
            return personRepository.findById(id);
        } catch (Exception e) {
            logger.severe("PersonService: Ошибка при получении лица по ID: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public Person getPersonByInn(String inn) {
        try {
            logger.info("PersonService: Получение лица по ИНН: " + inn);
            return personRepository.findByInn(inn);
        } catch (Exception e) {
            logger.severe("PersonService: Ошибка при получении лица по ИНН: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Person> getPersonsByType(String type) {
        try {
            logger.info("PersonService: Получение лиц по типу: " + type);
            return personRepository.findByType(type);
        } catch (Exception e) {
            logger.severe("PersonService: Ошибка при получении лиц по типу: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void savePerson(Person person) {
        try {
            logger.info("PersonService: Сохранение лица: " + person);
            if (personRepository == null) {
                throw new IllegalStateException("PersonRepository не инжектирован");
            }
            personRepository.save(person);
            logger.info("PersonService: Лицо успешно сохранено");
        } catch (Exception e) {
            logger.severe("PersonService: Ошибка при сохранении лица: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public void deletePerson(Long id) {
        try {
            logger.info("PersonService: Удаление лица с ID: " + id);
            personRepository.delete(id);
            logger.info("PersonService: Лицо успешно удалено");
        } catch (Exception e) {
            logger.severe("PersonService: Ошибка при удалении лица: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 