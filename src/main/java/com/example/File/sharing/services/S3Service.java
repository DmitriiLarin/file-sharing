package com.example.File.sharing.services;

import com.example.File.sharing.client.S3Client;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class S3Service {

    private final S3Client s3Client;

    public S3Service() {
        this.s3Client = new S3Client(
                "6b2db27724ac4f0dbbe258e8ff7a3d61",
                "4e302d6630f045f7a99fc993db83b07e",
                "https://s3.storage.selcloud.ru",
                "myserves"
        );

        ExecutorService executorService = Executors.newFixedThreadPool(10);
    }

    public CompletableFuture<Void> uploadFile(MultipartFile localFilePath, String s3Key) {
        return s3Client.uploadFile(localFilePath, s3Key);
    }

    public CompletableFuture<InputStream> downloadFile(String s3key) {
        return s3Client.getFile(s3key);
    }

    public void deleteFile(String s3Key) {
        s3Client.deleteFile(s3Key);
    }
}

