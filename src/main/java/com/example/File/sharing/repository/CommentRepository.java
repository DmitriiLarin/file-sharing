package com.example.File.sharing.repository;

import com.example.File.sharing.entity.Comment;
import com.example.File.sharing.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByFile(File file);
}
