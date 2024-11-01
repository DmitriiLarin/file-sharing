package com.example.File.sharing.client;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class S3Client {
    private final S3AsyncClient s3Client;
    private final String bucketName;

    public S3Client(String accessKey, String secretKey, String endpointUrl, String bucketName) {
        var credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3AsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(endpointUrl))
                .build();
        this.bucketName = bucketName;
    }

    public CompletableFuture<Void> uploadFile(String filePath, String newFileNameWithoutExtension) {
        var originalFileName = Paths.get(filePath).getFileName().toString();
        var fileExtension = originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";

        var objectName = newFileNameWithoutExtension + fileExtension;

        try (var fileInputStream = new FileInputStream(new File(filePath))) {
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

            return s3Client.putObject(putObjectRequest, AsyncRequestBody.fromFile(Paths.get(filePath)))
                    .thenAccept(response -> System.out.println("File " + objectName + " uploaded to " + bucketName))
                    .exceptionally(e -> {
                        System.err.println("Error uploading file: " + e.getMessage());
                        return null;
                    });
        } catch (IOException e) {
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    public CompletableFuture<Void> uploadFile(MultipartFile file, String s3Key) {
        try {
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            return uploadFile(tempFile.toString(), s3Key)
                    .whenComplete((result, ex) -> {
                        try {
                            Files.delete(tempFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Void> deleteFile(String objectName) {
        var deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        return s3Client.deleteObject(deleteObjectRequest)
                .thenAccept(response -> System.out.println("File " + objectName + " deleted from " + bucketName))
                .exceptionally(e -> {
                    System.err.println("Error deleting file: " + e.getMessage());
                    return null;
                });
    }

    public CompletableFuture<InputStream> getFile(String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        return s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBlockingInputStream())
                .thenApply(response -> {
                    return response;
                });
    }

}