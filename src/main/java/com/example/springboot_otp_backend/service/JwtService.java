package com.example.springboot_otp_backend.service;


import com.example.springboot_otp_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    @Autowired
    private JwtUtil jwtUtil;

    public String createToken(String email, String role) {
        return jwtUtil.generateToken(email, role);
    }
}
