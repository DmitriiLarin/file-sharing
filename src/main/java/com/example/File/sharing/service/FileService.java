package com.example.File.sharing.service;

import com.example.File.sharing.entity.Tag;
import com.example.File.sharing.repository.FileRepository;
import com.example.File.sharing.exception.FileNotFoundException;
import com.example.File.sharing.entity.File;
import com.example.File.sharing.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final RestTemplate restTemplate;

    private final FileRepository fileRepository;
    private final TagRepository tagRepository;

    public List<File> findAll() {
        return fileRepository.findAll();
    }

    public void attachTagsToFile(int fileId, List<Integer> tagIds) throws FileNotFoundException {
        File file = fileRepository.findById(fileId)
                .orElseThrow(FileNotFoundException::new);
        List<Tag> tags = tagRepository.findAllById(tagIds);

        tags.forEach(file::addTag);
        fileRepository.save(file);
    }

    public List<File> findByTagId(int tagId) {
        return fileRepository.findByTags_Id(tagId);
    }


    public File save(File file) {
        enrichFile(file);
        return fileRepository.save(file);
    }

    public Optional<File> findById(int id){
        return fileRepository.findById(id);
    }

    public void deleteById(int id){
        fileRepository.deleteById(id);
    }

    private void enrichFile(File file) {
        file.setCreatedAt(LocalDateTime.now());
    }

    public void deleteExpiredFiles() {
        List<File> files = new ArrayList<>();
        for (File file : findAll()) {
            LocalDateTime expirationDate = LocalDateTime.now().minusHours(Long.parseLong(file.getLifeTime().toString()));
            if (file.getCreatedAt().isBefore(expirationDate)) {
                files.add(file);
            }
        }
        for (File file : files) {
            String deleteUrl = "http://localhost:8080/api/v1/files/" + file.getId() + "/delete";
            try {
                restTemplate.exchange(deleteUrl, HttpMethod.DELETE, null, Void.class);
            } catch (Exception e) {
                log.error("Error deleting file with ID " + file.getId() + ": " + e.getMessage());
            }
        }
    }
}
