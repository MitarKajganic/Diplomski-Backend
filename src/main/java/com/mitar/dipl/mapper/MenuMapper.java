package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.repository.MenuItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Set<String> menuItemIds = menuCreateDto.getItems().stream().map(MenuItemDto::getId).collect(Collectors.toSet());
        for (String menuItemId : menuItemIds) {
            menuItemRepository.findById(UUID.fromString(menuItemId));
            menu.getItems().add(menuItemRepository.findById(UUID.fromString(menuItemId)).get());
        }
        return menu;
    }

}
