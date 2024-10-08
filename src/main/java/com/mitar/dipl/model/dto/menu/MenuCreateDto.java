package com.mitar.dipl.model.dto.menu;

import com.mitar.dipl.model.entity.MenuItem;
import lombok.Data;

import java.util.Set;

@Data
public class MenuCreateDto {

    private String name;
    private Set<MenuItem> items;

}
