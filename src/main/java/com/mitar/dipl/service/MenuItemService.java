package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MenuItemService {

    /**
     * Fetches all menu items.
     *
     * @return List of MenuItemDto
     */
    List<MenuItemDto> getMenuItems();

    /**
     * Fetches a menu item by its name.
     *
     * @param name The name of the menu item.
     * @return MenuItemDto
     */
    MenuItemDto getMenuItemByName(String name);

    /**
     * Fetches menu items containing the specified name (case-insensitive).
     *
     * @param name The partial name to search for.
     * @return List of MenuItemDto
     */
    List<MenuItemDto> getMenuItemsByNameContaining(String name);

    /**
     * Fetches a menu item by its ID.
     *
     * @param id The UUID of the menu item as a string.
     * @return MenuItemDto
     */
    MenuItemDto getMenuItemById(String id);


    /**
     * Fetches menu items by their category.
     *
     * @param category The category to filter by.
     * @return List of MenuItemDto
     */
    List<MenuItemDto> getMenuItemsByCategory(String category);

    /**
     * Creates a new menu item.
     *
     * @param menuItemCreateDto The DTO containing menu item creation data.
     * @return MenuItemDto
     */
    MenuItemDto createMenuItem(MenuItemCreateDto menuItemCreateDto);


    /**
     * Deletes a menu item by its ID.
     *
     * @param id The UUID of the menu item as a string.
     * @return Success message.
     */
    String deleteMenuItem(String id);

    /**
     * Removes a menu item from a specific menu.
     *
     * @param menuItemId The UUID of the menu item as a string.
     * @param menuId     The UUID of the menu as a string.
     * @return Success message.
     */
    String deleteMenuItemFromMenu(String menuItemId, String menuId);


    /**
     * Updates an existing menu item.
     *
     * @param id                 The UUID of the menu item as a string.
     * @param menuItemCreateDto The DTO containing updated menu item data.
     * @return MenuItemDto
     */
    MenuItemDto updateMenuItem(String id, MenuItemCreateDto menuItemCreateDto);

}
