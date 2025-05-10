package com.example.GameOn.entity.UserDetails;

import com.example.GameOn.entity.Location;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.List;

@Data
public class UserProfile {
    @Id
    private String id;

//    @NotBlank(message = "userName is required")
//    private String userName;
    @NotBlank(message = "password is required")
    private String password;
    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;
    @NotBlank(message = "adharCardNumber is required")
    private String adharCardNumber;
    private Boolean isVerified = false;
    @NotBlank(message = "email is required")
    @Email(message = "Invalid email address")
    private String email;
    private Subscription subscription;
    private String uid;
    private UserDetails userDetails;
    private MatchPreference matchPreference;
    private Boolean isDeleted = false;
    private Long lastActive;
    private List<EmergencyContacts> emergency_contacts;
    //need to discuss
//    private List<String> events;
//    private List<String> rating;
//    private List<String> connections;
    private Location location;

    @CreatedDate
    private Long createdOn;  // Automatically set when the document is created

    @LastModifiedDate
    private Long lastUpdatedOn;

}
