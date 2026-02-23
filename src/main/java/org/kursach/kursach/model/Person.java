package org.kursach.kursach.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "person")
public class Person implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "shiffer", nullable = false, unique = true, length = 50)
    private String shiffer;
    
    @Column(name = "inn", nullable = false, unique = true, length = 12)
    private String inn;
    
    @Column(name = "type", length = 100)
    private String type;
    
    @Column(name = "date_created", nullable = false)
    private LocalDate dateCreated;
    
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Citizen> citizens;
    
    // Конструкторы
    public Person() {
    }
    
    public Person(String shiffer, String inn, String type, LocalDate dateCreated) {
        this.shiffer = shiffer;
        this.inn = inn;
        this.type = type;
        this.dateCreated = dateCreated;
    }
    
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShiffer() {
        return shiffer;
    }

    public void setShiffer(String shiffer) {
        this.shiffer = shiffer;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<Citizen> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<Citizen> citizens) {
        this.citizens = citizens;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Person person = (Person) o;
        return id != null ? id.equals(person.id) : person.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", shiffer='" + shiffer + '\'' +
                ", inn='" + inn + '\'' +
                ", type='" + type + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }
} 