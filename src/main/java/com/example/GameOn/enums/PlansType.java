package com.example.GameOn.enums;

public enum PlansType {
    TRIAL("TRIAL"),
    MONTHLY("MONTHLY"),
    YEARLY("YEARLY"),
    HALF_YEARLY("HALF YEARLY"),
    GOLD("GOLD"),
    SILVER("SILVER"),
    LIFETIME("LIFETIME");


    private final String description;

    PlansType(String description) {
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

