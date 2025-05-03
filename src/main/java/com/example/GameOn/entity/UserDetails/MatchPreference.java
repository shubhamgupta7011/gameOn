package com.example.GameOn.entity.UserDetails;

import com.example.GameOn.enums.InterestedIn;
import lombok.Data;

@Data
public class MatchPreference {
    private int maxDistance;
    private int minAge;
    private int maxAge;
    private InterestedIn interestedIn;
}
