package com.example.cv2.service;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

import com.example.cv2.model.User;
import com.example.cv2.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(User user) throws Exception {
        System.out.println("Start of registerUser");

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Ten email został już użyty do rejestracji.");
        }

        // Hashowanie hasła
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // Ustawienie tokenu weryfikacyjnego
        user.setVerificationToken(generateVerificationToken());

        // Ustawienie czasu wygaśnięcia tokenu
        user.setTokenExpiration(LocalDateTime.now().plusDays(1));

        // Ustawienie flagi weryfikacji na false
        user.setEmailVerified(false);

        // Zapisanie użytkownika do bazy danych
        User registeredUser = userRepository.save(user);

        System.out.println("User saved, sending email");

        // Wysłanie emaila po zapisaniu użytkownika
        String baseVerifyUrl = "http://localhost:8080/api/users/verify";
        String verificationUrl = baseVerifyUrl + "?code=" + registeredUser.getVerificationToken();
        emailService.sendVerificationEmail(registeredUser, "http://localhost:8080/api/users");

        System.out.println("Email sent");

        return registeredUser;
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean verifyUser(String token) {
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getTokenExpiration() != null && user.getTokenExpiration().isAfter(LocalDateTime.now())) {
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setTokenExpiration(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public User loginUser(String emailOrUsername, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(emailOrUsername)
                .or(() -> userRepository.findByUsername(emailOrUsername));

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Użytkownik nie znaleziony.");
        }

        User user = optionalUser.get();
        if (user.isEmailVerified() && passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        } else {
            throw new IllegalArgumentException("Nieprawidłowy email albo hasło");
        }
    }

    @Override
    public boolean resetPassword(String email, String siteURL) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetPasswordToken(resetToken);
            userRepository.save(user);

            String resetUrl = siteURL + "?token=" + resetToken;
            emailService.sendPasswordResetEmail(user, resetUrl);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void changePassword(String token, String newPassword) throws Exception {
        Optional<User> optionalUser = userRepository.findByResetPasswordToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setResetPasswordToken(null); // Usuń token po zmianie hasła
            userRepository.save(user);
        } else {
            throw new Exception("Nieprawidłowy token.");
        }
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            optionalUser = userRepository.findByEmail(username);
            if (!optionalUser.isPresent()) {
                throw new UsernameNotFoundException("Użytkownik nie znaleziony.");
            }
        }

        User user = optionalUser.get();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(), List.of(new SimpleGrantedAuthority("USER")));
    }
}
