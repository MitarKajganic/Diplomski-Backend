package com.mitar.dipl.model.dto.menu;

import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import com.mitar.dipl.model.entity.MenuItem;
import lombok.Data;

import java.util.Set;

@Data
public class MenuDto {

    private String name;
    private Set<MenuItemDto> items;

}
