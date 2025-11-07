package com.ecommerce.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {


    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File names of the current / original file ---> File name should be unique
        String originalFileName = file.getOriginalFilename(); // abc.jpg

        // Generate a unique file name
        String randomId = UUID.randomUUID().toString(); // 123455

        String uniqueFileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.'))); // 123455.jpg

        // Path of the file where it will get saved. Like images/123455.jpg

        // File.separator is / for windows. If this program is running in linux the file separator whould be different.
        // That's why we have not hardcoded it

        String filePath = path + File.separator + uniqueFileName;

        // Check if path exist and create
        File folder = new File(path); // Creates a File object which represents the file stored at the given path

        if(!folder.exists()) folder.mkdir(); // If this file path does not exist create it, Initially it does not exist, so it will be created.

        // Upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // Return filename
        return uniqueFileName;
    }
}
