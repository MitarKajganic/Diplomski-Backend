package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("@securityUtils.isUserByUserId(#userId) or hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("@securityUtils.isUserByEmail(#email) or hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> getUserByEmail(@PathVariable @Email String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByEmail(email));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Validated UserCreateDto userCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userCreateDto));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userService.deleteUser(userId));
    }

    @PutMapping("/disable/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableUser(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.disableUser(userId));
    }

    @PutMapping("/enable/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableUser(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.enableUser(userId));
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("@securityUtils.isUserByUserId(#userId) or hasAnyRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody @Validated UserCreateDto userCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, userCreateDto));
    }

}
