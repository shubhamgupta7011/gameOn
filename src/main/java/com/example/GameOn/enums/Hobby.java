package com.example.GameOn.enums;

public enum Hobby {
    TRAVELLING("Travelling"),
    READING("Reading"),
    WRITING("Writing"),
    PAINTING("Painting"),
    PHOTOGRAPHY("Photography"),
    COOKING("Cooking"),
    BAKING("Baking"),
    TRAVELING("Traveling"),
    GARDENING("Gardening"),
    HIKING("Hiking"),
    CAMPING("Camping"),
    FISHING("Fishing"),
    MUSIC("Listening to Music"),
    PLAYING_MUSIC("Playing a Musical Instrument"),
    DANCING("Dancing"),
    ACTING("Acting"),
    GAMING("Gaming"),
    BOARD_GAMES("Board Games"),
    PUZZLES("Solving Puzzles"),
    DIY("DIY & Crafting"),
    COLLECTING("Collecting Items"),
    VOLUNTEERING("Volunteering"),
    YOGA("Yoga"),
    MEDITATION("Meditation"),
    FITNESS("Fitness & Gym"),
    CYCLING("Cycling"),
    RUNNING("Running"),
    SWIMMING("Swimming"),
    LANGUAGE_LEARNING("Learning New Languages"),
    ASTROLOGY("Astrology"),
    STARGAZING("Stargazing"),
    INVESTING("Investing"),
    BLOGGING("Blogging"),
    PODCASTING("Podcasting"),
    ANIME("Watching Anime"),
    MOVIES("Watching Movies"),
    THEATER("Theater & Drama"),
    TECHNOLOGY("Exploring Technology"),
    ROBOTICS("Robotics"),
    CODING("Coding & Programming"),
    ANIMALS("Caring for Animals"),
    OTHER("Other");

    private final String description;

    Hobby(String description) {
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

