package com.example.springboot_otp_backend.controller;



import com.example.springboot_otp_backend.model.User;
import com.example.springboot_otp_backend.service.*;
import com.example.springboot_otp_backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private JwtService jwtService;

    // Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // Check if passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
        // Check if email exists
        if (userService.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        // Register user
        User user = userService.registerUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getPhone(),
                request.getRole()
        );
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    // Login: step 1 - verify email and password, then send OTP based on choice
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
        User user = userOpt.get();
        if (!userService.checkPassword(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
        // At this point, password is correct. We'll send OTP via the user's preferred method?
        // But we need frontend to tell which method (email or phone). Let's store choice in session?
        // Better: frontend sends a flag after login, or we combine steps.
        // Approach: login endpoint returns success with user info and prompts for OTP method.
        // Then frontend calls /send-otp with identifier (email or phone).
        // So here we just return that user exists and password matches.
        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "role", user.getRole()
        ));
    }

    // Send OTP to email or phone
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String method = request.get("method"); // "email" or "phone"
        boolean isEmail = method.equalsIgnoreCase("email");
        // Validate that identifier exists and matches user (optional)
        if (isEmail) {
            if (!userService.findByEmail(identifier).isPresent()) {
                return ResponseEntity.badRequest().body("Email not registered");
            }
        } else {
            // For phone, we would need to find user by phone; you can add findByPhone in UserService.
            // For simplicity, we assume phone exists.
        }
        otpService.generateAndSendOtp(identifier, isEmail);
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + identifier));
    }

    // Verify OTP and issue JWT
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OTPVerificationRequest request) {
        if (otpService.verifyOtp(request.getIdentifier(), request.getOtp())) {
            // Find user by identifier (email or phone)
            Optional<User> userOpt = userService.findByEmail(request.getIdentifier());
            if (userOpt.isEmpty()) {
                // If identifier is phone, need to search by phone
                // Add findByPhone in UserService
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOpt.get();
            String token = jwtService.createToken(user.getEmail(), user.getRole());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", user.getRole(),
                    "user", Map.of("firstName", user.getFirstName(), "lastName", user.getLastName(), "email", user.getEmail())
            ));
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }
}
