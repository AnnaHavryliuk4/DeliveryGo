package org.geekhub.coursework.delivery.main.service.restaurant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.geekhub.coursework.delivery.main.service.restaurant.FileUploadUtils.saveFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class FileUploadUtilsTest {
    @TempDir
    static Path tempDir;

    @Test
    void saveFile_shouldSaveFileSuccessfully(@TempDir Path tempDir) throws IOException {
        String uploadDir = tempDir.toString();
        String fileName = "test.txt";
        String fileContent = "Hello, World!";
        byte[] contentBytes = fileContent.getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName, "text/plain", contentBytes);


        saveFile(uploadDir, multipartFile, fileName);

        File uploadedFile = new File(uploadDir + File.separator + fileName);
        assertTrue(uploadedFile.exists());
        assertArrayEquals(contentBytes, Files.readAllBytes(uploadedFile.toPath()));
    }

    @Test
    void saveFile_shouldCreateUploadDirIfNotExists(@TempDir Path tempDir) throws IOException {
        String uploadDir = tempDir.resolve("uploads").toString();
        String fileName = "test.txt";
        String fileContent = "Hello, World!";
        byte[] contentBytes = fileContent.getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName, "text/plain", contentBytes);

        saveFile(uploadDir, multipartFile, fileName);

        File uploadedFile = new File(uploadDir + File.separator + fileName);
        assertTrue(uploadedFile.exists());
        assertArrayEquals(contentBytes, Files.readAllBytes(uploadedFile.toPath()));
    }

    @Test
    void saveFile_shouldThrowIOException_whenUnableToSaveFile() throws IOException {
        String uploadDir = "/nonexistent/directory";
        String fileName = "test.txt";
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName, "text/plain", new byte[0]);

        FileUploadUtils fileUploadUtilsMock = mock(FileUploadUtils.class);
        doThrow(new RuntimeException("Failed to save file")).when(fileUploadUtilsMock);

        assertThrows(IOException.class, () -> saveFile(uploadDir, multipartFile, fileName));
    }

    @Test
    void deleteFile_shouldDelete_whenFileExists() throws IOException {
        String fileName = "testFile.txt";
        Path filePath = tempDir.resolve(fileName);
        Files.createFile(filePath);

        FileUploadUtils.deleteFile(tempDir.toString(), fileName);
        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFile_shouldReturnFileNotFoundException_whenFileNotExists(){
        String fileName = "testFile.txt";
        String uploadDir = tempDir.toString();

        FileNotFoundException actualException = assertThrows(FileNotFoundException.class,
            ()->FileUploadUtils.deleteFile(uploadDir,fileName));
        assertEquals("File not found:" + "/" + fileName,actualException.getMessage());
    }

    @Test
    void deleteFile_shouldThrowIOException_whenDeleteFails() throws IOException {
        String fileName = "testFile.txt";
        String uploadDir = tempDir.toString();
        Path filePath = tempDir.resolve(fileName);
        Files.createFile(filePath);

        Files mockFiles = mock(Files.class);
        doThrow(new IOException("Failed to delete file")).when(mockFiles);
        Files.delete(filePath);


        IOException actualException = assertThrows(IOException.class,
            ()->FileUploadUtils.deleteFile(uploadDir,fileName));
        assertEquals("Failed to delete file: File not found: "+filePath,actualException.getMessage());
    }

}
