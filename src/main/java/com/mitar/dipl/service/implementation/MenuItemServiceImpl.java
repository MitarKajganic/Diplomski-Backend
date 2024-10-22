package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.MenuItemMapper;
import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import com.mitar.dipl.model.dto.menu_item.MenuItemDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.MenuRepository;
import com.mitar.dipl.repository.OrderItemRepository;
import com.mitar.dipl.service.MenuItemService;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemMapper menuItemMapper;


    @Override
    public List<MenuItemDto> getMenuItems() {
        log.info("Fetching all MenuItems.");
        List<MenuItemDto> menuItemDtos = menuItemRepository.findAll().stream()
                .map(menuItemMapper::toDto)
                .toList();
        log.info("Fetched {} MenuItems.", menuItemDtos.size());
        return menuItemDtos;
    }

    @Override
    public MenuItemDto getMenuItemByName(String name) {
        log.debug("Fetching MenuItem with name: {}", name);
        MenuItem menuItem = menuItemRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("MenuItem not found with name: {}", name);
                    return new ResourceNotFoundException("MenuItem not found with name: " + name);
                });
        MenuItemDto menuItemDto = menuItemMapper.toDto(menuItem);
        log.info("Fetched MenuItem: {}", menuItemDto);
        return menuItemDto;
    }

    @Override
    public List<MenuItemDto> getMenuItemsByNameContaining(String name) {
        log.debug("Fetching MenuItems containing name: {}", name);
        List<MenuItem> menuItems = menuItemRepository.findAllByNameContainingIgnoreCase(name);
        if (menuItems.isEmpty()) {
            log.warn("No MenuItems found containing: {}", name);
            throw new ResourceNotFoundException("No MenuItems found containing: " + name);
        }
        List<MenuItemDto> menuItemDtos = menuItems.stream()
                .map(menuItemMapper::toDto)
                .toList();
        log.info("Fetched {} MenuItems containing: {}", menuItemDtos.size(), name);
        return menuItemDtos;
    }

    @Override
    public MenuItemDto getMenuItemById(String id) {
        UUID parsedId = UUIDUtils.parseUUID(id);
        log.debug("Fetching MenuItem with ID: {}", parsedId);

        MenuItem menuItem = menuItemRepository.findById(parsedId)
                .orElseThrow(() -> {
                    log.warn("MenuItem not found with ID: {}", id);
                    return new ResourceNotFoundException("MenuItem not found with ID: " + id);
                });

        MenuItemDto menuItemDto = menuItemMapper.toDto(menuItem);
        log.info("Fetched MenuItem: {}", menuItemDto);
        return menuItemDto;
    }

    @Override
    public List<MenuItemDto> getMenuItemsByCategory(String category) {
        log.debug("Fetching MenuItems by category: {}", category);
        List<MenuItem> menuItems = menuItemRepository.findAllByCategory(category);
        if (menuItems.isEmpty()) {
            log.warn("No MenuItems found in category: {}", category);
            throw new ResourceNotFoundException("No MenuItems found in category: " + category);
        }
        List<MenuItemDto> menuItemDtos = menuItems.stream()
                .map(menuItemMapper::toDto)
                .toList();
        log.info("Fetched {} MenuItems in category: {}", menuItemDtos.size(), category);
        return menuItemDtos;
    }

    @Override
    public MenuItemDto createMenuItem(MenuItemCreateDto menuItemCreateDto) {
        String itemName = menuItemCreateDto.getName();
        log.debug("Attempting to create MenuItem with name: {}", itemName);

        if (menuItemRepository.findByName(itemName).isPresent()) {
            log.warn("MenuItem already exists with name: {}", itemName);
            throw new BadRequestException("MenuItem already exists with name: " + itemName);
        }

        UUID menuId = UUIDUtils.parseUUID(menuItemCreateDto.getMenuId());
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> {
                    log.warn("Menu not found with ID: {}", menuId);
                    return new ResourceNotFoundException("Menu not found with ID: " + menuId);
                });

        MenuItem menuItem = menuItemMapper.toEntity(menuItemCreateDto);
        menuItem.setMenu(menu);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        menu.addMenuItem(savedMenuItem);

        MenuItemDto menuItemDto = menuItemMapper.toDto(savedMenuItem);
        log.info("MenuItem created successfully: {}", menuItemDto);
        return menuItemDto;
    }

    @Override
    public String deleteMenuItem(String id) {
        UUID parsedId = UUIDUtils.parseUUID(id);
        log.debug("Attempting to delete MenuItem with ID: {}", parsedId);

        MenuItem menuItem = menuItemRepository.findById(parsedId)
                .orElseThrow(() -> {
                    log.warn("MenuItem not found with ID: {}", id);
                    return new ResourceNotFoundException("MenuItem not found with ID: " + id);
                });

        boolean isReferenced = orderItemRepository.existsByMenuItem(menuItem);
        if (isReferenced) {
            log.warn("Cannot delete MenuItem '{}' as it is associated with existing orders.", menuItem.getName());
            throw new BadRequestException("Cannot delete MenuItem '" + menuItem.getName() + "' as it is associated with existing orders.");
        }

        Menu menu = menuItem.getMenu();
        if (menu != null) {
            menu.removeMenuItem(menuItem);
            log.debug("Removed MenuItem '{}' from Menu '{}'.", menuItem.getName(), menu.getName());
        }

        menuItemRepository.delete(menuItem);
        log.info("MenuItem deleted successfully with ID: {}", id);
        return "MenuItem deleted successfully.";
    }

    @Override
    public String deleteMenuItemFromMenu(String menuItemId, String menuId) {
        UUID parsedMenuItemId = UUIDUtils.parseUUID(menuItemId);
        UUID parsedMenuId = UUIDUtils.parseUUID(menuId);
        log.debug("Attempting to remove MenuItem with ID: {} from Menu with ID: {}", parsedMenuItemId, parsedMenuId);

        MenuItem menuItem = menuItemRepository.findById(parsedMenuItemId)
                .orElseThrow(() -> {
                    log.warn("MenuItem not found with ID: {}", menuItemId);
                    return new ResourceNotFoundException("MenuItem not found with ID: " + menuItemId);
                });

        Menu menu = menuRepository.findById(parsedMenuId)
                .orElseThrow(() -> {
                    log.warn("Menu not found with ID: {}", menuId);
                    return new ResourceNotFoundException("Menu not found with ID: " + menuId);
                });

        if (!menu.getItems().contains(menuItem)) {
            log.warn("MenuItem '{}' is not associated with Menu '{}'.", menuItem.getName(), menu.getName());
            throw new BadRequestException("MenuItem is not associated with the specified Menu.");
        }

        menu.removeMenuItem(menuItem);
        log.info("MenuItem '{}' removed from Menu '{}'.", menuItem.getName(), menu.getName());
        return "MenuItem removed from Menu successfully.";
    }

    @Override
    public MenuItemDto updateMenuItem(String id, MenuItemCreateDto menuItemCreateDto) {
        UUID parsedId = UUIDUtils.parseUUID(id);
        log.debug("Attempting to update MenuItem with ID: {}", parsedId);

        MenuItem menuItem = menuItemRepository.findById(parsedId)
                .orElseThrow(() -> {
                    log.warn("MenuItem not found with ID: {}", id);
                    return new ResourceNotFoundException("MenuItem not found with ID: " + id);
                });

        String newName = menuItemCreateDto.getName();
        if (!menuItem.getName().equalsIgnoreCase(newName)) {
            if (menuItemRepository.findByName(newName).isPresent()) {
                log.warn("Another MenuItem already exists with name: {}", newName);
                throw new BadRequestException("Another MenuItem already exists with name: " + newName);
            }
            menuItem.setName(newName);
            log.debug("Updated MenuItem name to: {}", newName);
        }

        menuItem.setDescription(menuItemCreateDto.getDescription());
        menuItem.setPrice(menuItemCreateDto.getPrice());
        menuItem.setCategory(menuItemCreateDto.getCategory());
        log.debug("Updated MenuItem fields: description, price, category.");

        UUID newMenuId = UUIDUtils.parseUUID(menuItemCreateDto.getMenuId());
        Menu newMenu = menuRepository.findById(newMenuId)
                .orElseThrow(() -> {
                    log.warn("Menu not found with ID: {}", newMenuId);
                    return new ResourceNotFoundException("New Menu not found with ID: " + newMenuId);
                });

        Menu currentMenu = menuItem.getMenu();
        if (currentMenu == null || !currentMenu.getId().equals(newMenuId)) {
            if (currentMenu != null) {
                currentMenu.removeMenuItem(menuItem);
                log.debug("Removed MenuItem '{}' from previous Menu '{}'.", menuItem.getName(), currentMenu.getName());
            }
            newMenu.addMenuItem(menuItem);
            log.debug("Added MenuItem '{}' to new Menu '{}'.", menuItem.getName(), newMenu.getName());
        }

        MenuItemDto menuItemDto = menuItemMapper.toDto(menuItemRepository.save(menuItem));
        log.info("MenuItem updated successfully: {}", menuItemDto);
        return menuItemDto;
    }
}
