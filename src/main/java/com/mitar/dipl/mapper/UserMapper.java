package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.model.dto.user.UserDto;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId().toString());
        userDto.setEmail(user.getEmail());
        userDto.setReservations(user.getReservations());
        userDto.setRole(user.getRole());
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
