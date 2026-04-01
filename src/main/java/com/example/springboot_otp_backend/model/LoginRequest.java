package com.example.springboot_otp_backend.model;

public class LoginRequest {
    private String email;
    private String password;
    // getters/setters

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}