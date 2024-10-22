package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.dto.staff.StaffDto;
import com.mitar.dipl.model.dto.staff.StaffUpdateDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StaffService {

    /**
     * Fetches all staff members.
     *
     * @return List of StaffDto
     */
    List<StaffDto> getAllStaff();

    /**
     * Fetches a staff member by their ID.
     *
     * @param staffId The UUID of the staff member as a string.
     * @return StaffDto
     */
    StaffDto getStaffById(String staffId);

    /**
     * Fetches staff members by their position.
     *
     * @param position The position of the staff members.
     * @return List of StaffDto
     */
    List<StaffDto> getStaffByPosition(String position);

    /**
     * Creates a new staff member.
     *
     * @param staffCreateDto The DTO containing staff creation data.
     * @return StaffDto
     */
    StaffDto createStaff(StaffCreateDto staffCreateDto);

    /**
     * Deletes a staff member by their ID.
     *
     * @param staffId The UUID of the staff member as a string.
     * @return Success message.
     */
    String deleteStaff(String staffId);

    /**
     * Updates an existing staff member.
     *
     * @param staffId         The UUID of the staff member as a string.
     * @param staffUpdateDto The DTO containing updated staff data.
     * @return StaffDto
     */
    StaffDto updateStaff(String staffId, StaffUpdateDto staffUpdateDto);

}
