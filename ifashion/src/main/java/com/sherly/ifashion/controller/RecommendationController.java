package com.sherly.ifashion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class RecommendationController {

    @PostMapping("/send-to-flask")
    public String sendToFlask(@RequestParam("file") MultipartFile file) {
        File tempFile = null;
        try {
            // Menggunakan RestTemplate untuk mengirim file ke Flask
            RestTemplate restTemplate = new RestTemplate();

            // Convert file ke file sementara
            tempFile = convertMultipartFileToFile(file);

            // Menyiapkan multipart body untuk mengirim file
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));

            // Menyiapkan headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Menyiapkan request dengan body dan headers
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // URL Flask API
            String url = "http://127.0.0.1:5000/recommend";
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println("Flask Response: " + response.getBody());

            // Menghapus file sementara setelah selesai digunakan
            tempFile.delete();

            // Mengembalikan response
            return response.getBody();
        } catch (IOException e) {
            return "Error: File handling error - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Convert MultipartFile ke File sementara
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        // Menggunakan ekstensi .tmp untuk file sementara
        File tempFile = File.createTempFile("upload-", ".tmp");
        file.transferTo(tempFile);
        return tempFile;
    }
}