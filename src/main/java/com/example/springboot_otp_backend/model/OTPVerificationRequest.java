package com.example.springboot_otp_backend.model;

public class OTPVerificationRequest {
    private String identifier; // email or phone
    private String otp;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
// getters/setters
}
