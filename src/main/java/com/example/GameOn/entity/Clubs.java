package com.example.GameOn.entity;

import com.example.GameOn.enums.Skills;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;


import java.util.List;

@Data
public class Clubs {
    @Id
    private String id;
    private String name;
    private String description;
    private Location location;
    private Contacts contacts;
    private String founder;
    private List<String> users;
    private Skills skill;
    private int foundedYear;
    private int totalMembersAllowed;
    private Membership membership;
    private Boolean availability = false;
    private List<String> images;
    private List<String> logo;
    private List<String> feedbacks;
    private List<String> events;

    @CreatedDate
    private Long createdOn;

    @LastModifiedDate
    private Long lastUpdatedOn;

}
