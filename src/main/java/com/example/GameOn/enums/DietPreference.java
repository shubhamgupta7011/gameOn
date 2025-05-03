package com.example.GameOn.enums;

public enum DietPreference {
    OMNIVORE("Eats both plant and animal-based food"),
    VEGETARIAN("Avoids meat but consumes dairy and eggs"),
    VEGAN("Avoids all animal products"),
    PESCATARIAN("Vegetarian but includes fish and seafood"),
    KETO("Low-carb, high-fat diet"),
    PALEO("Focuses on whole foods, avoids processed foods"),
    MEDITERRANEAN("Balanced diet with lean proteins, healthy fats, and grains"),
    HALAL("Follows Islamic dietary laws"),
    KOSHER("Follows Jewish dietary laws"),
    GLUTEN_FREE("Avoids gluten-containing foods"),
    LACTOSE_FREE("Avoids dairy products"),
    NO_PREFERENCE("No specific dietary preference");

    private final String description;

    DietPreference(String description) {
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

