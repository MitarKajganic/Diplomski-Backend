package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import org.springframework.http.ResponseEntity;

public interface StaffService {

    ResponseEntity<?> getAllStaff();

    ResponseEntity<?> getStaffById(String staffId);

    ResponseEntity<?> getStaffByUserId(String userId);

    ResponseEntity<?> getStaffByPosition(String position);

    ResponseEntity<?> createStaff(StaffCreateDto staffCreateDto);

    ResponseEntity<?> deleteStaff(String staffId);

    ResponseEntity<?> updateStaff(String staffId, StaffCreateDto staffCreateDto);

}
