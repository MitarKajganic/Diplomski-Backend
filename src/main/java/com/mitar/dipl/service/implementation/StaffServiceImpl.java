package com.mitar.dipl.service.implementation;


import com.mitar.dipl.mapper.StaffMapper;
import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.entity.Staff;
import com.mitar.dipl.repository.StaffRepository;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.service.StaffService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

    private UserRepository userRepository;

    private StaffMapper staffMapper;


    @Override
    public ResponseEntity<?> getAllStaff() {
        return ResponseEntity.status(HttpStatus.OK).body(staffRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getStaffById(String staffId) {
        Optional<Staff> staff = staffRepository.findById(UUID.fromString(staffId));
        if (staff.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(staff.get());
    }

    @Override
    public ResponseEntity<?> getStaffByUserId(String userId) {
        Optional<Staff> staff = staffRepository.findByUser_Id(UUID.fromString(userId));
        if (staff.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(staff.get());
    }

    @Override
    public ResponseEntity<?> getStaffByPosition(String position) {
        return ResponseEntity.status(HttpStatus.OK).body(staffRepository.findAllByPosition(position));
    }

    @Override
    public ResponseEntity<?> createStaff(StaffCreateDto staffCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffRepository.save(staffMapper.toEntity(staffCreateDto)));
    }

    @Override
    public ResponseEntity<?> deleteStaff(String staffId) {
        Optional<Staff> staff = staffRepository.findById(UUID.fromString(staffId));
        if (staff.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        staffRepository.delete(staff.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Override
    public ResponseEntity<?> updateStaff(String staffId, StaffCreateDto staffCreateDto) {
        Optional<Staff> optionalStaff = staffRepository.findById(UUID.fromString(staffId));
        if (optionalStaff.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found.");

        Staff staff = optionalStaff.get();
        staff.setName(staffCreateDto.getName());
        staff.setSurname(staffCreateDto.getSurname());
        staff.setPosition(staffCreateDto.getPosition());
        staff.setContactInfo(staffCreateDto.getContactInfo());

        userRepository.findById(UUID.fromString(staffCreateDto.getUserId())).ifPresent(staff::setUser);

        return ResponseEntity.status(HttpStatus.OK).body(staffRepository.save(staff));
    }
}
