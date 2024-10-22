package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ConflictException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.StaffMapper;
import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.dto.staff.StaffDto;
import com.mitar.dipl.model.dto.staff.StaffUpdateDto;
import com.mitar.dipl.model.entity.Staff;
import com.mitar.dipl.model.entity.enums.Position;
import com.mitar.dipl.repository.StaffRepository;
import com.mitar.dipl.service.StaffService;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;


    @Override
    public List<StaffDto> getAllStaff() {
        log.info("Fetching all staff members.");
        List<StaffDto> staffDtos = staffRepository.findAll().stream()
                .map(staffMapper::toDto)
                .toList();
        log.info("Fetched {} staff members.", staffDtos.size());
        return staffDtos;
    }

    @Override
    public StaffDto getStaffById(String staffId) {
        UUID parsedStaffId = UUIDUtils.parseUUID(staffId);
        log.debug("Fetching Staff with ID: {}", parsedStaffId);

        Staff staff = staffRepository.findById(parsedStaffId)
                .orElseThrow(() -> {
                    log.warn("Staff not found with ID: {}", staffId);
                    return new ResourceNotFoundException("Staff not found with ID: " + staffId);
                });

        StaffDto staffDto = staffMapper.toDto(staff);
        log.info("Retrieved Staff ID: {}", staffId);
        return staffDto;
    }

    @Override
    public List<StaffDto> getStaffByPosition(String position) {
        Position staffPosition;
        try {
            staffPosition = Position.valueOf(position.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid position value: {}", position);
            throw new BadRequestException("Invalid position value: " + position);
        }

        log.debug("Fetching Staff with Position: {}", staffPosition);
        List<StaffDto> staffDtos = staffRepository.findAllByPosition(staffPosition).stream()
                .map(staffMapper::toDto)
                .toList();

        log.info("Fetched {} staff members with Position: {}", staffDtos.size(), staffPosition);
        return staffDtos;
    }

    @Override
    public StaffDto createStaff(StaffCreateDto staffCreateDto) {
        log.info("Attempting to create staff member with email: {}", staffCreateDto.getEmail());

        if (staffRepository.findByEmail(staffCreateDto.getEmail()).isPresent()) {
            log.warn("Email already in use: {}", staffCreateDto.getEmail());
            throw new ConflictException("Email already in use.");
        }

        Staff staffEntity = staffMapper.toEntity(staffCreateDto);

        Staff savedStaff = staffRepository.save(staffEntity);
        log.info("Staff member created successfully with ID: {}", savedStaff.getId());

        return staffMapper.toDto(savedStaff);
    }

    @Override
    public String deleteStaff(String staffId) {
        UUID uuid = UUIDUtils.parseUUID(staffId);
        log.debug("Attempting to delete Staff with ID: {}", uuid);

        Staff staff = staffRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.warn("Staff not found with ID: {}", staffId);
                    return new ResourceNotFoundException("Staff not found with ID: " + staffId);
                });

        staffRepository.delete(staff);
        log.info("Staff member deleted successfully with ID: {}", staffId);
        return "Staff deleted successfully.";
    }

    @Override
    public StaffDto updateStaff(String staffId, StaffUpdateDto staffUpdateDto) {
        UUID uuid = UUIDUtils.parseUUID(staffId);
        log.debug("Attempting to update Staff with ID: {}", uuid);

        Staff existingStaff = staffRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.warn("Staff not found with ID: {}", staffId);
                    return new ResourceNotFoundException("Staff not found with ID: " + staffId);
                });

        existingStaff.setName(staffUpdateDto.getName());
        existingStaff.setSurname(staffUpdateDto.getSurname());
        existingStaff.setPosition(Position.valueOf(staffUpdateDto.getPosition().toUpperCase()));
        existingStaff.setContactInfo(staffUpdateDto.getContactInfo());

        Staff updatedStaff = staffRepository.save(existingStaff);
        log.info("Staff member updated successfully with ID: {}", staffId);

        return staffMapper.toDto(updatedStaff);
    }
}
