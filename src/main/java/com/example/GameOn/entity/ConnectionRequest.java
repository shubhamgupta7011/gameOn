package com.example.GameOn.entity;

import com.example.GameOn.enums.RequestStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class ConnectionRequest {
    @Id
    private String id;
    private String fromUserId;
    private String toUserId;
    private RequestStatus status; // PENDING, ACCEPTED, REJECTED

    @CreatedDate
    private Long createdAt;

    @LastModifiedDate
    private Long respondedAt;

}
