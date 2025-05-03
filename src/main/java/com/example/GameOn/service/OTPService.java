package com.example.GameOn.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OTPService {
//    Recovery code :- K6LDH1786DHZJNRSUEJ42GQY
    // Twilio account SID and authentication token
    private static final String ACCOUNT_SID = "AC402246e19c2d89bf2ef222ffd3271a01";
    private static final String AUTH_TOKEN = "c73c28cbce047dd02b93c330c4a87a1f";
    private static final String FROM_PHONE = "7011203385";

    public OTPService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendOTP(String phoneNumber, String otp) {
        // Send OTP via SMS
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(FROM_PHONE),
                "Your OTP is: " + otp
        ).create();

        System.out.println("Message sent: " + message.getSid());
    }

    public String generateOTP() {
        Random random = new Random();
        int otp = random.nextInt(999999 - 100000) + 100000; // Generate 6-digit OTP
        return String.valueOf(otp);
    }
}

