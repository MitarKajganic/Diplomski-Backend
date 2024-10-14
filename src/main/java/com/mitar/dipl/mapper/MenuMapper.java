package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class MenuMapper {

    private MenuItemMapper menuItemMapper;

    private MenuItemRepository menuItemRepository;

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
        for (String menuItemId : menuCreateDto.getItemIds()) {
            Optional<MenuItem> menuItemDto = menuItemRepository.findById(UUID.fromString(menuItemId));
            menuItemDto.ifPresent(menuItem -> menu.getItems().add(menuItem));
        }
        return menu;
    }

}
