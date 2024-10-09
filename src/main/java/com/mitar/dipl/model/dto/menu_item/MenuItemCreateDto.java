package com.mitar.dipl.model.dto.menu_item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemCreateDto {

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Description cannot be null")
    @NotEmpty(message = "Description cannot be empty")
    @Size(min = 10, message = "Description must contain at least 10 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be greater than or equal to zero")
    private BigDecimal price;

    @NotNull(message = "Category cannot be null")
    @NotEmpty(message = "Category cannot be empty")
    @Size(min = 3, message = "Category must contain at least 3 characters")
    private String category;

    @NotNull(message = "Menu ID cannot be null")
    @NotEmpty(message = "Menu ID cannot be empty")
    private String menuId;

}
