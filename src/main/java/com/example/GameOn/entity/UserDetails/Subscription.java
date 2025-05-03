package com.example.GameOn.entity.UserDetails;

import com.example.GameOn.enums.PlansType;
import lombok.Data;

@Data
public class Subscription {
    private String planId;
    private PlansType plansType;
    private Long subscriptionStartDate;
    private Long subscriptionStartEndDate;
}
