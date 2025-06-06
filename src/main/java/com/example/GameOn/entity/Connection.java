package com.example.GameOn.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.List;

@Data
public class Connection {
    @Id
    private String id;
    private String usersId;
    private String phoneNumber;
    private List<String> fUserId;


//    private List<Long> availableTimeSlot;

    @CreatedDate
    private Long createdOn;

    @LastModifiedDate
    private Long lastUpdatedOn;
}
