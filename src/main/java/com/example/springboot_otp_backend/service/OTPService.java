package com.example.springboot_otp_backend.service;

import com.example.springboot_otp_backend.model.OTP;

import com.example.springboot_otp_backend.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OTPService {
    @Autowired
    private OTPRepository otpRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;

    public void generateAndSendOtp(String identifier, boolean isEmail) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        // Delete any existing unused OTPs for this identifier
        otpRepository.deleteByIdentifierAndUsedFalse(identifier);

        OTP otpEntity = new OTP();
        otpEntity.setIdentifier(identifier);
        otpEntity.setOtpCode(otp);
        otpEntity.setExpiryTime(expiry);
        otpRepository.save(otpEntity);

        if (isEmail) {
            emailService.sendOtpEmail(identifier, otp);
        } else {
            smsService.sendOtpSms(identifier, otp);
        }
    }

    public boolean verifyOtp(String identifier, String otp) {
        OTP otpEntity = otpRepository.findFirstByIdentifierAndOtpCodeAndUsedFalse(identifier, otp)
                .orElse(null);
        if (otpEntity == null) return false;
        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) return false;
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
        return true;
    }
}