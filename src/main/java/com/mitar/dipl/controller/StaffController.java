package com.mitar.dipl.controller;


import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.service.StaffService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<?> getStaffById(@PathVariable String staffId) {
        return staffService.getStaffById(staffId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getStaffByUserId(@PathVariable String userId) {
        return staffService.getStaffByUserId(userId);
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<?> getStaffByPosition(@PathVariable String position) {
        if (!position.equals("WAITER") && !position.equals("COOK") && !position.equals("BARTENDER") && !position.equals("MANAGER"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid position.");
        return staffService.getStaffByPosition(position);
    }

    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody @Validated StaffCreateDto staffCreateDto) {
        return staffService.createStaff(staffCreateDto);
    }

    @DeleteMapping("/delete/{staffId}")
    public ResponseEntity<?> deleteStaff(@PathVariable String staffId) {
        return staffService.deleteStaff(staffId);
    }

    @PutMapping("/update/{staffId}")
    public ResponseEntity<?> updateStaff(@PathVariable String staffId, @RequestBody @Validated StaffCreateDto staffCreateDto) {
        return staffService.updateStaff(staffId, staffCreateDto);
    }

}
