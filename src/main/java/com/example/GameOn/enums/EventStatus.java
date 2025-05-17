package com.example.GameOn.enums;

public enum EventStatus {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    SCHEDULED("Scheduled");



    private final String description;

    EventStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

//    @Override
//    public String toString() {
//        return description;
//    }
}

