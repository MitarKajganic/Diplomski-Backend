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

    @GetMapping
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

    @GetMapping("/name-containing/{menuItemName}")
    public ResponseEntity<?> getMenuItemsByNameContaining(@PathVariable String menuItemName) {
        return menuItemService.getMenuItemsByNameContaining(menuItemName);
    }

    @GetMapping("/category/{category}")
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

    @DeleteMapping("/delete/{menuItemId}/{menuId}")
    public ResponseEntity<?> deleteMenuItemFromMenu(@PathVariable String menuItemId, @PathVariable String menuId) {
        return menuItemService.deleteMenuItemFromMenu(menuItemId, menuId);
    }

    @PutMapping("/update/{menuItemId}")
    public ResponseEntity<?> updateMenuItem(@PathVariable String menuItemId, @RequestBody @Validated MenuItemCreateDto menuItemCreateDto) {
        return menuItemService.updateMenuItem(menuItemId, menuItemCreateDto);
    }

}
