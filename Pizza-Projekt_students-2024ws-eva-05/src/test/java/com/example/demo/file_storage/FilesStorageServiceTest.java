package com.example.demo.file_storage;

import com.example.demo.grade.service.FilesStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FilesStorageServiceTest {

    private FilesStorageService filesStorageService;

    @TempDir
    Path tempDir; // Временная директория для каждого теста

    @BeforeEach
    void setUp() {
        filesStorageService = new FilesStorageService();
        // Устанавливаем приватное поле uploadDir через ReflectionTestUtils
        ReflectionTestUtils.setField(filesStorageService, "uploadDir", tempDir.toString());
    }


    @Test
    void save_ShouldThrowException_WhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> filesStorageService.save(emptyFile));

        assertEquals("File is empty", exception.getMessage());
    }


    @Test
    void load_ShouldThrowException_WhenFileDoesNotExist() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filesStorageService.load("non-existing-file.jpg"));

        assertTrue(exception.getMessage().contains("Failed to load image: non-existing-file.jpg"));
    }

    @Test
    void load_ShouldThrowException_WhenFileIsNotReadable() throws IOException {
        // Создаём файл и делаем его недоступным для чтения
        Path testFilePath = tempDir.resolve("unreadable-file.jpg");
        Files.writeString(testFilePath, "Test content");
        testFilePath.toFile().setReadable(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> filesStorageService.load("unreadable-file.jpg"));

        assertTrue(exception.getMessage().contains("Failed to load image"));
    }



    @Test
    void delete_ShouldNotThrowException_WhenFileDoesNotExist() {
        assertDoesNotThrow(() -> filesStorageService.delete("non-existing-file.jpg"));
    }


}
