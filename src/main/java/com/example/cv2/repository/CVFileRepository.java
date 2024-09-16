package com.example.cv2.repository;

import com.example.cv2.model.CVFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CVFileRepository extends JpaRepository<CVFile, Integer> {
    // Możesz dodać dodatkowe metody wyszukiwania, jeśli to konieczne
    List<CVFile> findByUserId(int userId);
}