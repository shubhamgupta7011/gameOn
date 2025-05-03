package com.example.GameOn.enums;

public enum Skills {
    Football("Football"),
    Soccer("Soccer"),
    Basketball("Basketball"),
    Baseball("Baseball"),
    Cricket("Cricket"),
    Tennis("Tennis"),
    Badminton("Badminton"),
    Volleyball("Volleyball"),
    Swimming("Swimming"),
    Running("Running"),
    Cycling("Cycling"),
    Table_Tennis("Table Tennis"),
    Play_Station("Play Station"),
    Gym("Gym"),
    Yoga("Yoga"),
    Golf("Golf"),
    Boxing("Boxing"),
    Martial_Arts("Martial Arts"),
    Rugby("Rugby"),
    Chess("Chess"),
    Esports("Esports"),
    Other("Other");

    private final String displayName;

    Skills(String displayName) {
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
