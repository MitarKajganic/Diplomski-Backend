package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.MenuMapper;
import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.dto.menu.MenuDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.MenuRepository;
import com.mitar.dipl.service.MenuService;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final MenuItemRepository menuItemRepository;

    /**
     * Fetches all menus.
     *
     * @return List of MenuDto
     */
    @Override
    public List<MenuDto> getAllMenus() {
        log.info("Fetching all menus.");
        List<MenuDto> menuDtos = menuRepository.findAll().stream()
                .map(menuMapper::toDto)
                .toList();
        log.info("Fetched {} menus.", menuDtos.size());
        return menuDtos;
    }

    /**
     * Fetches a menu by its ID.
     *
     * @param menuId The UUID of the menu as a string.
     * @return MenuDto
     */
    @Override
    public MenuDto getMenuById(String menuId) {
        UUID parsedMenuId = UUIDUtils.parseUUID(menuId);
        log.debug("Fetching Menu with ID: {}", parsedMenuId);

        Menu menu = menuRepository.findById(parsedMenuId)
                .orElseThrow(() -> {
                    log.warn("Menu not found with ID: {}", menuId);
                    return new ResourceNotFoundException("Menu not found with ID: " + menuId);
                });

        MenuDto menuDto = menuMapper.toDto(menu);
        log.info("Fetched Menu: {}", menuDto);
        return menuDto;
    }

    /**
     * Fetches a menu by its name.
     *
     * @param menuName The name of the menu.
     * @return MenuDto
     */
    @Override
    public MenuDto getMenuByMenuName(String menuName) {
        log.debug("Fetching Menu with name: {}", menuName);
        Menu menu = menuRepository.findByName(menuName)
                .orElseThrow(() -> {
                    log.warn("Menu not found with name: {}", menuName);
                    return new ResourceNotFoundException("Menu not found with name: " + menuName);
                });

        MenuDto menuDto = menuMapper.toDto(menu);
        log.info("Fetched Menu: {}", menuDto);
        return menuDto;
    }

    /**
     * Creates a new menu.
     *
     * @param menuCreateDto The DTO containing menu creation data.
     * @return MenuDto
     */
    @Override
    public MenuDto createMenu(MenuCreateDto menuCreateDto) {
        String menuName = menuCreateDto.getName();
        log.debug("Attempting to create Menu with name: {}", menuName);

        if (menuRepository.findByName(menuName).isPresent()) {
            log.warn("Menu already exists with name: {}", menuName);
            throw new BadRequestException("Menu already exists with name: " + menuName);
        }

        Menu menu = menuMapper.toEntity(menuCreateDto);

        if (menuCreateDto.getItemIds() != null && !menuCreateDto.getItemIds().isEmpty()) {
            List<UUID> menuItemIds = menuCreateDto.getItemIds().stream()
                    .map(UUIDUtils::parseUUID)
                    .toList();

            List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIds);
            if (menuItems.size() != menuItemIds.size()) {
                log.warn("One or more MenuItem IDs do not exist.");
                throw new BadRequestException("One or more MenuItem IDs do not exist.");
            }

            for (MenuItem item : menuItems) {
                menu.addMenuItem(item);
            }
        }

        Menu savedMenu = menuRepository.save(menu);
        MenuDto menuDto = menuMapper.toDto(savedMenu);

        log.info("Menu created successfully with ID: {}", savedMenu.getId());
        return menuDto;
    }

    /**
     * Deletes a menu by its ID.
     *
     * @param menuId The UUID of the menu as a string.
     * @return Success message.
     */
    @Override
    public String deleteMenu(String menuId) {
        UUID parsedMenuId = UUIDUtils.parseUUID(menuId);
        log.debug("Attempting to delete Menu with ID: {}", parsedMenuId);

        Menu menu = menuRepository.findById(parsedMenuId)
                .orElseThrow(() -> {
                    log.warn("Menu not found with ID: {}", menuId);
                    return new ResourceNotFoundException("Menu not found.");
                });

        Set<MenuItem> itemsCopy = new HashSet<>(menu.getItems());
        for (MenuItem item : itemsCopy) {
            menu.removeMenuItem(item);
        }

        menuRepository.delete(menu);
        log.info("Menu deleted successfully with ID: {}", menuId);
        return "Menu deleted successfully.";
    }

    /**
     * Updates an existing menu.
     *
     * @param menuId         The UUID of the menu as a string.
     * @param menuCreateDto The DTO containing updated menu data.
     * @return MenuDto
     */
    @Override
    public MenuDto updateMenu(String menuId, MenuCreateDto menuCreateDto) {
        UUID parsedMenuId = UUIDUtils.parseUUID(menuId);
        log.debug("Attempting to update Menu with ID: {}", parsedMenuId);

        Menu menu = menuRepository.findById(parsedMenuId)
                .orElseThrow(() -> {
                    log.warn("Menu not found with ID: {}", menuId);
                    return new ResourceNotFoundException("Menu not found.");
                });

        String newMenuName = menuCreateDto.getName();
        if (!menu.getName().equalsIgnoreCase(newMenuName)) {
            if (menuRepository.findByName(newMenuName).isPresent()) {
                log.warn("Another Menu already exists with name: {}", newMenuName);
                throw new BadRequestException("Another Menu already exists with name: " + newMenuName);
            }
            menu.setName(newMenuName);
            log.debug("Updated Menu name to: {}", newMenuName);
        }

        if (menuCreateDto.getItemIds() != null) {
            List<UUID> newMenuItemIds = menuCreateDto.getItemIds().stream()
                    .map(UUIDUtils::parseUUID)
                    .collect(Collectors.toList());

            List<MenuItem> newMenuItems = menuItemRepository.findAllById(newMenuItemIds);

            if (newMenuItems.size() != newMenuItemIds.size()) {
                log.warn("One or more MenuItem IDs do not exist.");
                throw new BadRequestException("One or more MenuItem IDs do not exist.");
            }

            Set<MenuItem> currentMenuItems = new HashSet<>(menu.getItems());

            Set<MenuItem> itemsToRemove = new HashSet<>(currentMenuItems);
            newMenuItems.forEach(itemsToRemove::remove);

            Set<MenuItem> itemsToAdd = new HashSet<>(newMenuItems);
            currentMenuItems.forEach(itemsToAdd::remove);

            for (MenuItem item : itemsToRemove) {
                menu.removeMenuItem(item);
                log.debug("Removed MenuItem '{}' from Menu '{}'.", item.getName(), menu.getName());
            }

            for (MenuItem item : itemsToAdd) {
                menu.addMenuItem(item);
                log.debug("Added MenuItem '{}' to Menu '{}'.", item.getName(), menu.getName());
            }
        }

        MenuDto menuDto = menuMapper.toDto(menuRepository.save(menu));

        log.info("Menu updated successfully with ID: {}", menuId);
        return menuDto;
    }
}
