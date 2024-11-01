package com.example.File.sharing.services;

import com.example.File.sharing.models.Tag;
import com.example.File.sharing.repositories.FileRepository;
import com.example.File.sharing.exception.FileNotFoundException;
import com.example.File.sharing.models.File;
import com.example.File.sharing.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Source;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FileService {
    private final long FILE_EXPIRATION_DAYS = 14;

    private final RestTemplate restTemplate;

    private final FileRepository fileRepository;
    private final TagRepository tagRepository;

    @Autowired
    public FileService(FileRepository fileRepository, TagRepository tagRepository,
                       RestTemplate restTemplate) {
        this.fileRepository = fileRepository;
        this.tagRepository = tagRepository;
        this.restTemplate = restTemplate;
    }

    public List<File> findAll() {
        return fileRepository.findAll();
    }

    @Transactional
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
        System.out.println("Deleting expired files");
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(FILE_EXPIRATION_DAYS);
        List<File> expiredFiles = fileRepository.findAllByCreatedAtBefore(expirationDate);

        for (File file : expiredFiles) {
            String deleteUrl = "http://localhost:8080/api/v1/files/" + file.getId() + "/delete";
            try {
                System.out.println(deleteUrl);
                ResponseEntity<?> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, null, Void.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("File with ID " + file.getId() + " deleted successfully.");
                } else {
                    System.out.println("Failed to delete file with ID " + file.getId());
                }
            } catch (Exception e) {
                System.err.println("Error deleting file with ID " + file.getId() + ": " + e.getMessage());
            }
        }
    }
}
