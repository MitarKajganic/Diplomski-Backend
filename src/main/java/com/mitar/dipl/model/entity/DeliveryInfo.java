package com.mitar.dipl.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryInfo {

    private String firstName;
    private String lastName;
    private String street;
    private String number;
    private Integer floor;
    private String phoneNumber;

}
