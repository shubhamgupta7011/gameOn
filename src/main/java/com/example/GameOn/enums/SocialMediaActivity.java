package com.example.GameOn.enums;

public enum SocialMediaActivity {
    VERY_ACTIVE("Posts and engages daily"),
    MODERATELY_ACTIVE("Posts and interacts a few times a week"),
    OCCASIONAL_USER("Uses social media occasionally"),
    LURKER("Browses but rarely posts"),
    INFLUENCER("Actively creates and shares content"),
    PROFESSIONAL_USE("Uses social media mainly for work or networking"),
    PRIVATE_USER("Keeps profile private and shares minimally"),
    OFFLINE("Rarely or never uses social media"),
    NO_PREFERENCE("No specific preference");

    private final String description;

    SocialMediaActivity(String description) {
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

