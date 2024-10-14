package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.UserMapper;
import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Role;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

    private UserMapper userMapper;


    @Override
    public ResponseEntity<?> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(9);

        if (!user.get().getPassword().equals(bCryptPasswordEncoder.encode(password)))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);



        long expirationTime = 18 * 60 * 60 * 1000;

//        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponseDto(tokenService.generate(claims, expirationTime)));
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getUserById(String userId) {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(user.get());
    }

    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(user.get());
    }

    @Override
    public ResponseEntity<?> createUser(UserCreateDto userCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(userMapper.toEntity(userCreateDto)));
    }

    @Override
    public ResponseEntity<?> createStaff(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        user.setRole(Role.STAFF);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }

    @Override
    public ResponseEntity<?> createAdmin(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        user.setRole(Role.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }

    @Override
    public ResponseEntity<?> deleteUser(String userId) {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        userRepository.delete(user.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> disableUser(String userId) {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        user.get().setActive(false);
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(user.get()));
    }

    @Override
    public ResponseEntity<?> updateUser(String userId, UserCreateDto userCreateDto) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
        if (optionalUser.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        User existingUser = optionalUser.get();

        existingUser.setEmail(userCreateDto.getEmail());
        existingUser.setHashPassword(userCreateDto.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(existingUser));
    }
}
