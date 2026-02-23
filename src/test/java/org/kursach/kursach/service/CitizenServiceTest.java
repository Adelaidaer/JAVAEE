package org.kursach.kursach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.model.Person;
import org.kursach.kursach.repository.CitizenRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CitizenServiceTest {

    @Mock
    private CitizenRepository citizenRepository;

    @InjectMocks
    private CitizenService citizenService;

    private Person person;
    private Citizen citizen1;
    private Citizen citizen2;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные
        person = new Person("PER001", "123456789012", "Физическое лицо", LocalDate.of(2024, 1, 1));
        person.setId(1L);
        
        citizen1 = new Citizen();
        citizen1.setId(1L);
        citizen1.setPerson(person);
        citizen1.setFirstName("Иван");
        citizen1.setLastName("Иванов");
        citizen1.setPatronymic("Иванович");
        citizen1.setPersonalNumber("PN001");
        
        citizen2 = new Citizen();
        citizen2.setId(2L);
        citizen2.setPerson(person);
        citizen2.setFirstName("Петр");
        citizen2.setLastName("Петров");
        citizen2.setPatronymic("Петрович");
        citizen2.setPersonalNumber("PN002");
    }

    @Test
    void testGetAllCitizens() {
        // Подготовка
        List<Citizen> citizens = Arrays.asList(citizen1, citizen2);
        when(citizenRepository.findAll()).thenReturn(citizens);

        // Выполнение
        List<Citizen> result = citizenService.getAllCitizens();

        // Проверка
        assertEquals(2, result.size());
        assertEquals("Иван", result.get(0).getFirstName());
        assertEquals("Петр", result.get(1).getFirstName());
        verify(citizenRepository, times(1)).findAll();
    }

    @Test
    void testGetCitizenById() {
        // Подготовка
        when(citizenRepository.findById(1L)).thenReturn(citizen1);

        // Выполнение
        Citizen result = citizenService.getCitizenById(1L);

        // Проверка
        assertNotNull(result);
        assertEquals("Иван", result.getFirstName());
        assertEquals("Иванов", result.getLastName());
        assertEquals("PN001", result.getPersonalNumber());
        verify(citizenRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchCitizensByName() {
        // Подготовка
        String searchName = "Иван";
        when(citizenRepository.findByName(searchName)).thenReturn(Arrays.asList(citizen1));

        // Выполнение
        List<Citizen> result = citizenService.searchCitizensByName(searchName);

        // Проверка
        assertEquals(1, result.size());
        assertEquals("Иван", result.get(0).getFirstName());
        verify(citizenRepository, times(1)).findByName(searchName);
    }

    @Test
    void testGetCitizenByPersonalNumber() {
        // Подготовка
        when(citizenRepository.findByPersonalNumber("PN001")).thenReturn(citizen1);

        // Выполнение
        Citizen result = citizenService.getCitizenByPersonalNumber("PN001");

        // Проверка
        assertNotNull(result);
        assertEquals("Иван", result.getFirstName());
        assertEquals("PN001", result.getPersonalNumber());
        verify(citizenRepository, times(1)).findByPersonalNumber("PN001");
    }

    @Test
    void testGetCitizensByPerson() {
        // Подготовка
        List<Citizen> citizens = Arrays.asList(citizen1, citizen2);
        when(citizenRepository.findByPerson(person)).thenReturn(citizens);

        // Выполнение
        List<Citizen> result = citizenService.getCitizensByPerson(person);

        // Проверка
        assertEquals(2, result.size());
        assertEquals("Иван", result.get(0).getFirstName());
        assertEquals("Петр", result.get(1).getFirstName());
        verify(citizenRepository, times(1)).findByPerson(person);
    }

    @Test
    void testSaveCitizen() {
        // Выполнение
        citizenService.saveCitizen(citizen1);

        // Проверка
        verify(citizenRepository, times(1)).save(citizen1);
    }

    @Test
    void testDeleteCitizen() {
        // Выполнение
        citizenService.deleteCitizen(1L);

        // Проверка
        verify(citizenRepository, times(1)).delete(1L);
    }
} 