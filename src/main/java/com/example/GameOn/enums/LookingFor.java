package com.example.GameOn.enums;

public enum LookingFor {
    FRIENDSHIP("Looking for friendship"),
    CASUAL_DATING("Looking for casual dating"),
    SERIOUS_RELATIONSHIP("Looking for a serious relationship"),
    MARRIAGE("Looking for marriage"),
    ACTIVITY_PARTNER("Looking for an activity partner"),
    NETWORKING("Looking for professional networking"),
    TRAVEL_BUDDY("Looking for a travel buddy"),
    SPORT_PARTNER("Looking for a sports partner"),
    NO_PREFERENCE("No specific preference");

    private final String description;

    LookingFor(String description) {
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

