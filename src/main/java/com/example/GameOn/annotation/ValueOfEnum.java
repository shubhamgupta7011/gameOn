//package com.example.GameOn.annotation;
//
//import com.example.GameOn.validator.ValueOfEnumValidator;
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//import java.lang.annotation.*;
//
//@Documented
//@Constraint(validatedBy = ValueOfEnumValidator.class)
//@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
//@Retention(RetentionPolicy.RUNTIME)
//public @interface ValueOfEnum {
//
//    Class<? extends Enum<?>> enumClass();
//
//    String message() default "must be any of enum {enumClass}";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//}
