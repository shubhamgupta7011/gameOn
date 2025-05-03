package com.example.GameOn.entity.UserDetails;

//import com.example.GameOn.annotation.ValueOfEnum;
import com.example.GameOn.enums.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserDetails {
    private List<String> image;
    private String profileImage;
    @NotBlank(message = "name is required")
    private String name;
//    @ValueOfEnum(enumClass = Gender.class, message = "Gender must be MALE, FEMALE, or OTHER")
    private Gender gender;
    private String bio;
    private ProfectionalDetails profectionalDetails;
    private List<Skills> skills;
    private int securityRating;
    private int skillRating;
    private List<Language> languages;
    private List<Hobby> hobbies;
    private PersonalPreference personalPreference;
    private PersonalDetails personalDetails;
    private LookingFor lookingFor;
    private EducationLevel educationLevel;
    private SleepHabits SleepHabits;
    private SocialMediaActivity socialMediaActivity;
    private SocialMediaDetails socialMediaDetails;
}
