//package com.example.GameOn.validator;
//
//import com.example.GameOn.annotation.ValueOfEnum;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import java.util.Arrays;
//
//public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {
//
//    private String[] acceptedValues;
//
//    @Override
//    public void initialize(ValueOfEnum annotation) {
//        acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
//                .map(Enum::name)
//                .toArray(String[]::new);
//    }
//
//    @Override
//    public boolean isValid(String value, ConstraintValidatorContext context) {
//        if (value == null) {
//            return true; // allow null, use @NotNull if needed
//        }
//        return Arrays.asList(acceptedValues).contains(value);
//    }
//}
