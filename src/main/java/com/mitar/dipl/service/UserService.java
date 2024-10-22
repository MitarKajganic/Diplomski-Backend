package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.model.dto.user.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    /**
     * Fetches all users.
     *
     * @return List of UserDto
     */
    List<UserDto> getAllUsers();

    /**
     * Fetches a user by their ID.
     *
     * @param userId The UUID of the user as a string.
     * @return UserDto
     */
    UserDto getUserById(String userId);

    /**
     * Fetches a user by their email.
     *
     * @param email The email of the user.
     * @return UserDto
     */
    UserDto getUserByEmail(String email);

    /**
     * Creates a new user.
     *
     * @param userCreateDto The DTO containing user creation data.
     * @return UserDto
     */
    UserDto createUser(UserCreateDto userCreateDto);

    /**
     * Deletes a user by their ID.
     *
     * @param userId The UUID of the user as a string.
     * @return Success message.
     */
    String deleteUser(String userId);

    /**
     * Disables a user by their ID.
     *
     * @param userId The UUID of the user as a string.
     * @return UserDto with updated status.
     */
    UserDto disableUser(String userId);

    /**
     * Updates an existing user.
     *
     * @param userId         The UUID of the user as a string.
     * @param userCreateDto The DTO containing updated user data.
     * @return UserDto
     */
    UserDto updateUser(String userId, UserCreateDto userCreateDto);

}
