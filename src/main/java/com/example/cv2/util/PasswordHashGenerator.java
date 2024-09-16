package com.example.cv2.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Marcix222";  // Wprowadź oryginalne hasło tutaj
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Zahashowane hasło: " + encodedPassword);
    }
}
