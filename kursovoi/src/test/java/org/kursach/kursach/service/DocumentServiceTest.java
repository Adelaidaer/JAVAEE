package org.kursach.kursach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.kursach.kursach.model.Citizen;
import org.kursach.kursach.model.Document;
import org.kursach.kursach.model.Person;
import org.kursach.kursach.repository.DocumentRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    private Person person;
    private Citizen citizen;
    private Document document1;
    private Document document2;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные
        person = new Person("PER001", "123456789012", "Физическое лицо", LocalDate.of(2024, 1, 1));
        person.setId(1L);
        
        citizen = new Citizen();
        citizen.setId(1L);
        citizen.setPerson(person);
        citizen.setFirstName("Иван");
        citizen.setLastName("Иванов");
        citizen.setPatronymic("Иванович");
        citizen.setPersonalNumber("PN001");
        
        document1 = new Document();
        document1.setId(1L);
        document1.setCitizen(citizen);
        document1.setDocumentName("Паспорт");
        document1.setSeries("1234");
        document1.setIssueDate(LocalDate.of(2020, 1, 1));
        document1.setIssuingAuthority("УФМС России");
        
        document2 = new Document();
        document2.setId(2L);
        document2.setCitizen(citizen);
        document2.setDocumentName("Водительские права");
        document2.setSeries("AB");
        document2.setIssueDate(LocalDate.of(2021, 1, 1));
        document2.setIssuingAuthority("ГИБДД");
    }

    @Test
    void testGetAllDocuments() {
        // Подготовка
        List<Document> documents = Arrays.asList(document1, document2);
        when(documentRepository.findAll()).thenReturn(documents);

        // Выполнение
        List<Document> result = documentService.getAllDocuments();

        // Проверка
        assertEquals(2, result.size());
        assertEquals("Паспорт", result.get(0).getDocumentName());
        assertEquals("Водительские права", result.get(1).getDocumentName());
        verify(documentRepository, times(1)).findAll();
    }

    @Test
    void testGetDocumentById() {
        // Подготовка
        when(documentRepository.findById(1L)).thenReturn(document1);

        // Выполнение
        Document result = documentService.getDocumentById(1L);

        // Проверка
        assertNotNull(result);
        assertEquals("Паспорт", result.getDocumentName());
        assertEquals("1234", result.getSeries());
        assertEquals("УФМС России", result.getIssuingAuthority());
        verify(documentRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchDocumentsByName() {
        // Подготовка
        String searchName = "Паспорт";
        when(documentRepository.findByDocumentName(searchName)).thenReturn(Arrays.asList(document1));

        // Выполнение
        List<Document> result = documentService.searchDocumentsByName(searchName);

        // Проверка
        assertEquals(1, result.size());
        assertEquals("Паспорт", result.get(0).getDocumentName());
        verify(documentRepository, times(1)).findByDocumentName(searchName);
    }

    @Test
    void testGetDocumentBySeries() {
        // Подготовка
        String series = "1234";
        when(documentRepository.findBySeries(series)).thenReturn(Arrays.asList(document1));

        // Выполнение
        List<Document> result = documentService.getDocumentsBySeries(series);

        // Проверка
        assertEquals(1, result.size());
        assertEquals("1234", result.get(0).getSeries());
        verify(documentRepository, times(1)).findBySeries(series);
    }

    @Test
    void testGetDocumentsByCitizen() {
        // Подготовка
        List<Document> documents = Arrays.asList(document1, document2);
        when(documentRepository.findByCitizen(citizen)).thenReturn(documents);

        // Выполнение
        List<Document> result = documentService.getDocumentsByCitizen(citizen);

        // Проверка
        assertEquals(2, result.size());
        assertEquals("Паспорт", result.get(0).getDocumentName());
        assertEquals("Водительские права", result.get(1).getDocumentName());
        verify(documentRepository, times(1)).findByCitizen(citizen);
    }

    @Test
    void testSaveDocument() {
        // Выполнение
        documentService.saveDocument(document1);

        // Проверка
        verify(documentRepository, times(1)).save(document1);
    }

    @Test
    void testDeleteDocument() {
        // Выполнение
        documentService.deleteDocument(1L);

        // Проверка
        verify(documentRepository, times(1)).delete(1L);
    }
} 