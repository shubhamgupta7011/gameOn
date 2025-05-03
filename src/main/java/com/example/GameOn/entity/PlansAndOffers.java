package com.example.GameOn.entity;

import com.example.GameOn.enums.PlansType;
import com.example.GameOn.enums.Skills;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.List;

@Data
public class PlansAndOffers {
    @Id
    private String id;
    private String name;
    private String description;
    private PlansType type;
    private List<String> facility;
    private String planId;
    private int days;
    private int price;

    @CreatedDate
    private Long createdOn;

    @LastModifiedDate
    private Long lastUpdatedOn;

}
