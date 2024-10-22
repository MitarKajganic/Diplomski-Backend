package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.model.dto.bill.BillDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BillService {

    List<BillDto> getAll();

    BillDto getBillById(String id);

    BillDto createBill(BillCreateDto billCreateDto);

}
