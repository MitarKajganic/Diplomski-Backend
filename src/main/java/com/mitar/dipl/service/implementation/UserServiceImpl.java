package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ConflictException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.UserMapper;
import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.model.dto.user.UserDto;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.repository.ReservationRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.UserService;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final UserMapper userMapper;


    @Override
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users.");
        List<UserDto> userDtos = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
        log.info("Fetched {} users.", userDtos.size());
        return userDtos;
    }

    @Override
    public UserDto getUserById(String userId) {
        UUID parsedUserId = UUIDUtils.parseUUID(userId);
        log.debug("Fetching User with ID: {}", parsedUserId);

        User user = userRepository.findById(parsedUserId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        UserDto userDto = userMapper.toDto(user);
        log.info("Retrieved User ID: {}", userId);
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        log.debug("Fetching User with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        UserDto userDto = userMapper.toDto(user);
        log.info("Retrieved User with email: {}", email);
        return userDto;
    }

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        log.info("Attempting to create user with email: {}", userCreateDto.getEmail());

        if (userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
            log.warn("Email already in use: {}", userCreateDto.getEmail());
            throw new ConflictException("Email already in use.");
        }

        User userEntity = userMapper.toEntity(userCreateDto);

        User savedUser = userRepository.save(userEntity);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Override
    public String deleteUser(String userId) {
        UUID userUuid = UUIDUtils.parseUUID(userId);
        log.debug("Attempting to delete User with ID: {}", userUuid);

        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        user.getReservations().forEach(reservation -> {
            reservation.setUser(null);
            reservation.setDeleted(true);
            reservationRepository.save(reservation);
            log.debug("Soft-deleted Reservation ID: {} associated with User ID: {}", reservation.getId(), userId);
        });

        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", userId);
        return "User deleted successfully.";
    }

    @Override
    public UserDto disableUser(String userId) {
        UUID userUuid = UUIDUtils.parseUUID(userId);
        log.debug("Attempting to disable User with ID: {}", userUuid);

        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        if (!user.getActive()) {
            log.warn("User with ID: {} is already disabled.", userId);
            throw new BadRequestException("User is already disabled.");
        }

        user.setActive(false);
        User updatedUser = userRepository.save(user);
        log.info("User disabled successfully with ID: {}", userId);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto enableUser(String userId) {
        UUID userUuid = UUIDUtils.parseUUID(userId);
        log.debug("Attempting to enable User with ID: {}", userUuid);

        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        if (user.getActive()) {
            log.warn("User with ID: {} is already enabled.", userId);
            throw new BadRequestException("User is already enabled.");
        }

        user.setActive(true);
        User updatedUser = userRepository.save(user);
        log.info("User enabled successfully with ID: {}", userId);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto updateUser(String userId, UserCreateDto userCreateDto) {
        UUID userUuid = UUIDUtils.parseUUID(userId);
        log.debug("Attempting to update User with ID: {}", userUuid);

        User existingUser = userRepository.findById(userUuid)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        User userWithEmail = userRepository.findByEmail(userCreateDto.getEmail()).orElse(null);
        if (userWithEmail != null && !userWithEmail.getId().equals(existingUser.getId())) {
            log.warn("User with email {} already exists.", userCreateDto.getEmail());
            throw new ConflictException("User with email already exists.");
        }
        existingUser.setEmail(userCreateDto.getEmail());

        existingUser.setHashPassword(userCreateDto.getPassword());
        log.debug("Updated password for User ID: {}", userId);

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID: {}", userId);

        return userMapper.toDto(updatedUser);
    }
}
