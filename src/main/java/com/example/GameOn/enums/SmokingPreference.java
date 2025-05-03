package com.example.GameOn.enums;

public enum SmokingPreference {
    NON_SMOKER("Does not smoke"),
    OCCASIONAL_SMOKER("Smokes occasionally"),
    REGULAR_SMOKER("Smokes regularly"),
    SOCIAL_SMOKER("Smokes in social settings"),
    TRYING_TO_QUIT("Trying to quit smoking"),
    NO_PREFERENCE("No specific preference");

    private final String description;

    SmokingPreference(String description) {
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

