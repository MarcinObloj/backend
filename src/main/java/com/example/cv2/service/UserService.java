package com.example.cv2.service;

import java.util.List;

import com.example.cv2.model.User;

public interface UserService {
    User registerUser(User user) throws Exception;
    boolean verifyUser(String token);
    User loginUser(String username, String password) throws Exception;
    boolean resetPassword(String email, String siteURL);
    void changePassword(String email, String newPassword) throws Exception;
    User getUserById(int userId);
    List<User> getAllUsers();
}
