package com.mitar.dipl.model.dto.menu;

import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class MenuCreateDto {

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Items cannot be null")
    @Size(min = 1, message = "Items must contain at least one item")
    private Set<MenuItemDto> items;

}
