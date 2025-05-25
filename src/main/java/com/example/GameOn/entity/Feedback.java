package com.example.GameOn.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;

@Data
public class Feedback {
    @Id
    private String id;

    private int rating;
    private String venueId;
    private String clubId;
    private String comments;
    private List<String> image;

    private String UserName;
    private String userId;
    private String userImage;

    @CreatedDate
    private Long createdOn;  // Automatically set when the document is created
    @LastModifiedDate
    private Long lastUpdatedOn;
}
