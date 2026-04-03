package com.example.springboot_otp_backend.repository;


import com.example.springboot_otp_backend.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    void deleteByIdentifierAndUsedFalse(String identifier);
    Optional<OTP> findFirstByIdentifierAndOtpCodeAndUsedFalse(String identifier, String otpCode);
}
