package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.MenuMapper;
import com.mitar.dipl.model.dto.menu.MenuCreateDto;
import com.mitar.dipl.model.entity.Menu;
import com.mitar.dipl.repository.MenuRepository;
import com.mitar.dipl.service.MenuService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

    private MenuRepository menuRepository;

    private MenuMapper menuMapper;

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
    public ResponseEntity<?> deleteMenu(String menuId) {
        Optional<Menu> menu = menuRepository.findById(UUID.fromString(menuId));
        if (menu.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        menuRepository.delete(menu.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> createMenu(MenuCreateDto menuCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuRepository.save(menuMapper.toEntity(menuCreateDto)));
    }

    @Override
    public ResponseEntity<?> updateMenu(String menuId, MenuCreateDto menuCreateDto) {
        Optional<Menu> menu = menuRepository.findById(UUID.fromString(menuId));
        if (menu.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(menuRepository.save(menuMapper.toEntity(menuCreateDto)));
    }

}
