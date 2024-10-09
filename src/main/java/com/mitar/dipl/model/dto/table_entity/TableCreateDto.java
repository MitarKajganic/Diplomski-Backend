package com.mitar.dipl.model.dto.table_entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TableCreateDto {

    @NotNull(message = "Table number cannot be null")
    @Min(value = 1, message = "Table number must be greater than zero")
    private Integer tableNumber;

    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be greater than zero")
    private Integer capacity;

    @NotNull(message = "Is available cannot be null")
    private Boolean isAvailable;

}
