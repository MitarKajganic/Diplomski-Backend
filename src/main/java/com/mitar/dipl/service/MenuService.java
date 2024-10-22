package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MenuService {

    List<MenuDto> getAllMenus();

    MenuDto getMenuById(String menuId);

    MenuDto getMenuByMenuName(String menuName);

    MenuDto createMenu(MenuCreateDto menuCreateDto);

    String deleteMenu(String menuId);

    MenuDto updateMenu(String menuId, MenuCreateDto menuCreateDto);

}
