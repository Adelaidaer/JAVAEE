package org.kursach.kursach.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "migration_history")
public class MigrationHistory implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "migration_version", length = 50)
    private String migrationVersion;
    
    @Column(name = "migration_name", length = 200)
    private String migrationName;
    
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
    
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;
    
    @Column(name = "success")
    private Boolean success;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    // Конструкторы
    public MigrationHistory() {
    }
    
    public MigrationHistory(String migrationVersion, String migrationName, LocalDateTime executedAt, 
                           Integer executionTimeMs, Boolean success, String errorMessage) {
        this.migrationVersion = migrationVersion;
        this.migrationName = migrationName;
        this.executedAt = executedAt;
        this.executionTimeMs = executionTimeMs;
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMigrationVersion() {
        return migrationVersion;
    }

    public void setMigrationVersion(String migrationVersion) {
        this.migrationVersion = migrationVersion;
    }

    public String getMigrationName() {
        return migrationName;
    }

    public void setMigrationName(String migrationName) {
        this.migrationName = migrationName;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        MigrationHistory that = (MigrationHistory) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MigrationHistory{" +
                "id=" + id +
                ", migrationVersion='" + migrationVersion + '\'' +
                ", migrationName='" + migrationName + '\'' +
                ", executedAt=" + executedAt +
                ", executionTimeMs=" + executionTimeMs +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
} 