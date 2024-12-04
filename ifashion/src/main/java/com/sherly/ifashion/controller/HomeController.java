package com.sherly.ifashion.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    // Menampilkan halaman utama setelah login berhasil
    @GetMapping("/home")
    public String showHomePage() {
        return "home"; // Nama file home.html tanpa ekstensi
    }

    // Endpoint untuk menampilkan halaman konfirmasi logout
    @GetMapping("/logout-confirmation")
    public String showLogoutConfirmation() {
        return "logout"; // Mengarahkan ke logout.html
    }

    // Endpoint untuk melakukan logout jika pengguna mengkonfirmasi
    @PostMapping("/perform-logout")
    public String performLogout(HttpSession session) {
        // Menghapus sesi pengguna
        session.invalidate();

        // Mengarahkan pengguna ke halaman login setelah logout
        return "redirect:/dashboard";
    }

    @Value("${http://127.0.0.1:5000}") // URL Flask API, bisa disesuaikan
    private String flaskApiUrl;

    @PostMapping("/upload-image")
    public String uploadImage(@RequestParam("file") MultipartFile file, Model model) {
        try {
            // Membuat request ke Flask API
            RestTemplate restTemplate = new RestTemplate();

            // Menyusun header dan body untuk request
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "multipart/form-data");
            HttpEntity<MultipartFile> requestEntity = new HttpEntity<>(file, headers);

            // Mengirimkan file ke Flask API
            ResponseEntity<String> response = restTemplate.exchange(
                    flaskApiUrl + "/recommend", HttpMethod.POST, requestEntity, String.class
            );

            // Menyimpan rekomendasi gambar dari Flask ke model
            model.addAttribute("recommendedImages", response.getBody());

            // Menampilkan halaman home.html dengan rekomendasi gambar
            return "home";
        } catch (Exception e) {
            model.addAttribute("error", "Image upload failed: " + e.getMessage());
            return "home";
        }
    }
}

