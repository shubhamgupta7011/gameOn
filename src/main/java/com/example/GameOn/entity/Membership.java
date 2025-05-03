package com.example.GameOn.entity;

import lombok.Data;

@Data
public class Membership {
    private Boolean openForNewMembers;
    private String membershipFee;
    private String currency;
    private String  interval;
}
