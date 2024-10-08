package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import org.springframework.http.ResponseEntity;

public interface MenuItemService {

    ResponseEntity<?> getMenuItems();

    ResponseEntity<?> getMenuItemByName(String name);

    ResponseEntity<?> getMenuItemById(String id);

    ResponseEntity<?> getMenuItemsByMenu(String menuId);

    ResponseEntity<?> getMenuItemsByCategory(String category);

    ResponseEntity<?> createMenuItem(MenuItemCreateDto menuItemCreateDto);

    ResponseEntity<?> deleteMenuItem(String id);

    ResponseEntity<?> updateMenuItem(String id, MenuItemCreateDto menuItemCreateDto);

}
