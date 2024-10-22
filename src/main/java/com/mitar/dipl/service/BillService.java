package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.model.dto.bill.BillDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BillService {

    /**
     * Fetches all bills.
     *
     * @return List of BillDto
     */
    List<BillDto> getAll();

    /**
     * Fetches a bill by its ID.
     *
     * @param id The UUID of the bill as a string.
     * @return BillDto
     */
    BillDto getBillById(String id);

    /**
     * Creates a bill for an order.
     *
     * @param billCreateDto The BillCreateDto object.
     * @return BillDto
     */
    BillDto createBill(BillCreateDto billCreateDto);

}
