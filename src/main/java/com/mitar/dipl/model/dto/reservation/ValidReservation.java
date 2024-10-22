package com.mitar.dipl.model.dto.reservation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ReservationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReservation {
    String message() default "Invalid reservation details";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}