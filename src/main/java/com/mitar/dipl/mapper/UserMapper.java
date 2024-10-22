package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.model.dto.user.UserDto;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserMapper {

    private ReservationMapper reservationMapper;

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId().toString());
        userDto.setEmail(user.getEmail());
        userDto.setReservations(user.getReservations().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toSet()));
        userDto.setRole(user.getRole().name());
        userDto.setActive(user.getActive());

        return userDto;
    }

    public User toEntity(UserCreateDto userCreateDto) {
        User user = new User();

        user.setEmail(userCreateDto.getEmail());
        user.setHashPassword(userCreateDto.getPassword());
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        return user;
    }

}
