package com.example.GameOn.entity.UserDetails;

import com.example.GameOn.enums.*;
import lombok.Data;

@Data
public class PersonalPreference {
    private SmokingPreference smoking;
    private DrinkingPreference drinking;
    private WorkoutPreference workout;
    private DietPreference diet;
}
