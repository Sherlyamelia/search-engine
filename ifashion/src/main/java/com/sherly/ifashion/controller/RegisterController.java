package com.sherly.ifashion.controller;

import com.sherly.ifashion.model.User;
import com.sherly.ifashion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register")
    public String form(Model model) {
        User user = new User();
        model.addAttribute("user", user); // Mengirimkan objek User ke view
        return "register"; // Mengarahkan ke halaman register.html
    }

    @PostMapping(value = "/register")
    public String saveForm(@ModelAttribute("user") User user, Model model) {
        // Menyimpan user ke database
        userService.saveUser(user);

        // Menambahkan pesan sukses
        model.addAttribute("message", "User berhasil disimpan!");

        return "login"; // Mengarahkan ke halaman login.html
    }
}
