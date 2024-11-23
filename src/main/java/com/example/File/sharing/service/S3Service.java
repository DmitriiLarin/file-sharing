package com.example.File.sharing.service;

import com.example.File.sharing.client.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final ExecutorService executorService;

    public S3Service(
            @Value("${security.s3.accessKey}") String accessKey,
            @Value("${security.s3.secretKey}") String secretKey,
            @Value("${security.s3.endpoint}") String endpoint,
            @Value("${security.s3.bucket}") String bucket
    ) {
        this.s3Client = new S3Client(accessKey, secretKey, endpoint, bucket);
        this.executorService = Executors.newFixedThreadPool(10);
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


