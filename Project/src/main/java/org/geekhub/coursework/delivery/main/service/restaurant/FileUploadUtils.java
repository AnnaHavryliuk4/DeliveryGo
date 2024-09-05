package org.geekhub.coursework.delivery.main.service.restaurant;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadUtils {

    private FileUploadUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void saveFile(String uploadDir, MultipartFile file, String fileName) throws IOException {
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        File uploadedFile = new File(uploadPath.getAbsolutePath() + File.separator + fileName);
        FileCopyUtils.copy(file.getBytes(), uploadedFile);
    }

    public static void deleteFile(String directory, String fileName) throws IOException {
        try {
            Path filePath = Paths.get(directory, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new FileNotFoundException("File not found: " + filePath);
            }
        } catch (IOException e) {
            throw new IOException("Failed to delete file: " + e.getMessage());
        }
    }
}
