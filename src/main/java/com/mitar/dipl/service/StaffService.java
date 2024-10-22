package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.dto.staff.StaffDto;
import com.mitar.dipl.model.dto.staff.StaffUpdateDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StaffService {

    List<StaffDto> getAllStaff();

    StaffDto getStaffById(String staffId);

    List<StaffDto> getStaffByPosition(String position);

    StaffDto createStaff(StaffCreateDto staffCreateDto);

    String deleteStaff(String staffId);

    StaffDto updateStaff(String staffId, StaffUpdateDto staffUpdateDto);

}
