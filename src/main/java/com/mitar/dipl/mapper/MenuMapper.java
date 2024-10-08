package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import com.mitar.dipl.model.entity.Menu;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MenuMapper {

    private MenuItemMapper menuItemMapper;

    public MenuDto toDto(Menu menu) {
        MenuDto menuDto = new MenuDto();
        menuDto.setId(menu.getId().toString());
        menuDto.setName(menu.getName());
        menu.getItems().stream().map(menuItemMapper::toDto).forEach(menuDto.getItems()::add);
        return menuDto;
    }

    public Menu toEntity(MenuCreateDto menuCreateDto) {
        Menu menu = new Menu();
        menu.setName(menuCreateDto.getName());
        menu.setItems(menuCreateDto.getItems());
        return menu;
    }

}
