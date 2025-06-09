package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.dto.LoginRequest;
import com.example.expensetrackingsystem.entities.User;
import com.example.expensetrackingsystem.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(authenticationService.authenticate( loginRequest.username(), loginRequest.password() ) );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getUsername() == null || user.getUsername().isBlank() ||
                user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Username and password must not be empty.");
        }



        if (!isValidBucketName(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid  name.Names must be 3–63 characters long, all lowercase, "
                            + "start and end with a letter or digit, and cannot resemble an IP address.");
        }

        try {
            authenticationService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }


    }

    private boolean isValidBucketName(String bucketName) {
        if (bucketName == null) return false;

        // Basic pattern: lowercase letters, digits, hyphens and dots
        String pattern = "^[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]$";
        if (!bucketName.matches(pattern)) return false;

        // No "..", ".-", or "-."
        if (bucketName.contains("..") || bucketName.contains(".-") || bucketName.contains("-."))
            return false;

        // Can't look like an IP address
        if (bucketName.matches("^(\\d+\\.){3}\\d+$"))
            return false;

        return true;
    }


}
