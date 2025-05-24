package com.example.GameOn.controller;

//import com.example.GameOn.service.UserDetailsServiceImpl;
//import com.example.GameOn.service.UserService;
//import com.example.GameOn.utils.JWTUtils;
import com.example.GameOn.service.OTPService;
import com.example.GameOn.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class PublicController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    // Map to store generated OTP for phone numbers (this could be stored in a database or cache)
    private Map<String, String> otpMap = new HashMap<>();

    @Operation(
            summary = "Send OTP",
            description = "To Send OTP to the user to login user into app"
    )
    @PostMapping("/send-otp/{number}")
    public ResponseEntity<String> sendOtp(@PathVariable String number) {
        // Generate OTP
        String otp = otpService.generateOTP();
        otpMap.put(number, otp); // Store OTP (for demo purposes, use a cache or DB in production)

        // Send OTP to the user's phone number
        log.info("otp {} is send to user {} successfully", otp, number);
        otpService.sendOTP(number, otp);

        return ResponseEntity.ok("OTP sent to phone number.");
    }

    @Operation(
            summary = "Verify otp",
            description = "To Verify OTP which is send to user on phone number"
    )
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        String storedOtp = otpMap.get(phoneNumber);

        if (storedOtp != null && storedOtp.equals(otp)) {
            // OTP verified, generate JWT token
            String token = jwtUtil.generateToken(phoneNumber);

            Map<String, String> response = new HashMap<>();
            response.put("phone_Number", phoneNumber);
            response.put("token", token);
            response.put("message", "Login successful!");

            // (optional) Clear OTP after use
            otpMap.remove(phoneNumber);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP.");
        }
    }

}
