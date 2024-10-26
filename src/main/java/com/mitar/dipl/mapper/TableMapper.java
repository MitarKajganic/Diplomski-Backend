package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.reservation.ReservationDto;
import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.model.dto.table_entity.TableDto;
import com.mitar.dipl.model.entity.Reservation;
import com.mitar.dipl.model.entity.TableEntity;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Role;
import com.mitar.dipl.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TableMapper {

    private ReservationMapper reservationMapper;
    private UserRepository userRepository;

    public TableDto toDto(TableEntity tableEntity) {
        TableDto tableDto = new TableDto();

        tableDto.setId(tableEntity.getId().toString());
        tableDto.setTableNumber(tableEntity.getTableNumber());
        tableDto.setCapacity(tableEntity.getCapacity());
        tableDto.setIsAvailable(tableEntity.getIsAvailable());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            tableDto.setReservations(new HashSet<>());
            return tableDto;
        }

        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null || user.getRole().equals(Role.CUSTOMER)) {
            tableDto.setReservations(new HashSet<>());
        } else {
            tableDto.setReservations(
                    tableEntity.getReservations()
                            .stream()
                            .map(reservationMapper::toDto)
                            .collect(Collectors.toSet())
            );
        }

        return tableDto;
    }

    public TableEntity toEntity(TableCreateDto tableCreateDto) {
        TableEntity tableEntity = new TableEntity();

        tableEntity.setTableNumber(tableCreateDto.getTableNumber());
        tableEntity.setCapacity(tableCreateDto.getCapacity());
        tableEntity.setIsAvailable(tableCreateDto.getIsAvailable());

        return tableEntity;
    }

}
