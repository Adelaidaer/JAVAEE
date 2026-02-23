package org.kursach.kursach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.kursach.kursach.model.Person;
import org.kursach.kursach.repository.PersonRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person person1;
    private Person person2;

    @BeforeEach
    void setUp() {
        person1 = new Person("PER001", "123456789012", "Физическое лицо", LocalDate.of(2024, 1, 1));
        person1.setId(1L);
        
        person2 = new Person("PER002", "234567890123", "Физическое лицо", LocalDate.of(2024, 1, 2));
        person2.setId(2L);
    }

    @Test
    void testGetAllPersons() {
        // Подготовка
        List<Person> persons = Arrays.asList(person1, person2);
        when(personRepository.findAll()).thenReturn(persons);

        // Выполнение
        List<Person> result = personService.getAllPersons();

        // Проверка
        assertEquals(2, result.size());
        assertEquals("PER001", result.get(0).getShiffer());
        assertEquals("PER002", result.get(1).getShiffer());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testSearchPersonsByShiffer() {
        // Подготовка
        String searchShiffer = "PER001";
        when(personRepository.findByShiffer(searchShiffer)).thenReturn(Arrays.asList(person1));

        // Выполнение
        List<Person> result = personService.searchPersonsByShiffer(searchShiffer);

        // Проверка
        assertEquals(1, result.size());
        assertEquals("PER001", result.get(0).getShiffer());
        verify(personRepository, times(1)).findByShiffer(searchShiffer);
    }

    @Test
    void testGetPersonById() {
        // Подготовка
        when(personRepository.findById(1L)).thenReturn(person1);

        // Выполнение
        Person result = personService.getPersonById(1L);

        // Проверка
        assertNotNull(result);
        assertEquals("PER001", result.getShiffer());
        assertEquals("123456789012", result.getInn());
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPersonByInn() {
        // Подготовка
        when(personRepository.findByInn("123456789012")).thenReturn(person1);

        // Выполнение
        Person result = personService.getPersonByInn("123456789012");

        // Проверка
        assertNotNull(result);
        assertEquals("PER001", result.getShiffer());
        assertEquals("123456789012", result.getInn());
        verify(personRepository, times(1)).findByInn("123456789012");
    }

    @Test
    void testGetPersonsByType() {
        // Подготовка
        String type = "Физическое лицо";
        when(personRepository.findByType(type)).thenReturn(Arrays.asList(person1, person2));

        // Выполнение
        List<Person> result = personService.getPersonsByType(type);

        // Проверка
        assertEquals(2, result.size());
        assertEquals("Физическое лицо", result.get(0).getType());
        assertEquals("Физическое лицо", result.get(1).getType());
        verify(personRepository, times(1)).findByType(type);
    }

    @Test
    void testSavePerson() {
        // Выполнение
        personService.savePerson(person1);

        // Проверка
        verify(personRepository, times(1)).save(person1);
    }

    @Test
    void testDeletePerson() {
        // Выполнение
        personService.deletePerson(1L);

        // Проверка
        verify(personRepository, times(1)).delete(1L);
    }
} 