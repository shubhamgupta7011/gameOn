package com.example.GameOn.responses;

import com.example.GameOn.entity.Amenity;
import com.example.GameOn.entity.Feedback;
import com.example.GameOn.entity.Location;
import com.example.GameOn.enums.Skills;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;

import java.util.List;
import java.util.Objects;

@Data
@Builder
public class VenueRes {
    @Id
    private String id;
    private String name;
    private Location location;
    private Boolean availability = false;
    private List<Skills> skills;
    private List<String> images;
    private List<String> feedbacks;
    private List<String> amenities;

    @Transient
    @Schema(hidden = true)
    private List<Amenity> AmenitiesObj;
    @Transient
    @Schema(hidden = true)
    private List<Feedback> feedbacksObj;

    @CreatedDate
    private Long createdOn;

    @LastModifiedDate
    private Long lastUpdatedOn;

}

