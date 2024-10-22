package com.mitar.dipl.model.dto.reservation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservationValidator implements ConstraintValidator<ValidReservation, ReservationCreateDto> {

    @Override
    public boolean isValid(ReservationCreateDto dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (dto.getUserId() == null || dto.getUserId().isEmpty()) {
            if (dto.getGuestName() == null || dto.getGuestName().isEmpty() ||
                    dto.getGuestEmail() == null || dto.getGuestEmail().isEmpty() ||
                    dto.getGuestPhone() == null || dto.getGuestPhone().isEmpty()) {
                isValid = false;
            }
        } else {
            if (dto.getGuestName() != null && !dto.getGuestName().isEmpty() ||
                    dto.getGuestEmail() != null && !dto.getGuestEmail().isEmpty() ||
                    dto.getGuestPhone() != null && !dto.getGuestPhone().isEmpty()) {
                isValid = false;
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Either userId or guest fields must be filled, not both.")
                    .addConstraintViolation();
        }

        return isValid;
    }
}