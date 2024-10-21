package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.dto.staff.StaffUpdateDto;
import org.springframework.http.ResponseEntity;

public interface StaffService {

    ResponseEntity<?> getAllStaff();

    ResponseEntity<?> getStaffById(String staffId);

    ResponseEntity<?> getStaffByPosition(String position);

    ResponseEntity<?> createStaff(StaffCreateDto staffCreateDto);

    ResponseEntity<?> deleteStaff(String staffId);

    ResponseEntity<?> updateStaff(String staffId, StaffUpdateDto staffUpdateDto);

}
