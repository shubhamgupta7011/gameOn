package com.example.GameOn.enums;

public enum EventType {
    PRIVATE_EVENT("In Progress"),
    PUBLIC_EVENT("Completed");

    private final String description;

    EventType(String description) {
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

