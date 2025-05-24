package com.example.GameOn.enums;

public enum RequestStatus {
    //PENDING, ACCEPTED, REJECTED
    REJECTED("Rejected"),
    PENDING("Pending"),
    ACCEPTED("Accepted");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}

