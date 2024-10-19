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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class BillServiceImpl implements BillService {

    private BillRepository billRepository;

    private BillMapper billMapper;

    private final OrderRepository orderRepository;

    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    // amount with tax
    private static final BigDecimal TAX = new BigDecimal("1.20");

    @Override
    public ResponseEntity<?> getAll() {
        logger.info("Fetching all bills.");
        return ResponseEntity.ok(billRepository.findAll().stream()
                .map(billMapper::toDto)
                .toList());
    }

    @Override
    public ResponseEntity<?> getBillById(String id) {
        UUID billId = UUIDUtils.parseUUID(id);
        Optional<Bill> bill = billRepository.findById(billId);
        if (bill.isEmpty()) {
            logger.warn("Bill with ID " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill with ID " + id + " does not exist.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(billMapper.toDto(bill.get()));
    }

    @Override
    public ResponseEntity<?> createBill(BillCreateDto billCreateDto) {
        String orderId = billCreateDto.getOrderId();
        UUID orderUUID = UUIDUtils.parseUUID(orderId);

        Optional<OrderEntity> orderOpt = orderRepository.findById(orderUUID);
        if (orderOpt.isEmpty()) {
            logger.warn("Order with ID " + orderId + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with ID " + orderId + " does not exist.");
        }
        OrderEntity order = orderOpt.get();

        if (order.getBill() != null) {
            logger.warn("Bill for order with ID " + orderUUID + " already exists.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bill for order with ID " + orderUUID + " already exists.");
        }

        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(orderItem -> orderItem.getMenuItem().getPrice().multiply(new BigDecimal(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Bill bill = new Bill();
        bill.setTotalAmount(totalAmount);
        bill.setOrderEntity(order);

        order.setBill(bill);

        logger.info("Bill created successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(billMapper.toDto(billRepository.save(bill)));
    }

}
