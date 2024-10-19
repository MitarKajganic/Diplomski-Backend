package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.MenuItemMapper;
import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.MenuRepository;
import com.mitar.dipl.repository.OrderItemRepository;
import com.mitar.dipl.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemServiceImpl.class);

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    public ResponseEntity<?> getMenuItems() {
        logger.info("Fetching all MenuItems.");
        return ResponseEntity.ok(menuItemRepository.findAll().stream()
                .map(menuItemMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getMenuItemByName(String name) {
        Optional<MenuItem> menuItemOpt = menuItemRepository.findByName(name);
        if (menuItemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found.");
        }
        MenuItemDto dto = menuItemMapper.toDto(menuItemOpt.get());
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> getMenuItemsByNameContaining(String name) {
        List<MenuItem> menuItems = menuItemRepository.findAllByNameContainingIgnoreCase(name);
        if (menuItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No MenuItems found containing: " + name);
        }
        return ResponseEntity.ok(menuItems.stream()
                .map(menuItemMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getMenuItemById(String id) {
        UUID parsedId = UUIDUtils.parseUUID(id);

        Optional<MenuItem> menuItemOpt = menuItemRepository.findById(parsedId);
        if (menuItemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found.");
        }
        return ResponseEntity.ok(menuItemMapper.toDto(menuItemOpt.get()));
    }

    @Override
    public ResponseEntity<?> getMenuItemsByCategory(String category) {
        return ResponseEntity.ok(menuItemRepository.findAllByCategory(category).stream()
                .map(menuItemMapper::toDto)
                .toList());
    }

    @Override
    public ResponseEntity<?> createMenuItem(MenuItemCreateDto menuItemCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                menuItemMapper.toDto(menuItemRepository.save(menuItemMapper.toEntity(menuItemCreateDto))));
    }

    @Override
    public ResponseEntity<?> deleteMenuItem(String id) {
        UUID parsedId = UUIDUtils.parseUUID(id);

        Optional<MenuItem> menuItemOpt = menuItemRepository.findById(parsedId);
        if (menuItemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found.");
        }

        MenuItem menuItem = menuItemOpt.get();

        boolean isReferenced = orderItemRepository.existsByMenuItem(menuItem);
        if (isReferenced) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot delete MenuItem '" + menuItem.getName() + "' as it is associated with existing orders.");
        }

        Menu menu = menuItem.getMenu();
        if (menu != null)
            menu.removeMenuItem(menuItem);

        menuItemRepository.delete(menuItem);
        return ResponseEntity.ok("MenuItem deleted successfully.");
    }

    @Override
    public ResponseEntity<?> deleteMenuItemFromMenu(String menuItemId, String menuId) {
        UUID parsedMenuItemId = UUIDUtils.parseUUID(menuItemId);
        UUID parsedMenuId = UUIDUtils.parseUUID(menuId);

        Optional<MenuItem> menuItemOpt = menuItemRepository.findById(parsedMenuItemId);
        if (menuItemOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found.");


        MenuItem menuItem = menuItemOpt.get();

        Optional<Menu> menuOpt = menuRepository.findById(parsedMenuId);
        if (menuOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu not found.");
        }

        Menu menu = menuOpt.get();

        if (!menu.getItems().contains(menuItem)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("MenuItem is not associated with the specified Menu.");
        }

        menu.removeMenuItem(menuItem);

        return ResponseEntity.ok("MenuItem removed from Menu successfully.");
    }

    @Override
    public ResponseEntity<?> updateMenuItem(String id, MenuItemCreateDto menuItemCreateDto) {
        UUID parsedId = UUIDUtils.parseUUID(id);

        Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(parsedId);
        if (optionalMenuItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found.");
        }

        MenuItem menuItem = optionalMenuItem.get();

        menuItem.setName(menuItemCreateDto.getName());
        menuItem.setDescription(menuItemCreateDto.getDescription());
        menuItem.setPrice(menuItemCreateDto.getPrice());
        menuItem.setCategory(menuItemCreateDto.getCategory());

        UUID newMenuId = UUIDUtils.parseUUID(menuItemCreateDto.getMenuId());

        Menu currentMenu = menuItem.getMenu();
        if (currentMenu == null || !currentMenu.getId().equals(newMenuId)) {
            Optional<Menu> newMenuOpt = menuRepository.findById(newMenuId);
            if (newMenuOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("New Menu not found.");
            }

            Menu newMenu = newMenuOpt.get();

            if (currentMenu != null) {
                currentMenu.removeMenuItem(menuItem);
            }

            newMenu.addMenuItem(menuItem);
        }

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        MenuItemDto dto = menuItemMapper.toDto(updatedMenuItem);
        return ResponseEntity.ok(dto);
    }
}
