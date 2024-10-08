package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.service.MenuService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllMenus() {
        return menuService.getAllMenus();
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<?> getMenuById(String menuId) {
        return menuService.getMenuById(menuId);
    }

    @GetMapping("/name/{menuName}")
    public ResponseEntity<?> getMenuByMenuName(String menuName) {
        return menuService.getMenuByMenuName(menuName);
    }

    @PostMapping
    public ResponseEntity<?> createMenu(MenuCreateDto menuCreateDto) {
        return menuService.createMenu(menuCreateDto);
    }

    @DeleteMapping("/delete/{menuId}")
    public ResponseEntity<?> deleteMenu(String menuId) {
        return menuService.deleteMenu(menuId);
    }

    @PutMapping("/update/{menuId}")
    public ResponseEntity<?> updateMenu(String menuId, MenuCreateDto menuCreateDto) {
        return menuService.updateMenu(menuId, menuCreateDto);
    }

}
