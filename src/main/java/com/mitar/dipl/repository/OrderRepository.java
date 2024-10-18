package com.mitar.dipl.repository;

import com.mitar.dipl.model.entity.OrderEntity;
import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByBill_Id(UUID billId);

    Optional<OrderEntity> findByUserAndStatus(User user, Status status);
}
