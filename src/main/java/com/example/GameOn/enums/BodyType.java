package com.example.GameOn.enums;

public enum BodyType {
    SLIM("Slim body type"),
    ATHLETIC("Athletic/muscular body type"),
    CURVY("Curvy body type"),
    AVERAGE("Average body type"),
    PLUS_SIZE("Plus-size body type"),
    NO_PREFERENCE("No specific body preference");

    private final String description;

    BodyType(String description) {
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
