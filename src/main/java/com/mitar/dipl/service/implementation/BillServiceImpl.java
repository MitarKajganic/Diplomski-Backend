package com.mitar.dipl.service.implementation;

import com.mitar.dipl.mapper.BillMapper;
import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.repository.BillRepository;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.service.BillService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
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
public class BillServiceImpl implements BillService {

    private BillRepository billRepository;

    private BillMapper billMapper;

    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);


    @Override
    public ResponseEntity<?> getAll() {
        logger.info("Fetching all bills.");
        return ResponseEntity.ok(billRepository.findAll().stream()
                .map(billMapper::toDto)
                .toList());
    }

    @Override
    public ResponseEntity<?> getBillById(String id) {
        Optional<Bill> bill = billRepository.findById(UUID.fromString(id));
        if (bill.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(billMapper.toDto(bill.get()));
    }

    @Override
    public ResponseEntity<?> createBill(BillCreateDto billCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billMapper.toDto(billRepository.save(billMapper.toEntity(billCreateDto))));
    }

}
