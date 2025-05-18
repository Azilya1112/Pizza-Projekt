package com.example.demo.grade.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FilesStorageService {

    private final String uploadDir = "static/images"; 
    private static final Logger logger = LoggerFactory.getLogger(FilesStorageService.class);

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = generateFileName(file);

        try {
            saveFile(uploadDir, fileName, file);
            logger.info("File saved to: " + uploadDir + "/" + fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + fileName, e);
        }

        return "/images/" + fileName; 
    }

    public Resource load(String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Failed to load image: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to load image: " + filename, e);
        }
    }

    public void delete(String filename) {
        Path file = Paths.get(uploadDir).resolve(filename);
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image: " + filename, e);
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        return UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

