package com.mitar.dipl.repository;

import com.mitar.dipl.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findAllByUser_Id(UUID userId);

    List<Reservation> findAllByTable_Id(UUID tableId);

    List<Reservation> findAllByGuestName(String guestName);

    List<Reservation> findAllByGuestEmail(String guestEmail);

    List<Reservation> findAllByGuestPhone(String guestPhone);

    @Query("SELECT r FROM Reservation r")
    List<Reservation> findAllIncludingDeleted();

    boolean existsByTable_IdAndReservationTimeBetween(UUID tableId, LocalDateTime start, LocalDateTime end);

    boolean existsByUser_IdAndReservationTime(UUID userId, LocalDateTime reservationTime);

    boolean existsByGuestEmailAndReservationTime(String guestEmail, LocalDateTime reservationTime);

    boolean existsByUser_IdAndReservationTimeAndIdNot(UUID userId, LocalDateTime reservationTime, UUID id);

    boolean existsByTable_IdAndReservationTimeBetweenAndIdNot(UUID tableId, LocalDateTime start, LocalDateTime end, UUID id);

    boolean existsByGuestEmailAndReservationTimeAndIdNot(String guestEmail, LocalDateTime reservationTime, UUID id);

}
