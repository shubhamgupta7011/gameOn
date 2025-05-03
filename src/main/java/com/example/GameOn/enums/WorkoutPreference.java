package com.example.GameOn.enums;

public enum WorkoutPreference {
    NEVER("Does not work out"),
    OCCASIONAL("Works out occasionally"),
    REGULAR("Works out regularly"),
    INTENSE("Intense workouts frequently"),
    ATHLETE("Professional or competitive training"),
    NO_PREFERENCE("No specific preference");

    private final String description;

    WorkoutPreference(String description) {
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

