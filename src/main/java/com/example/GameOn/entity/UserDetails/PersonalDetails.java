package com.example.GameOn.entity.UserDetails;

import com.example.GameOn.entity.Location;
import com.example.GameOn.enums.BodyType;
import com.example.GameOn.enums.SexualPreference;
import lombok.Data;

import java.util.Date;

@Data
public class PersonalDetails {
    private int hight;
    //not required
    private int age;
    private Date dateOfBirth;
    private int weight;
    private BodyType bodyType;
    private SexualPreference sexualPreference;
//    private Location location;

}
