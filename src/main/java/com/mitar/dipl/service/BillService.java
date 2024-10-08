package com.mitar.dipl.service;

import com.mitar.dipl.model.dto.bill.BillCreateDto;
import org.springframework.http.ResponseEntity;

public interface BillService {

    ResponseEntity<?> getBillById(String id);

    ResponseEntity<?> getBillByOrderId(String orderId);

    ResponseEntity<?> createBill(BillCreateDto billCreateDto);

    ResponseEntity<?> deleteBill(String id);

    ResponseEntity<?> updateBill(String id, BillCreateDto billCreateDto);

}
