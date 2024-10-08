package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.MenuItemMapper;
import com.mitar.dipl.model.dto.menu_item.MenuItemCreateDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.MenuRepository;
import com.mitar.dipl.service.MenuItemService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class MenuItemServiceImpl implements MenuItemService {

    private MenuItemRepository menuItemRepository;

    private MenuRepository menuRepository;

    private MenuItemMapper menuItemMapper;


    @Override
    public ResponseEntity<?> getMenuItems() {
        return ResponseEntity.status(HttpStatus.OK).body(
                menuItemRepository
                        .findAll()
                        .stream()
                        .map(menuItemMapper::toDto)
        );
    }

    @Override
    public ResponseEntity<?> getMenuItemByName(String name) {
        Optional<MenuItem> menuItem = menuItemRepository.findByName(name);
        if (menuItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(menuItemMapper.toDto(menuItem.get()));
    }

    @Override
    public ResponseEntity<?> getMenuItemById(String id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(UUID.fromString(id));
        if (menuItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(menuItemMapper.toDto(menuItem.get()));
    }

    @Override
    public ResponseEntity<?> getMenuItemsByMenu(String menuId) {
        Optional<Menu> menu = menuRepository.findById(UUID.fromString(menuId));
        if (menu.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(
                menuItemRepository
                        .findAllByMenu(menu.get())
                        .stream()
                        .map(menuItemMapper::toDto)
        );
    }

    @Override
    public ResponseEntity<?> getMenuItemsByCategory(String category) {
        return ResponseEntity.status(HttpStatus.OK).body(
                menuItemRepository
                        .findAllByCategory(category)
                        .stream()
                        .map(menuItemMapper::toDto)
        );
    }

    @Override
    public ResponseEntity<?> createMenuItem(MenuItemCreateDto menuItemCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                menuItemMapper.toDto(menuItemRepository.save(menuItemMapper.toEntity(menuItemCreateDto)))
        );
    }

    @Override
    public ResponseEntity<?> deleteMenuItem(String id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(UUID.fromString(id));
        if (menuItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        menuItemRepository.delete(menuItem.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> updateMenuItem(String id, MenuItemCreateDto menuItemCreateDto) {
        Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(UUID.fromString(id));
        if (optionalMenuItem.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        MenuItem menuItem = optionalMenuItem.get();

        menuItem.setName(menuItemCreateDto.getName());
        menuItem.setDescription(menuItemCreateDto.getDescription());
        menuItem.setPrice(new BigDecimal(menuItemCreateDto.getPrice()));
        menuItem.setCategory(menuItemCreateDto.getCategory());
        Optional<Menu> menu = menuRepository.findById(UUID.fromString(menuItemCreateDto.getMenuId()));
        menu.ifPresent(menuItem::setMenu);

        return ResponseEntity.status(HttpStatus.OK).body(menuItemMapper.toDto(menuItemRepository.save(menuItem)));
    }

}
