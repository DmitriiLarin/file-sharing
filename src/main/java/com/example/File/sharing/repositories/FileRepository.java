package com.example.File.sharing.repositories;

import com.example.File.sharing.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> findByTags_Id(int tagId);
    List<File> findAllByCreatedAtBefore(LocalDateTime createdAt);
}
