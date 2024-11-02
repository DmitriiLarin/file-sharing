package com.example.File.sharing.repositories;

import com.example.File.sharing.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository  extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByName(String name);

}
