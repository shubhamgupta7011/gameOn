package com.example.GameOn.entity.UserDetails;

import com.example.GameOn.enums.InterestedIn;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Builder
public class MatchPreference {
    @Id
    private String id;

    private String userId;
    private int maxDistance;
    private int minAge;
    private int maxAge;
    private InterestedIn interestedIn;

    @CreatedDate
    private Long createdOn;  // Automatically set when the document is created

    @LastModifiedDate
    private Long lastUpdatedOn;
}
