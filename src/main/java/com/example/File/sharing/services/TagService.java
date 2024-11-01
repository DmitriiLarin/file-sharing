package com.example.File.sharing.services;


import com.example.File.sharing.models.Comment;
import com.example.File.sharing.models.Tag;
import com.example.File.sharing.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void deleteTagById(Integer tagId) {
        tagRepository.deleteById(tagId);
    }

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Transactional
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }
}
