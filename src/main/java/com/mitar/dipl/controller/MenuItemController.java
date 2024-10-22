package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import com.mitar.dipl.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<?> getAllMenuItems() {
        return ResponseEntity.status(HttpStatus.OK).body(menuItemService.getMenuItems());
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<?> getMenuItemById(@PathVariable String menuItemId) {
        return ResponseEntity.status(HttpStatus.OK).body(menuItemService.getMenuItemById(menuItemId));
    }

    @GetMapping("/name/{menuItemName}")
    public ResponseEntity<?> getMenuItemByMenuItemName(@PathVariable String menuItemName) {
        return ResponseEntity.status(HttpStatus.OK).body(menuItemService.getMenuItemByName(menuItemName));
    }

    @GetMapping("/name-containing/{menuItemName}")
    public ResponseEntity<?> getMenuItemsByNameContaining(@PathVariable String menuItemName) {
        return ResponseEntity.status(HttpStatus.OK).body(menuItemService.getMenuItemsByNameContaining(menuItemName));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getMenuItemByCategory(@PathVariable String category) {
        return ResponseEntity.status(HttpStatus.OK).body(menuItemService.getMenuItemsByCategory(category));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMenuItem(@RequestBody @Validated MenuItemCreateDto menuItemCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.createMenuItem(menuItemCreateDto));
    }

    @DeleteMapping("/delete/{menuItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMenuItem(@PathVariable String menuItemId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(menuItemService.deleteMenuItem(menuItemId));
    }

    @DeleteMapping("/delete/{menuItemId}/{menuId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMenuItemFromMenu(@PathVariable String menuItemId, @PathVariable String menuId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(menuItemService.deleteMenuItemFromMenu(menuItemId, menuId));
    }

    @PutMapping("/update/{menuItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMenuItem(@PathVariable String menuItemId, @RequestBody @Validated MenuItemCreateDto menuItemCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(menuItemService.updateMenuItem(menuItemId, menuItemCreateDto));
    }

}
