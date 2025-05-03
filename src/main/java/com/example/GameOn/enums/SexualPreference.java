package com.example.GameOn.enums;

public enum SexualPreference {
    HETEROSEXUAL("Attracted to the opposite sex"),
    HOMOSEXUAL("Attracted to the same sex"),
    BISEXUAL("Attracted to both sexes"),
    ASEXUAL("No sexual attraction"),
    PANSEXUAL("Attracted to all gender identities"),
    DEMISEXUAL("Sexual attraction only after emotional connection"),
    QUEER("Non-specific or fluid attraction"),
    NO_PREFERENCE("No specific preference");

    private final String description;

    SexualPreference(String description) {
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

