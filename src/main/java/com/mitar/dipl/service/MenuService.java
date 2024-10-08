package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import org.springframework.http.ResponseEntity;

public interface MenuService {

    ResponseEntity<?> getAllMenus();

    ResponseEntity<?> getMenuById(String menuId);

    ResponseEntity<?> getMenuByMenuName(String menuName);

    ResponseEntity<?> createMenu(MenuCreateDto menuCreateDto);

    ResponseEntity<?> deleteMenu(String menuId);

    ResponseEntity<?> updateMenu(String menuId, MenuCreateDto menuCreateDto);

}
