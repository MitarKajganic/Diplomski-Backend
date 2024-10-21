package com.mitar.dipl.model.dto.staff;

import com.mitar.dipl.model.dto.user.UserDto;
import lombok.Data;

@Data
public class StaffDto extends UserDto {

    private String name;
    private String surname;
    private String position;
    private String contactInfo;

}
