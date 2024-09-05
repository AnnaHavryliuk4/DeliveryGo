package org.geekhub.coursework.delivery.main.service.restaurant;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class FileServer {
    public void saveFile(String uploadDir, MultipartFile file, String fileName) throws IOException{
        FileUploadUtils.saveFile(uploadDir, file, fileName);
    }
    public void deleteFile(String uploadDir,String fileName) throws IOException{
        FileUploadUtils.deleteFile(uploadDir,fileName);
    }
}
