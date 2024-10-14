package com.mitar.dipl.controller;

import com.mitar.dipl.model.dto.user.UserCreateDto;
import com.mitar.dipl.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Validated UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @PostMapping("/createStaff")
    public ResponseEntity<?> createStaff(@RequestBody @Validated UserCreateDto userCreateDto) {
        return userService.createStaff(userCreateDto);
    }

    @PostMapping("/createAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody @Validated UserCreateDto userCreateDto) {
        return userService.createAdmin(userCreateDto);
    }

    //login

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody @Validated UserCreateDto userCreateDto) {
        return userService.updateUser(userId, userCreateDto);
    }

    @PutMapping("/disable/{userId}")
    public ResponseEntity<?> disableUser(@PathVariable String userId) {
        return userService.disableUser(userId);
    }
}
