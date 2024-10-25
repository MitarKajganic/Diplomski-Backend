package com.mitar.dipl.controller;


import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.dto.staff.StaffUpdateDto;
import com.mitar.dipl.service.StaffService;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.status(HttpStatus.OK).body(staffService.getAllStaff());
    }

    @GetMapping("/{staffId}")
    @PreAuthorize("@securityUtils.isStaffByStaffId(#staffId) or hasRole('ADMIN')")
    public ResponseEntity<?> getStaffById(@PathVariable String staffId) {
        return ResponseEntity.status(HttpStatus.OK).body(staffService.getStaffById(staffId));
    }

    @GetMapping("/position/{position}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStaffByPosition(@PathVariable @Pattern(regexp = "^(WAITER|COOK|BARTENDER|MANAGER)$", message = "Position must be WAITER, COOK, BARTENDER or MANAGER") String position) {
        return ResponseEntity.status(HttpStatus.OK).body(staffService.getStaffByPosition(position));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createStaff(@RequestBody @Validated StaffCreateDto staffCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.createStaff(staffCreateDto));
    }

    @DeleteMapping("/delete/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStaff(@PathVariable String staffId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(staffService.deleteStaff(staffId));
    }

    @PutMapping("/update/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStaff(@PathVariable String staffId, @RequestBody @Validated StaffUpdateDto staffCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(staffService.updateStaff(staffId, staffCreateDto));
    }

}
