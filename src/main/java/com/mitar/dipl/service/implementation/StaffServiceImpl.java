package com.mitar.dipl.service.implementation;


import com.mitar.dipl.mapper.StaffMapper;
import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.dto.staff.StaffUpdateDto;
import com.mitar.dipl.model.entity.Staff;
import com.mitar.dipl.model.entity.enums.Position;
import com.mitar.dipl.repository.StaffRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.StaffService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

    private StaffRepository staffRepository;

    private StaffMapper staffMapper;

    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);


    @Override
    public ResponseEntity<?> getAllStaff() {
        logger.info("Fetching all staff.");
        return ResponseEntity.status(HttpStatus.OK).body(staffRepository.findAll().stream()
                .map(staffMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> getStaffById(String staffId) {
        UUID staffUUID = UUIDUtils.parseUUID(staffId);
        Optional<Staff> staff = staffRepository.findById(staffUUID);
        if (staff.isEmpty()) {
            logger.warn("Staff not found with ID: {}", staffId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(staffMapper.toDto(staff.get()));
    }

    @Override
    public ResponseEntity<?> getStaffByPosition(String position) {
        return ResponseEntity.status(HttpStatus.OK).body(staffRepository.findAllByPosition(Position.valueOf(position)).stream()
                .map(staffMapper::toDto)
                .toList()
        );
    }

    @Override
    public ResponseEntity<?> createStaff(StaffCreateDto staffCreateDto) {
        if (staffRepository.findByEmail(staffCreateDto.getEmail()).isPresent()) {
            logger.warn("Email already in use: {}", staffCreateDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(staffMapper.toDto(staffRepository.save(staffMapper.toEntity(staffCreateDto))));
    }

    @Override
    public ResponseEntity<?> deleteStaff(String staffId) {
        UUID uuid = UUIDUtils.parseUUID(staffId);

        Optional<Staff> staff = staffRepository.findById(uuid);
        if (staff.isEmpty()) {
            logger.warn("Staff not found with ID: {}", staffId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found.");
        }

        staffRepository.delete(staff.get());
        return ResponseEntity.status(HttpStatus.OK).body("Staff deleted successfully.");
    }

    @Override
    public ResponseEntity<?> updateStaff(String staffId, StaffUpdateDto staffCreateDto) {
        UUID uuid = UUIDUtils.parseUUID(staffId);

        Optional<Staff> optionalStaff = staffRepository.findById(uuid);
        if (optionalStaff.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found.");
        }

        Staff existingStaff = optionalStaff.get();

        existingStaff.setName(staffCreateDto.getName());
        existingStaff.setSurname(staffCreateDto.getSurname());


        existingStaff.setPosition(Position.valueOf(staffCreateDto.getPosition().toUpperCase()));

        existingStaff.setContactInfo(staffCreateDto.getContactInfo());

        return ResponseEntity.status(HttpStatus.OK).body(staffMapper.toDto(staffRepository.save(existingStaff)));
    }
}
