package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.MenuItemMapper;
import com.mitar.dipl.mapper.MenuMapper;
import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.model.entity.MenuItem;
import com.mitar.dipl.repository.MenuItemRepository;
import com.mitar.dipl.repository.MenuRepository;
import com.mitar.dipl.service.MenuService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

    private MenuRepository menuRepository;

    private MenuMapper menuMapper;

    private MenuItemMapper menuItemMapper;

    private MenuItemRepository menuItemRepository;

    @Override
    public ResponseEntity<?> getAllMenus() {
        return ResponseEntity.status(HttpStatus.OK).body(menuRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getMenuById(String menuId) {
        Optional<Menu> menu = menuRepository.findById(UUID.fromString(menuId));
        if (menu.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(menu.get());
    }

    @Override
    public ResponseEntity<?> getMenuByMenuName(String menuName) {
        Optional<Menu> menu = menuRepository.findByName(menuName);
        if (menu.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(menu.get());
    }

    @Override
    public ResponseEntity<?> createMenu(MenuCreateDto menuCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuRepository.save(menuMapper.toEntity(menuCreateDto)));
    }

    @Override
    public ResponseEntity<?> deleteMenu(String menuId) {
        Optional<Menu> menu = menuRepository.findById(UUID.fromString(menuId));
        if (menu.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        menuRepository.delete(menu.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> updateMenu(String menuId, MenuCreateDto menuCreateDto) {
        Optional<Menu> optionalMenu = menuRepository.findById(UUID.fromString(menuId));
        if (optionalMenu.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        Menu menu = optionalMenu.get();

        menu.setName(menuCreateDto.getName());
        menu.getItems().clear();

        List<UUID> menuItemIds = menuCreateDto.getItemIds().stream()
                .map(UUID::fromString)
                .toList();

        List<MenuItem> menuItems = menuItemIds.stream()
                .map(menuItemRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        menu.setItems(new HashSet<>(menuItems));

        return ResponseEntity.status(HttpStatus.OK).body(menuMapper.toDto(menuRepository.save(menu)));
    }

}
