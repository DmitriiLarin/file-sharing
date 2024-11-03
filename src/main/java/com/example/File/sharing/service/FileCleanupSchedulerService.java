package com.example.File.sharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FileCleanupSchedulerService {
    private final FileService fileService;

    @Autowired
    public FileCleanupSchedulerService(FileService fileService) {
        this.fileService = fileService;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void scheduleFileCleanup() {
        fileService.deleteExpiredFiles();
    }
}
