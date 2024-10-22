package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class MenuItemMapper {

    public MenuItemDto toDto(MenuItem menuItem) {
        MenuItemDto menuItemDto = new MenuItemDto();

        menuItemDto.setId(menuItem.getId().toString());
        menuItemDto.setName(menuItem.getName());
        menuItemDto.setDescription(menuItem.getDescription());
        menuItemDto.setPrice(menuItem.getPrice());
        menuItemDto.setCategory(menuItem.getCategory());
        menuItemDto.setMenuId(menuItem.getMenu() != null ? menuItem.getMenu().getId().toString() : null);

        return menuItemDto;
    }

    public MenuItem toEntity(MenuItemCreateDto menuItemCreateDto) {
        MenuItem menuItem = new MenuItem();

        menuItem.setName(menuItemCreateDto.getName());
        menuItem.setDescription(menuItemCreateDto.getDescription());
        menuItem.setPrice(menuItemCreateDto.getPrice());
        menuItem.setCategory(menuItemCreateDto.getCategory());

        return menuItem;
    }

}
