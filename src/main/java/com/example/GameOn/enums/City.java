package com.example.GameOn.enums;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

public enum City {
    New_Delhi("Delhi", "New Delhi"),
    Mumbai("Mumbai"),
    Bangalore("Bangalore"),
    Hyderabad("Hyderabad"),
    Chennai("Chennai"),
    Kolkata("Kolkata"),
    Pune("Pune"),
    Jaipur("Jaipur"),
    Ahmedabad("Ahmedabad"),
    Lucknow("Lucknow"),
    Surat("Surat"),
    Kanpur("Kanpur"),
    Nagpur("Nagpur"),
    Indore("Indore"),
    Thane("Thane"),
    Bhopal("Bhopal"),
    Visakhapatnam("Visakhapatnam"),
    Patna("Patna"),
    Vadodara("Vadodara"),
    Ghaziabad("Ghaziabad"),
    Ludhiana("Ludhiana"),
    Agra("Agra"),
    Nashik("Nashik"),
    Faridabad("Faridabad"),
    Meerut("Meerut"),
    Rajkot("Rajkot"),
    Kalyan("Kalyan"),
    Vasai_Virar("Vasai Virar"),
    Varanasi("Varanasi"),
    Srinagar("Srinagar"),
    Aurangabad("Aurangabad"),
    Dhanbad("Dhanbad"),
    Amritsar("Amritsar"),
    Navi_Mumbai("Navi Mumbai"),
    Allahabad("Allahabad"),
    Ranchi("Ranchi"),
    Howrah("Howrah"),
    Coimbatore("Coimbatore"),
    Jabalpur("Jabalpur"),
    Gwalior("Gwalior"),
    Vijayawada("Vijayawada"),
    Jodhpur("Jodhpur"),
    Madurai("Madurai"),
    Raipur("Raipur"),
    Kota("Kota"),
    Guwahati("Guwahati"),
    Chandigarh("Chandigarh"),
    Solapur("Solapur")
    ;

    private final List<String> descriptions;

    City(String... descriptions) {
        this.descriptions = Arrays.asList(descriptions);
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    // Map from description -> list of cities having that description
    private static final Map<String, List<City>> DESCRIPTION_TO_CITIES;
    private static final Map<City, List<String>> CITY_TO_DESCRIPTIONS;

    static {
        DESCRIPTION_TO_CITIES = Arrays.stream(City.values())
                .flatMap(city -> city.getDescriptions().stream()
                        .map(desc -> new AbstractMap.SimpleEntry<>(desc, city)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        CITY_TO_DESCRIPTIONS = Arrays.stream(City.values())
                .collect(Collectors.toMap(
                        city -> city, City::getDescriptions
                ));
    }

    public static Map<String,List<City>> getDescriptionToCitiesMap() {
        return DESCRIPTION_TO_CITIES;
    }

    public static Map<City, List<String>> getCityToDescriptionsMap() { return CITY_TO_DESCRIPTIONS; }

}


