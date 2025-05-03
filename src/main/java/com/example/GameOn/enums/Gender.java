package com.example.GameOn.enums;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    NON_BINARY("Non-binary"),
    //    TRANSGENDER_MALE("Transgender Male"),
//    TRANSGENDER_FEMALE("Transgender Female"),
//    GENDER_FLUID("Gender Fluid"),
//    AGENDER("Agender"),
    OTHER("Other");
//    PREFER_NOT_TO_SAY("Prefer not to say");

    private final String description;

    Gender(String description) {
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

