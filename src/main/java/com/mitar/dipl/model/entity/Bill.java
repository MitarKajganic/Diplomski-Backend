package com.mitar.dipl.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bills")
@Getter
@Setter
@ToString(exclude = {"orderEntity"})
@EqualsAndHashCode(exclude = {"orderEntity"})
public class Bill {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private BigDecimal tax = new BigDecimal("0.2");

    private BigDecimal finalAmount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "bill", fetch = FetchType.LAZY)
    @JsonBackReference
    private OrderEntity orderEntity;

    @PrePersist
    public void calculateFinalAmount() {
        this.finalAmount = this.totalAmount.multiply(this.tax.add(BigDecimal.ONE));
    }
}

