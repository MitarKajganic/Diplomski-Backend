package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.user.UserCreateDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> getAllUsers();

    ResponseEntity<?> getUserById(String userId);

    ResponseEntity<?> getUserByEmail(String email);

    ResponseEntity<?> createUser(UserCreateDto userCreateDto);

    ResponseEntity<?> createStaff(UserCreateDto userCreateDto);

    ResponseEntity<?> createAdmin(UserCreateDto userCreateDto);

    ResponseEntity<?> deleteUser(String userId);

    ResponseEntity<?> disableUser(String userId);

    ResponseEntity<?> updateUser(String userId, UserCreateDto userCreateDto);
}
