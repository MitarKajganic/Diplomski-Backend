package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import org.springframework.http.ResponseEntity;

public interface MenuService {

    ResponseEntity<?> getMenuById(String menuId);

    ResponseEntity<?> getMenuByMenuName(String menuName);

    ResponseEntity<?> deleteMenu(String menuId);

    ResponseEntity<?> createMenu(MenuCreateDto menuCreateDto);

    ResponseEntity<?> updateMenu(String menuId, MenuCreateDto menuCreateDto);

}
