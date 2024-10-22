package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MenuService {

    /**
     * Fetches all menus.
     *
     * @return List of MenuDto
     */
    List<MenuDto> getAllMenus();


    /**
     * Fetches a menu by its ID.
     *
     * @param menuId The UUID of the menu as a string.
     * @return MenuDto
     */
    MenuDto getMenuById(String menuId);


    /**
     * Fetches a menu by its name.
     *
     * @param menuName The name of the menu.
     * @return MenuDto
     */
    MenuDto getMenuByMenuName(String menuName);


    /**
     * Creates a new menu.
     *
     * @param menuCreateDto The DTO containing menu creation data.
     * @return MenuDto
     */
    MenuDto createMenu(MenuCreateDto menuCreateDto);


    /**
     * Deletes a menu by its ID.
     *
     * @param menuId The UUID of the menu as a string.
     * @return Success message.
     */
    String deleteMenu(String menuId);


    /**
     * Updates an existing menu.
     *
     * @param menuId         The UUID of the menu as a string.
     * @param menuCreateDto The DTO containing updated menu data.
     * @return MenuDto
     */
    MenuDto updateMenu(String menuId, MenuCreateDto menuCreateDto);

}
