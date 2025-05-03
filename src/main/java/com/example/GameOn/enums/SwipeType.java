package com.example.GameOn.enums;

public enum SwipeType {
    RIGHT("Right"),
    LEFT("Left"),
    SUPER_LIKE("Super Like");

    private final String description;

    SwipeType(String description) {
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
