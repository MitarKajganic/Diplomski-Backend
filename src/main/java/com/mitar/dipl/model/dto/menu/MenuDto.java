package com.mitar.dipl.model.dto.menu;

import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class MenuDto {

    private String id;
    private String name;
    private List<MenuItemDto> items;

}
