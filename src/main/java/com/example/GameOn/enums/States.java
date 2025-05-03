package com.example.GameOn.enums;

public enum States {
    Delhi("Delhi"),
    AndhraPradesh("Andhra Pradesh"),
    ArunachalPradesh("Arunachal Pradesh"),
    Assam("Assam"),
    Bihar("Bihar"),
    Chhattisgarh("Chhattisgarh"),
    Goa("Goa"),
    Gujarat("Gujarat"),
    Haryana("Haryana"),
    HimachalPradesh("Himachal Pradesh"),
    Jharkhand("Jharkhand"),
    Karnataka("Karnataka"),
    Kerala("Kerala"),
    MadhyaPradesh("Madhya Pradesh"),
    Maharashtra("Maharashtra"),
    Manipur("Manipur"),
    Meghalaya("Meghalaya"),
    Mizoram("Mizoram"),
    Nagaland("Nagaland"),
    Odisha("Odisha"),
    Punjab("Punjab"),
    Rajasthan("Rajasthan"),
    Sikkim("Sikkim"),
    TamilNadu("Tamil Nadu"),
    Telangana("Telangana"),
    Tripura("Tripura"),
    UttarPradesh("Uttar Pradesh"),
    Uttarakhand("Uttarakhand"),
    WestBengal("West Bengal"),
    JammuAndKashmir("Jammu and Kashmir"),
    Ladakh("Ladakh");

    private final String description;

    States(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
