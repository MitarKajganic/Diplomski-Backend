package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.MenuMapper;
import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.MenuRepository;
import com.mitar.dipl.repository.OrderItemRepository;
import com.mitar.dipl.service.MenuService;
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
public class MenuServiceImpl implements MenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public ResponseEntity<?> getAllMenus() {
        logger.info("Fetching all menus.");
        return ResponseEntity.ok(menuRepository.findAll().stream()
                .map(menuMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getMenuById(String menuId) {
        UUID parsedMenuId = UUIDUtils.parseUUID(menuId);

        Optional<Menu> menuOpt = menuRepository.findById(parsedMenuId);
        if (menuOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu not found.");
        }

        MenuDto dto = menuMapper.toDto(menuOpt.get());
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> getMenuByMenuName(String menuName) {
        Optional<Menu> menuOpt = menuRepository.findByName(menuName);
        if (menuOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu not found.");
        }

        MenuDto dto = menuMapper.toDto(menuOpt.get());
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> createMenu(MenuCreateDto menuCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                menuMapper.toDto(menuRepository.save(menuMapper.toEntity(menuCreateDto))));
    }

    @Override
    public ResponseEntity<?> deleteMenu(String menuId) {
        UUID parsedMenuId = UUIDUtils.parseUUID(menuId);

        Optional<Menu> menuOpt = menuRepository.findById(parsedMenuId);
        if (menuOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu not found.");
        }

        Menu menu = menuOpt.get();

        Set<MenuItem> itemsCopy = new HashSet<>(menu.getItems());
        for (MenuItem item : itemsCopy)
            menu.removeMenuItem(item);


        menuRepository.delete(menu);
        return ResponseEntity.ok("Menu deleted successfully.");
    }

    @Override
    public ResponseEntity<?> updateMenu(String menuId, MenuCreateDto menuCreateDto) {
        UUID parsedMenuId = UUIDUtils.parseUUID(menuId);

        Optional<Menu> optionalMenu = menuRepository.findById(parsedMenuId);
        if (optionalMenu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu not found.");
        }

        Menu menu = optionalMenu.get();

        menu.setName(menuCreateDto.getName());

        List<UUID> menuItemIds = menuCreateDto.getItemIds().stream()
                .map(UUIDUtils::parseUUID)
                .toList();

        List<MenuItem> newMenuItems = menuItemRepository.findAllById(menuItemIds);

        if (newMenuItems.size() != menuItemIds.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One or more MenuItem IDs do not exist.");
        }

        Set<MenuItem> currentItems = new HashSet<>(menu.getItems());
        Set<MenuItem> itemsToRemove = new HashSet<>(currentItems);
        newMenuItems.forEach(itemsToRemove::remove);

        Set<MenuItem> itemsToAdd = new HashSet<>(newMenuItems);
        itemsToAdd.removeAll(currentItems);

        for (MenuItem item : itemsToRemove)
            menu.removeMenuItem(item);


        for (MenuItem item : itemsToAdd)
            menu.addMenuItem(item);

        return ResponseEntity.status(HttpStatus.OK).body(menuMapper.toDto(menuRepository.save(menu)));
    }
}
