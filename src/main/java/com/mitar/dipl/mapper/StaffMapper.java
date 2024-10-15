package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.dto.staff.StaffDto;
import com.mitar.dipl.model.entity.Staff;
import com.mitar.dipl.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class StaffMapper {

    private UserRepository userRepository;

    public StaffDto toDto(Staff staff) {
        StaffDto staffDto = new StaffDto();
        staffDto.setId(staff.getId().toString());
        staffDto.setName(staff.getName());
        staffDto.setSurname(staff.getSurname());
        staffDto.setPosition(staff.getPosition());
        staffDto.setContactInfo(staff.getContactInfo());
        staffDto.setUserId(staff.getUser().getId().toString());
        return staffDto;
    }

    public Staff toEntity(StaffCreateDto staffCreateDto) {
        Staff staff = new Staff();
        staff.setName(staffCreateDto.getName());
        staff.setSurname(staffCreateDto.getSurname());
        staff.setPosition(staffCreateDto.getPosition());
        staff.setContactInfo(staffCreateDto.getContactInfo());
        userRepository.findById(UUID.fromString(staffCreateDto.getUserId())).ifPresent(staff::setUser);
        return staff;
    }

}
