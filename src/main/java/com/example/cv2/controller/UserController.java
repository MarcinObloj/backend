package com.example.cv2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.cv2.dto.ChangePasswordRequest;
import com.example.cv2.model.User;
import com.example.cv2.service.UserService;
import com.example.cv2.service.EmailService;
import com.example.cv2.util.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, EmailService emailService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            User registeredUser = userService.registerUser(user);
            response.put("message", "Użytkownik zarejestrował się pomyślnie. Token weryfikacyjny został wysłany na Twój adres e-mail.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Email already in use")) {
                response.put("message", "Email już został użyty do rejestracji.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            response.put("message", "Wystąpił problem podczas rejestracji: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        try {
            String emailOrUsername = loginRequest.get("emailOrUsername");
            String password = loginRequest.get("password");
            User user = userService.loginUser(emailOrUsername, password);

            // Wygenerowanie tokenu JWT
            String token = jwtUtil.generateToken(user.getUsername());

            // Przygotowanie odpowiedzi
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId()); // Dodanie userId do odpowiedzi
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Logowanie nie powiodło się: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable int userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving user: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving users: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
        try {
            boolean result = userService.resetPassword(email, "http://localhost:3000/reset.html");
            if (result) {
                return ResponseEntity.ok("Na Twój adres e-mail została wysłana wiadomość z instrukcjami dotyczącymi resetowania hasła.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono użytkownika o podanym adresie e-mail.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wystąpił błąd podczas przetwarzania Twojego żądania: " + e.getMessage());
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Hasło zostało zmienione pomyślnie.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wystąpił błąd podczas zmiany hasła: " + e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("code") String code) {
        try {
            boolean isVerified = userService.verifyUser(code);
            if (isVerified) {
                String htmlContent = "<html>"
                        + "<head>"
                        + "<style>"
                        + "  body { font-family: Arial, sans-serif; font-size: 1.2rem; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }"
                        + "  .container { text-align: center; margin-top: 50px; }"
                        + "  h3 { color: #4CAF50; margin-bottom: 20px; font-size: 2rem; }"
                        + "  h4 { margin-bottom: 50px; font-size: 1.5rem; }"
                        + "  a.btn-primary { padding: 1em 2em; width: 40%; font-size: 1.6rem; color: white; background-color: #fec85b; padding: 10px 20px; border-radius: 16px; text-decoration: none; transition: background-color 0.3s; }"
                        + "  a.btn-primary:hover { background-color: #d5a84c; }"
                        + "</style>"
                        + "</head>"
                        + "<body>"
                        + "<div class=\"container\">"
                        + "    <h3>Gratulacje, twoje konto zostało zweryfikowane!</h3>"
                        + "    <h4>Możesz się teraz zalogować.</h4>"
                        + "    <a href=\"http://localhost:3000/login.html\" class=\"btn-primary\">Przejdź do logowania</a>"
                        + "</div>"
                        + "</body>"
                        + "</html>";

                return ResponseEntity.ok().header("Content-Type", "text/html").body(htmlContent);
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Nieprawidlowy lub wygasły token uwierzytelniający.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during verification: " + e.getMessage());
        }
    }
}
