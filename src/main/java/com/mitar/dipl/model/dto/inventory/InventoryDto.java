package com.mitar.dipl.model.dto.inventory;

import lombok.Data;

@Data
public class InventoryDto {

    private String id;
    private String itemName;
    private Integer quantity;
    private String unit;
    private Boolean lowStock;

}


