package com.mitar.dipl.model.dto.staff;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class StaffCreateDto {

    @NotEmpty(message = "Name cannot be empty")
    @NotNull(message = "Name cannot be null")
    @Min(value = 2, message = "Name must have at least 2 character")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Name must contain only letters")
    private String name;

    @NotEmpty(message = "Surname cannot be empty")
    @NotNull(message = "Surname cannot be null")
    @Min(value = 2, message = "Surname must have at least 2 character")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Surname must contain only letters")
    private String surname;

    @NotEmpty(message = "Position cannot be empty")
    @NotNull(message = "Position cannot be null")
    @Pattern(regexp = "^(WAITER|COOK|BARTENDER|MANAGER)$", message = "Position must be WAITER, COOK, BARTENDER or MANAGER")
    private String position;

    @NotEmpty(message = "Contact info cannot be empty")
    @NotNull(message = "Contact info cannot be null")
    @Min(value = 5, message = "Contact info must have at least 5 characters")
    private String contactInfo;

    private String userId;

}
