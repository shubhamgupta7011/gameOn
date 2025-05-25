package com.example.GameOn.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;

@Data
public class Ratings {
    @Id
    private String id;
    private String UserName;
    private String userImage;
    private String fromUserId;
    private int securityRating;
    private int skillRating;
    private String comments;
    private List<String> image;
    private String toUserId;

    @CreatedDate
    private Long createdOn;  // Automatically set when the document is created
    @LastModifiedDate
    private Long lastUpdatedOn;
}
