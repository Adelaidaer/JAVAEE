package org.kursach.kursach.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "document")
public class Document implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;
    
    @Column(name = "document_name", length = 200)
    private String documentName;
    
    @Column(name = "series", length = 50)
    private String series;
    
    @Column(name = "issuing_authority", length = 500)
    private String issuingAuthority;
    
    @Column(name = "issue_date")
    private LocalDate issueDate;
    
    // Конструкторы
    public Document() {
    }
    
    public Document(Citizen citizen, String documentName, String series, String issuingAuthority, LocalDate issueDate) {
        this.citizen = citizen;
        this.documentName = documentName;
        this.series = series;
        this.issuingAuthority = issuingAuthority;
        this.issueDate = issueDate;
    }
    
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Document document = (Document) o;
        return id != null ? id.equals(document.id) : document.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", citizen=" + (citizen != null ? citizen.getPersonalNumber() : "null") +
                ", documentName='" + documentName + '\'' +
                ", series='" + series + '\'' +
                ", issuingAuthority='" + issuingAuthority + '\'' +
                ", issueDate=" + issueDate +
                '}';
    }
} 