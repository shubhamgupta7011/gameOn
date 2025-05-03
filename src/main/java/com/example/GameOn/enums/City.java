package com.example.GameOn.enums;

public enum City {
    New_Delhi("Delhi"),
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

    private final String description;

    City(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

//    private final String state;
//
//    City(String state) {
//        this.state = state;
//    }
//
//    public String getState() {
//        return state;
//    }
}


