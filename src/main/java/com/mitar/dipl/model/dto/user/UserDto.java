package com.mitar.dipl.model.dto.user;

import com.mitar.dipl.model.entity.Reservation;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {

    private String id;
    private String email;
    private String role;
    private Set<Reservation> reservations;

}
