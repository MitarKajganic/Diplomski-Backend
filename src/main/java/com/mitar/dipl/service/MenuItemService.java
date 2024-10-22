package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MenuItemService {

    List<MenuItemDto> getMenuItems();

    MenuItemDto getMenuItemByName(String name);

    List<MenuItemDto> getMenuItemsByNameContaining(String name);

    MenuItemDto getMenuItemById(String id);

    List<MenuItemDto> getMenuItemsByCategory(String category);

    MenuItemDto createMenuItem(MenuItemCreateDto menuItemCreateDto);

    String deleteMenuItem(String id);

    String deleteMenuItemFromMenu(String menuItemId, String menuId);

    MenuItemDto updateMenuItem(String id, MenuItemCreateDto menuItemCreateDto);

}
