package com.sherly.ifashion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    private final Path imageStorageLocation = Paths.get("C:/Users/Asus/Kampus Merdeka/MSIB/search engine/fashion recomendation/images");

    @GetMapping("/images/{imageName}")
    public Resource getImage(@PathVariable String imageName) {
        try {
            Path filePath = imageStorageLocation.resolve(imageName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Image not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while loading image", e);
        }
    }
}


