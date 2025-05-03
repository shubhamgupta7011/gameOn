package com.example.GameOn.enums;

public enum InterestedIn {
    MEN("Men"),
    WOMEN("Women"),
    EVERYONE("Everyone");

    private final String description;

    InterestedIn(String description) {
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
