package com.example.GameOn.enums;

public enum Language {
    ENGLISH("English"),
    SPANISH("Spanish"),
    FRENCH("French"),
    GERMAN("German"),
    MANDARIN("Mandarin Chinese"),
    HINDI("Hindi"),
    ARABIC("Arabic"),
    PORTUGUESE("Portuguese"),
    RUSSIAN("Russian"),
    JAPANESE("Japanese"),
    KOREAN("Korean"),
    ITALIAN("Italian"),
    DUTCH("Dutch"),
    TURKISH("Turkish"),
    SWEDISH("Swedish"),
    HEBREW("Hebrew"),
    GREEK("Greek"),
    POLISH("Polish"),
    VIETNAMESE("Vietnamese"),
    THAI("Thai"),
    BENGALI("Bengali"),
    TAMIL("Tamil"),
    URDU("Urdu"),
    PERSIAN("Persian"),
    OTHER("Other");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

//    @Override
//    public String toString() {
//        return displayName;
//    }
}

