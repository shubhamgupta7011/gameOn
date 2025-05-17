package com.example.GameOn.entity;

import com.example.GameOn.enums.Skills;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;

@Data
public class Amenity {
    @Id
    private String id;
    private String venueId;
    private String name;
    private String description;
    private List<String> image;
    private boolean availability;
    private Double price;
    private String groundId;
    private Skills skills;
    private List<TimeSlots> timeSlots;

//    private List<Long> availableTimeSlot;

    @CreatedDate
    private Long createdOn;

    @LastModifiedDate
    private Long lastUpdatedOn;
}
