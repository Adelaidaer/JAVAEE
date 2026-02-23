package org.kursach.kursach.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "citizen")
public class Citizen implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
    
    @Column(name = "first_name", length = 100)
    private String firstName;
    
    @Column(name = "last_name", length = 100)
    private String lastName;
    
    @Column(name = "patronymic", length = 100)
    private String patronymic;
    
    @Column(name = "personal_number", unique = true, length = 50)
    private String personalNumber;
    
    @OneToMany(mappedBy = "citizen", cascade = CascadeType.ALL)
    private List<Document> documents;
    
    // Конструкторы
    public Citizen() {
    }
    
    public Citizen(Person person, String firstName, String lastName, String patronymic, String personalNumber) {
        this.person = person;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.personalNumber = personalNumber;
    }
    
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Citizen citizen = (Citizen) o;
        return id != null ? id.equals(citizen.id) : citizen.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Citizen{" +
                "id=" + id +
                ", person=" + (person != null ? person.getShiffer() : "null") +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", personalNumber='" + personalNumber + '\'' +
                '}';
    }
} 