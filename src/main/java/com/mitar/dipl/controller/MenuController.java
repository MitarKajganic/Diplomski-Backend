package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.security.CheckSecurity;
import com.mitar.dipl.service.MenuService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;


    @GetMapping("/all")
    public ResponseEntity<?> getAllMenus() {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.getAllMenus());
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<?> getMenuById(@PathVariable String menuId) {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.getMenuById(menuId));
    }

    @GetMapping("/name/{menuName}")
    public ResponseEntity<?> getMenuByMenuName(@PathVariable String menuName) {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.getMenuByMenuName(menuName));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMenu(@RequestBody @Validated MenuCreateDto menuCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(menuCreateDto));
    }

    @DeleteMapping("/delete/{menuId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMenu(@PathVariable String menuId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(menuService.deleteMenu(menuId));
    }

    @PutMapping("/update/{menuId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMenu(@PathVariable String menuId, @RequestBody @Validated MenuCreateDto menuCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.updateMenu(menuId, menuCreateDto));
    }

}
