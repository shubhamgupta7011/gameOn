package com.example.GameOn.entity;

import com.example.GameOn.enums.EventStatus;
import com.example.GameOn.enums.EventType;
import com.example.GameOn.enums.Skills;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class Booking {
    @Id
    private String id;
    private List<String> userId;
    private String paymentId;
    private String amenityId;
    private String venueId;
    private String timeSlotId;
    private EventStatus status;
    private EventType type;
    private Skills skills;
    private int noOfParticipants;
}
