package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import org.springframework.http.ResponseEntity;

public interface MenuItemService {

    ResponseEntity<?> getMenuItems();

    ResponseEntity<?> getMenuItemByName(String name);

    ResponseEntity<?> getMenuItemsByNameContaining(String name);

    ResponseEntity<?> getMenuItemById(String id);

    ResponseEntity<?> getMenuItemsByCategory(String category);

    ResponseEntity<?> createMenuItem(MenuItemCreateDto menuItemCreateDto);

    ResponseEntity<?> deleteMenuItem(String id);

    ResponseEntity<?> deleteMenuItemFromMenu(String menuItemId, String menuId);

    ResponseEntity<?> updateMenuItem(String id, MenuItemCreateDto menuItemCreateDto);

}
