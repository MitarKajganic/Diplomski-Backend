package com.mitar.dipl.repository;

import com.mitar.dipl.model.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, UUID> {

    Optional<TableEntity> findByTableNumber(Integer tableNumber);
}
