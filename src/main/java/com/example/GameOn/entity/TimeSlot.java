package com.example.GameOn.entity;

import com.example.GameOn.enums.TimeSlotStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.List;

@Data
public class TimeSlot {
    private Long startTime;
    private Long endTime;
    private boolean availability;
    private TimeSlotStatus status;
    private String bookingId;
    private List <String> usersId;
}
