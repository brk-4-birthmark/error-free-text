package com.example.errorfreetext;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 10000)
    private String originalText;

    @Column(length = 10000)
    private String correctedText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    private String errorMessage;

    private LocalDateTime createdAt;

    public Task() {
        this.createdAt = LocalDateTime.now();
        this.status = TaskStatus.NEW;
    }

    @Column(nullable = false)
    private String language;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }

    public String getCorrectedText() { return correctedText; }
    public void setCorrectedText(String correctedText) { this.correctedText = correctedText; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

}