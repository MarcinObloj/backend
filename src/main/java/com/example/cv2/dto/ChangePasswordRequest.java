package com.example.cv2.dto;

public class ChangePasswordRequest {
    private String token;
    private String newPassword;

    // Gettery i settery
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
