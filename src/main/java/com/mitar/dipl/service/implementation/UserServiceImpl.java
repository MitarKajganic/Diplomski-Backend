package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.UserMapper;
import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Role;
import com.mitar.dipl.repository.ReservationRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.ReservationService;
import com.mitar.dipl.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private ReservationRepository reservationRepository;

    private UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);



    @Override
    public ResponseEntity<?> getAllUsers() {
        logger.info("Fetching all users.");
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getUserById(String userId) {
        UUID userUuid = UUIDUtils.parseUUID(userId);
        Optional<User> user = userRepository.findById(userUuid);
        if (user.isEmpty()) {
            logger.warn("User not found with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(user.get()));
    }

    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            logger.warn("User not found with email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(user.get()));
    }

    @Override
    public ResponseEntity<?> createUser(UserCreateDto userCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(userRepository.save(userMapper.toEntity(userCreateDto))));
    }

    @Override
    public ResponseEntity<?> deleteUser(String userId) {
        UUID userUuid = UUIDUtils.parseUUID(userId);
        Optional<User> optionalUser = userRepository.findById(userUuid);
        if (optionalUser.isEmpty()) {
            logger.warn("User not found with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = optionalUser.get();
        user.getReservations().forEach(reservation -> {
            reservation.setUser(null);
            reservation.setDeleted(true);
            reservationRepository.save(reservation);
        });

        userRepository.delete(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully.");
    }

    @Override
    public ResponseEntity<?> disableUser(String userId) {
        UUID userUuid = UUIDUtils.parseUUID(userId);
        Optional<User> user = userRepository.findById(userUuid);
        if (user.isEmpty()) {
            logger.warn("User not found with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        user.get().setActive(false);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(userRepository.save(user.get())));
    }

    @Override
    public ResponseEntity<?> updateUser(String userId, UserCreateDto userCreateDto) {
        UUID userUuid = UUIDUtils.parseUUID(userId);
        Optional<User> optionalUser = userRepository.findById(userUuid);
        if (optionalUser.isEmpty()) {
            logger.warn("User not found with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User existingUser = optionalUser.get();

        User byEmail = userRepository.findByEmail(userCreateDto.getEmail()).orElse(null);
        if (byEmail != null && !byEmail.getId().equals(existingUser.getId())) {
            logger.warn("User with email {} already exists.", userCreateDto.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with email already exists.");
        }

        existingUser.setEmail(userCreateDto.getEmail());
        existingUser.setHashPassword(userCreateDto.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(userRepository.save(existingUser)));
    }
}
