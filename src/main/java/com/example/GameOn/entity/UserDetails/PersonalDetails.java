package com.example.GameOn.entity.UserDetails;

import com.example.GameOn.entity.Location;
import com.example.GameOn.enums.BodyType;
import com.example.GameOn.enums.SexualPreference;
import lombok.Data;

@Data
public class PersonalDetails {
    private int hight;
    private int age;
    private int weight;
    private BodyType bodyType;
    private SexualPreference sexualPreference;
    private Location location;

}
