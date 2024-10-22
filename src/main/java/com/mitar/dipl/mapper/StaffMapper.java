package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.staff.StaffCreateDto;
import com.mitar.dipl.model.dto.staff.StaffDto;
import com.mitar.dipl.model.dto.user.UserDto;
import com.mitar.dipl.model.entity.Staff;
import com.mitar.dipl.model.entity.enums.Position;
import com.mitar.dipl.model.entity.enums.Role;
import com.mitar.dipl.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class StaffMapper {

    private UserMapper userMapper;

    public StaffDto toDto(Staff staff) {
        StaffDto staffDto = new StaffDto();

        UserDto userDto = userMapper.toDto(staff);
        staffDto.setId(userDto.getId());
        staffDto.setEmail(userDto.getEmail());
        staffDto.setReservations(userDto.getReservations());
        staffDto.setRole(userDto.getRole());
        staffDto.setActive(userDto.getActive());

        staffDto.setName(staff.getName());
        staffDto.setSurname(staff.getSurname());
        staffDto.setPosition(String.valueOf(staff.getPosition()));
        staffDto.setContactInfo(staff.getContactInfo());
        return staffDto;
    }

    public Staff toEntity(StaffCreateDto staffCreateDto) {
        Staff staff = new Staff();

        staff.setEmail(staffCreateDto.getEmail());
        staff.setHashPassword(staffCreateDto.getPassword());
        staff.setActive(true);
        staff.setRole(Role.STAFF);

        staff.setName(staffCreateDto.getName());
        staff.setSurname(staffCreateDto.getSurname());
        staff.setPosition(Position.valueOf(staffCreateDto.getPosition()));
        staff.setContactInfo(staffCreateDto.getContactInfo());

        return staff;
    }

}
