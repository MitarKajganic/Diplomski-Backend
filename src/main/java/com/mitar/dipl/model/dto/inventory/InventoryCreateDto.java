package com.mitar.dipl.model.dto.inventory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryCreateDto {

    @NotNull(message = "Item name cannot be null")
    @NotEmpty(message = "Item name cannot be empty")
    private String itemName;

    @NotNull(message = "Unit cannot be null")
    @NotEmpty(message = "Unit cannot be empty")
    private String unit;

}
