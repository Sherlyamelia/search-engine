package com.sherly.ifashion.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class RecommendationService {

    public String getRecommendations(MultipartFile file) {
        // URL endpoint Flask
        String flaskUrl = "http://127.0.0.1:5000/recommend";

        try {
            // Konversi MultipartFile ke File
            File tempFile = convertMultipartFileToFile(file);

            // Buat request body untuk multipart/form-data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));

            // Buat header untuk request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Gabungkan body dan header
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Kirim request ke Flask menggunakan RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, requestEntity, String.class);

            // Hapus file sementara setelah request
            tempFile.delete();

            // Kembalikan respons dari Flask
            return response.getBody();

        } catch (IOException e) {
            return "Error: File handling error - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Helper: Konversi MultipartFile ke File sementara
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        // Membuat file sementara dengan nama acak
        File tempFile = File.createTempFile("upload-", ".tmp");  // Gunakan ekstensi sementara
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }
}
