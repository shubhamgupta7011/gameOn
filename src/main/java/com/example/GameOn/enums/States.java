package com.example.GameOn.enums;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum States {
    Delhi("Delhi",""),
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


    private final List<String> descriptions;

    States(String... descriptions) {
        this.descriptions = Arrays.asList(descriptions);
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    // Map from description -> list of cities having that description
    private static final Map<String, List<States>> DESCRIPTION_TO_STATES;
    private static final Map<States, List<String>> STATES_TO_DESCRIPTIONS;

    static {
        DESCRIPTION_TO_STATES = Arrays.stream(States.values())
                .flatMap(city -> city.getDescriptions().stream()
                        .map(desc -> new AbstractMap.SimpleEntry<>(desc, city)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        STATES_TO_DESCRIPTIONS = Arrays.stream(States.values())
                .collect(Collectors.toMap(
                        state -> state, States::getDescriptions
                ));
    }

    public static Map<String, List<States>> getDescriptionToStatesMap() {
        return DESCRIPTION_TO_STATES;
    }

    public static Map<States, List<String>> getCityToDescriptionsMap() { return STATES_TO_DESCRIPTIONS; }

}
