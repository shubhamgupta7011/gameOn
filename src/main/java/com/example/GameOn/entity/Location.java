package com.example.GameOn.entity;

import com.example.GameOn.enums.City;
import com.example.GameOn.enums.States;
import lombok.Data;

@Data
public class Location {
    private String address;
    private City city;
    private States state;
    private String pinCode;
    private double latitude = 0;
    private double longitude = 0;
}
