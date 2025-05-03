package com.example.GameOn.enums;

public enum TimeSlotStatus {
    PENDING("Pending"),
    FAILED("Failed"),
    BOOKED("Booked"),
    NOT_BOOKED("Not Booked"),
    NOT_AVAILABLE("Not Available");
    //    TRANSGENDER_MALE("Transgender Male"),
//    TRANSGENDER_FEMALE("Transgender Female"),
//    GENDER_FLUID("Gender Fluid"),
//    AGENDER("Agender"),
//    OTHER("Other");
//    PREFER_NOT_TO_SAY("Prefer not to say");

    private final String description;

    TimeSlotStatus(String description) {
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

