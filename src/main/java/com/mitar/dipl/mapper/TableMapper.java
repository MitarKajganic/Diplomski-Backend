package com.mitar.dipl.mapper;

import com.mitar.dipl.model.dto.table_entity.TableCreateDto;
import com.mitar.dipl.model.dto.table_entity.TableDto;
import com.mitar.dipl.model.entity.TableEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TableMapper {

    private ReservationMapper reservationMapper;

    public TableDto toDto(TableEntity tableEntity) {
        TableDto tableDto = new TableDto();
        tableDto.setId(tableEntity.getId().toString());
        tableDto.setTableNumber(tableEntity.getTableNumber());
        tableDto.setCapacity(tableEntity.getCapacity());
        tableDto.setIsAvailable(tableEntity.getIsAvailable());
        tableDto.setReservations(tableEntity.getReservations().stream().map(reservation -> reservationMapper.toDto(reservation)).collect(Collectors.toSet()));
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
