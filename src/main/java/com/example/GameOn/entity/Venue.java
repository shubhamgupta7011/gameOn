package com.example.GameOn.entity;

import com.example.GameOn.enums.Skills;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;

import java.util.List;
import java.util.Objects;

@Data
public class Venue {
    @Id
    private String id;
    private String name;
    private Location location;
    private Boolean availability = false;
    private List<Skills> skills;
    private List<String> images;
    private List<String> feedbacks;
    private List<String> amenities;

    @CreatedDate
    private Long createdOn;

    @LastModifiedDate
    private Long lastUpdatedOn;

}
