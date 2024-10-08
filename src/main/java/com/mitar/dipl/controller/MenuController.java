package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.service.MenuService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<?> getMenuById(@PathVariable String menuId) {
        return menuService.getMenuById(menuId);
    }

    @GetMapping("/name/{menuName}")
    public ResponseEntity<?> getMenuByMenuName(@PathVariable String menuName) {
        return menuService.getMenuByMenuName(menuName);
    }

    @PostMapping
    public ResponseEntity<?> createMenu(@RequestBody @Validated MenuCreateDto menuCreateDto) {
        return menuService.createMenu(menuCreateDto);
    }

    @DeleteMapping("/delete/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable String menuId) {
        return menuService.deleteMenu(menuId);
    }

    @PutMapping("/update/{menuId}")
    public ResponseEntity<?> updateMenu(@PathVariable String menuId, @RequestBody @Validated MenuCreateDto menuCreateDto) {
        return menuService.updateMenu(menuId, menuCreateDto);
    }

}
