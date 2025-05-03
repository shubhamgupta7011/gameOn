package com.example.GameOn.controller;

import com.example.GameOn.entity.UserDetails.Users;
//import com.example.GameOn.service.UserDetailsServiceImpl;
//import com.example.GameOn.service.UserService;
//import com.example.GameOn.utils.JWTUtils;
import com.example.GameOn.service.OTPService;
import com.example.GameOn.utils.JwtUtil;
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
@RequestMapping("/api")
@Slf4j
public class PublicController {

//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Autowired
//    private JWTUtils jwtUtils;
//
//    @Autowired
//    UserService userService;
//
//    @GetMapping("/health-check")
//    public static String healthCheck() {
//        return "Ok";
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<Users> signup(@RequestBody Users myEntry){
//        try {
////            myEntry.setDate(LocalDateTime.now());
//            userService.saveNewUser(myEntry);
//            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
//        }catch (Exception exception){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody Users myEntry){
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(myEntry.getUserName(),myEntry.getPassword())
//            );
//            UserDetails userDetails = userDetailsService.loadUserByUsername(myEntry.getUserName());
//            String jwt=jwtUtils.generateToken(userDetails.getUsername());
//            return new ResponseEntity<>(jwt,HttpStatus.OK);
//        }catch (Exception exception){
//            log.error("Exception occurred");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }

    @Autowired
    private OTPService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    // Map to store generated OTP for phone numbers (this could be stored in a database or cache)
    private Map<String, String> otpMap = new HashMap<>();

    @PostMapping("/auth/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String phoneNumber) {
        // Generate OTP
        String otp = otpService.generateOTP();
        otpMap.put(phoneNumber, otp); // Store OTP (for demo purposes, use a cache or DB in production)

        // Send OTP to the user's phone number
        log.info("otp {} is send to user {} successfully", otp, phoneNumber);
        otpService.sendOTP(phoneNumber, otp);

        return ResponseEntity.ok("OTP sent to phone number.");
    }

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
