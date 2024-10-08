package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import com.mitar.dipl.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllMenuItems() {
        return menuItemService.getMenuItems();
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<?> getMenuItemById(@PathVariable String menuItemId) {
        return menuItemService.getMenuItemById(menuItemId);
    }

    @GetMapping("/name/{menuItemName}")
    public ResponseEntity<?> getMenuItemByMenuItemName(@PathVariable String menuItemName) {
        return menuItemService.getMenuItemByName(menuItemName);
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<?> getMenuItemByMenuId(@PathVariable String menuId) {
        return menuItemService.getMenuItemsByMenu(menuId);
    }

    @GetMapping("/menu/{category}")
    public ResponseEntity<?> getMenuItemByCategory(@PathVariable String category) {
        return menuItemService.getMenuItemsByCategory(category);
    }

    @PostMapping
    public ResponseEntity<?> createMenuItem(@RequestBody @Validated MenuItemCreateDto menuItemCreateDto) {
        return menuItemService.createMenuItem(menuItemCreateDto);
    }

    @DeleteMapping("/delete/{menuItemId}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable String menuItemId) {
        return menuItemService.deleteMenuItem(menuItemId);
    }

    @PutMapping("/update/{menuItemId}")
    public ResponseEntity<?> updateMenuItem(@PathVariable String menuItemId, @RequestBody @Validated MenuItemCreateDto menuItemCreateDto) {
        return menuItemService.updateMenuItem(menuItemId, menuItemCreateDto);
    }

}
