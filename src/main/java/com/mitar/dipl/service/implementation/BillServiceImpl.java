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

    private OrderRepository orderRepository;

    private BillMapper billMapper;


    @Override
    public ResponseEntity<?> getBillById(String id) {
        Optional<Bill> bill = billRepository.findById(UUID.fromString(id));
        if (bill.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(billMapper.toDto(bill.get()));
    }

    @Override
    public ResponseEntity<?> getBillByOrderId(String orderId) {
        Optional<OrderEntity> order = orderRepository.findById(UUID.fromString(orderId));
        if (order.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        Optional<Bill> bill = billRepository.findByOrderEntity(order.get());
        if (bill.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(billMapper.toDto(bill.get()));
    }

    @Override
    public ResponseEntity<?> createBill(BillCreateDto billCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billMapper.toDto(billRepository.save(billMapper.toEntity(billCreateDto))));
    }

}
