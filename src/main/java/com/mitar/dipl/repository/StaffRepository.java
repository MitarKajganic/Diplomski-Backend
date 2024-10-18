package com.mitar.dipl.repository;

import com.mitar.dipl.model.entity.Staff;
import com.mitar.dipl.model.entity.enums.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    Optional<Staff> findByUser_Id(UUID userId);

    List<Staff> findAllByPosition(Position position);

}
