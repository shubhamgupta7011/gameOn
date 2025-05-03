package com.example.GameOn.entity;

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
    private String bookingStatus;
}
