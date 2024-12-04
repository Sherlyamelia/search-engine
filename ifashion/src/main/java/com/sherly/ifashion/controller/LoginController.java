package com.sherly.ifashion.controller;

import com.sherly.ifashion.model.User;
import com.sherly.ifashion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("errorMessage", "");
        return "login"; // Menampilkan halaman login.html
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               Model model) {
        User user = userService.findByUsername(username);

        if (user == null) {
            model.addAttribute("errorMessage", "Username tidak ditemukan!");
            return "login"; // Tetap di halaman login jika username salah
        } else if (!user.getPassword().equals(password)) {
            model.addAttribute("errorMessage", "Password salah!");
            return "login"; // Tetap di halaman login jika password salah
        }

        // Jika validasi berhasil
        return "redirect:/home";
    }

}


