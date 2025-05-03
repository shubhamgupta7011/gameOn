package com.example.GameOn.enums;

public enum SleepHabits {
    EARLY_BIRD("Wakes up early and sleeps early"),
    NIGHT_OWL("Sleeps late and wakes up late"),
    LIGHT_SLEEPER("Wakes up easily from noise or disturbances"),
    HEAVY_SLEEPER("Sleeps deeply and is hard to wake up"),
    NAP_LOVER("Loves taking naps during the day"),
    INSOMNIAC("Has difficulty falling or staying asleep"),
    FLEXIBLE("Can adjust sleep schedule easily"),
    NO_PREFERENCE("No specific sleep pattern");

    private final String description;

    SleepHabits(String description) {
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

