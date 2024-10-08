package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import com.mitar.dipl.model.entity.Menu;
import org.springframework.stereotype.Component;

@Component
public class MenuMapper {

    public MenuDto toDto(Menu menu) {
        MenuDto menuDto = new MenuDto();
        menuDto.setName(menu.getName());
        menuDto.setItems(menu.getItems());
        return menuDto;
    }

    public Menu toEntity(MenuCreateDto menuCreateDto) {
        Menu menu = new Menu();
        menu.setName(menuCreateDto.getName());
        menu.setItems(menuCreateDto.getItems());
        return menu;
    }

}
