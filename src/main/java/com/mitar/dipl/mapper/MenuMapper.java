package com.mitar.dipl.mapper;

import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class MenuMapper {

    private final MenuItemMapper menuItemMapper;

    private final MenuItemRepository menuItemRepository;

    public MenuDto toDto(Menu menu) {
        MenuDto menuDto = new MenuDto();
        menuDto.setId(menu.getId().toString());
        menuDto.setName(menu.getName());

        if (menu.getItems() != null) {
            List<MenuItemDto> menuItemDtos = menu.getItems().stream()
                    .map(menuItemMapper::toDto)
                    .toList();
            menuDto.setItems(menuItemDtos);
        }

        return menuDto;
    }

    public Menu toEntity(MenuCreateDto menuCreateDto) {
        Menu menu = new Menu();
        menu.setName(menuCreateDto.getName());

        for (String menuItemId : menuCreateDto.getItemIds()) {
            UUID parsedMenuItemId = UUIDUtils.parseUUID(menuItemId);
            MenuItem menuItem = menuItemRepository.findById(parsedMenuItemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + menuItemId));
            menu.addMenuItem(menuItem);
        }

        return menu;
    }
}
