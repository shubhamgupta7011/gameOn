package com.example.GameOn.enums;

public enum DrinkingPreference {
    NON_DRINKER("Does not drink"),
    OCCASIONAL_DRINKER("Drinks occasionally"),
    SOCIAL_DRINKER("Drinks in social settings"),
    REGULAR_DRINKER("Drinks regularly"),
    HEAVY_DRINKER("Drinks heavily"),
    TRYING_TO_QUIT("Trying to quit drinking"),
    NO_PREFERENCE("No specific preference");

    private final String description;

    DrinkingPreference(String description) {
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

