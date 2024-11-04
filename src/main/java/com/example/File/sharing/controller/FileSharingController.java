package com.example.File.sharing.controller;

import com.example.File.sharing.dto.FileDTO;
import com.example.File.sharing.dto.TagDTO;
import com.example.File.sharing.entity.LifeTime;
import com.example.File.sharing.exception.FileNotFoundException;
import com.example.File.sharing.entity.Comment;
import com.example.File.sharing.entity.File;
import com.example.File.sharing.entity.Tag;
import com.example.File.sharing.service.CommentService;
import com.example.File.sharing.service.FileService;
import com.example.File.sharing.service.LifeTimeService;
import com.example.File.sharing.service.S3Service;
import com.example.File.sharing.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileSharingController {

    private final ModelMapper modelMapper;

    private final FileService fileService;
    private final S3Service s3Service;
    private final TagService tagService;
    private final CommentService commentService;
    private final LifeTimeService lifeTimeService;

    @GetMapping
    public ResponseEntity<List<FileDTO>> getAllFiles(@RequestParam(value = "tagId", required = false) Integer tagId) {
        List<FileDTO> fileDTOs;

        if (tagId != null) {
            fileDTOs = fileService.findByTagId(tagId).stream()
                    .map(this::convertToFileDTO)
                    .collect(Collectors.toList());
        } else {
            fileDTOs = fileService.findAll().stream()
                    .map(this::convertToFileDTO)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(fileDTOs);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File dbFile = new File();
            dbFile.setName(file.getOriginalFilename());
            dbFile = fileService.save(dbFile);

            String s3Key = String.valueOf(dbFile.getId());
            CompletableFuture<Void> future = s3Service.uploadFile(file, s3Key);
            future.join();

            return ResponseEntity.ok("File uploaded successfully with ID: " + s3Key);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable int id) {
        try {
            File dbFile = fileService.findById(id)
                    .orElseThrow(() -> new RuntimeException("File not found with ID: " + id));

            String fileName = dbFile.getName();

            String extension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                extension = fileName.substring(dotIndex);
            }
            String s3Key = dbFile.getId() + extension;

            CompletableFuture<InputStream> fileStreamFuture = s3Service.downloadFile(s3Key);

            InputStream fileStream = fileStreamFuture.join();

            String encodedFileName = URLEncoder.encode(dbFile.getName(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));


        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/files/{id}/delete")
    public ResponseEntity<?> deleteFile(@PathVariable int id) {
        File file = fileService.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + id));

        String fileName = file.getName().split("\\.")[1];

        s3Service.deleteFile(id + "." +  fileName);
        fileService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/files/{fileId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable int fileId) {
        File file = fileService.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        List<Comment> comments = new ArrayList<>(file.getComments());
        Collections.reverse(comments);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/files/{fileId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable int fileId, @RequestBody Comment comment) {
        File file = fileService.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        comment.setFile(file);
        Comment savedComment = commentService.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    @PostMapping("/tags")
    public ResponseEntity<?> addTag(@RequestBody Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tag name cannot be empty.");
        }

        Optional<Tag> existingTag = tagService.findByName(tag.getName());
        if (existingTag.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tag already exists.");
        }
        Tag savedTag = tagService.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTag);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.findAll();
        return ResponseEntity.ok(tags);
    }

    @PostMapping("/files/{fileId}/attach-tags")
    public ResponseEntity<String> attachTags(
            @PathVariable int fileId,
            @RequestBody TagDTO request) {
        try {
            fileService.attachTagsToFile(fileId, request.getTags());

            return ResponseEntity.ok("Tags attached successfully!");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to attach tags to file.");
        }
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Integer tagId) {
        try {
            tagService.deleteTagById(tagId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/files/{fileId}/set-lifetime")
    public ResponseEntity<?> addLifetime(@PathVariable int fileId,
                                              @RequestBody LifeTime lifeTime){
        try{
            System.out.println(lifeTime.getHours());
            File file = fileService.findById(fileId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
            lifeTime.setFile(file);
            LifeTime savedTime = lifeTimeService.save(lifeTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTime);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private FileDTO convertToFileDTO(File file) {
        return modelMapper.map(file, FileDTO.class);
    }
}
