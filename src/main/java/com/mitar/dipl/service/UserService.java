package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.model.dto.user.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(String userId);

    UserDto getUserByEmail(String email);

    UserDto createUser(UserCreateDto userCreateDto);

    String deleteUser(String userId);

    UserDto disableUser(String userId);

    UserDto updateUser(String userId, UserCreateDto userCreateDto);

}
