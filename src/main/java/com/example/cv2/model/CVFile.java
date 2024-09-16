package com.example.cv2.model;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cvfiles")
public class CVFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cvFileId;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    private String filePath;
    private String fileName;
    private LocalDateTime createdAt;
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getId() {
        return cvFileId;
    }

    // Getters and setters
}

