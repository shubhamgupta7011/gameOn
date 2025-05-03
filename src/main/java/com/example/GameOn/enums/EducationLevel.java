package com.example.GameOn.enums;

public enum EducationLevel {
    NO_FORMAL_EDUCATION("No formal education"),
    PRIMARY_SCHOOL("Completed primary school"),
    HIGH_SCHOOL("Completed high school"),
    ASSOCIATE_DEGREE("Associate degree"),
    BACHELORS_DEGREE("Bachelor's degree"),
    MASTERS_DEGREE("Master's degree"),
    DOCTORATE("Doctorate (PhD, MD, etc.)"),
    TRADE_SCHOOL("Completed vocational or trade school"),
    PROFESSIONAL_CERTIFICATION("Holds a professional certification"),
    OTHER("Other education level"),
    NO_PREFERENCE("No specific preference");

    private final String description;

    EducationLevel(String description) {
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

