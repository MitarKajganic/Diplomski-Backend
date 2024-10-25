package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import com.mitar.dipl.mapper.BillMapper;
import com.mitar.dipl.model.dto.bill.BillCreateDto;
import com.mitar.dipl.model.dto.bill.BillDto; // Assuming you have a BillDto
import com.mitar.dipl.model.entity.Bill;
import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.repository.BillRepository;
import com.mitar.dipl.repository.OrderRepository;
import com.mitar.dipl.service.BillService;
import com.mitar.dipl.utils.UUIDUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final BillMapper billMapper;
    private final OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<BillDto> getAll() {
        log.info("Fetching all bills.");
        return billRepository.findAll().stream()
                .map(billMapper::toDto)
                .toList();
    }

    @Override
    public BillDto getBillById(String id) {
        UUID billId = UUIDUtils.parseUUID(id);
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> {
                    log.warn("Bill with ID {} does not exist.", id);
                    return new ResourceNotFoundException("Bill with ID " + id + " does not exist.");
                });
        return billMapper.toDto(bill);
    }

    @Override
    public BillDto createBill(BillCreateDto billCreateDto) {
        String orderId = billCreateDto.getOrderId();
        UUID orderUUID = UUIDUtils.parseUUID(orderId);

        OrderEntity order = orderRepository.findById(orderUUID)
                .orElseThrow(() -> {
                    log.warn("Order with ID {} does not exist.", orderId);
                    return new ResourceNotFoundException("Order with ID " + orderId + " does not exist.");
                });

        if (order.getBill() != null) {
            log.warn("Bill for order with ID {} already exists.", orderUUID);
            throw new BadRequestException("Bill for order with ID " + orderUUID + " already exists.");
        }

        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(orderItem -> orderItem.getMenuItem().getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Bill bill = new Bill();
        bill.setTotalAmount(totalAmount);
        bill.setOrderEntity(order);

        order.setBill(bill);

        Bill savedBill = billRepository.save(bill);
        entityManager.flush();
        entityManager.refresh(savedBill);
        log.info("Bill created successfully with ID {} for order ID {}.", savedBill.getId(), orderUUID);

        return billMapper.toDto(savedBill);
    }

}
