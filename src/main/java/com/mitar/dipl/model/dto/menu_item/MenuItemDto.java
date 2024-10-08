package com.mitar.dipl.model.dto.menu_item;

import lombok.Data;

@Data
public class MenuItemDto {

    private String id;
    private String name;
    private String description;
    private String price;
    private String category;
    private String menuId;

}
