package com.mitar.dipl.security;

import com.mitar.dipl.model.entity.*;
import com.mitar.dipl.repository.*;
import com.mitar.dipl.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("securityUtils")
@AllArgsConstructor
public class SecurityUtils {

    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final BillRepository billRepository;
    private final OrderItemRepository orderItemRepository;
    private final TransactionRepository transactionRepository;
    private final StaffRepository staffRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Retrieves the email (username) of the currently authenticated user.
     *
     * @return The email of the user, or null if not authenticated.
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * Retrieves the UUID of the currently authenticated user.
     *
     * @return The UUID of the user.
     */
    public UUID getCurrentUserUUID() {
        String email = getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("User is not authenticated");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return user.getId();
    }

    /**
     * Checks if the authenticated user is the owner of the order by order ID.
     *
     * @param orderId The UUID of the order.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isOrderOwnerByOrderId(String orderId) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);
        OrderEntity order = orderRepository.findById(parsedOrderId)
                .orElse(null);
        if (order == null) {
            return false;
        }
        return getCurrentUserUUID().equals(order.getUser().getId());
    }

    /**
     * Checks if the authenticated user is the owner of the order by bill ID.
     *
     * @param billId The UUID of the bill.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isOrderOwnerByBillId(String billId) {
        UUID parsedBillId = UUIDUtils.parseUUID(billId);
        OrderEntity order = orderRepository.findByBill_Id(parsedBillId)
                .orElse(null);
        if (order == null) {
            return false;
        }
        return getCurrentUserUUID().equals(order.getUser().getId());
    }

    /**
     * Checks if the authenticated user is the owner of the bill by bill ID.
     *
     * @param billId The UUID of the bill.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isBillOwnerByBillId(String billId) {
        UUID parsedBillId = UUIDUtils.parseUUID(billId);
        Bill bill = billRepository.findById(parsedBillId)
                .orElse(null);
        if (bill == null) {
            return false;
        }
        return getCurrentUserUUID().equals(bill.getOrderEntity().getUser().getId());
    }

    /**
     * Checks if the authenticated user is the owner of the order item by order item ID.
     *
     * @param orderItemId The UUID of the order item.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isOrderItemOwnerByOrderItemId(String orderItemId) {
        UUID parsedOrderItemId = UUIDUtils.parseUUID(orderItemId);
        OrderItem orderItem = orderItemRepository.findById(parsedOrderItemId)
                .orElse(null);
        if (orderItem == null) {
            return false;
        }
        return getCurrentUserUUID().equals(orderItem.getOrderEntity().getUser().getId());
    }

    /**
     * Checks if the authenticated user is the owner of the order item by order ID.
     *
     * @param orderId The UUID of the order.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isOrderItemOwnerByOrderId(String orderId) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);
        OrderEntity order = orderRepository.findById(parsedOrderId)
                .orElse(null);
        if (order == null) {
            return false;
        }
        return getCurrentUserUUID().equals(order.getUser().getId());
    }

    /**
     * Checks if the authenticated user is the owner of the transaction by transaction ID.
     *
     * @param transactionId The UUID of the transaction.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isTransactionOwnerByTransactionId(String transactionId) {
        UUID parsedTransactionId = UUIDUtils.parseUUID(transactionId);
        Transaction transaction = transactionRepository.findById(parsedTransactionId)
                .orElse(null);
        if (transaction == null) {
            return false;
        }
        return getCurrentUserUUID().equals(transaction.getBill().getOrderEntity().getUser().getId());
    }

    /**
     * Checks if the authenticated user is the owner of the transaction by bill ID.
     *
     * @param billId The UUID of the bill.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isTransactionsOwnerByBillId(String billId) {
        UUID parsedBillId = UUIDUtils.parseUUID(billId);
        Bill bill = billRepository.findById(parsedBillId)
                .orElse(null);
        if (bill == null) {
            return false;
        }
        return getCurrentUserUUID().equals(bill.getOrderEntity().getUser().getId());
    }

    /**
     * Checks if the authenticated user is the given user by user ID.
     *
     * @param userId The UUID of the user.
     * @return True if the user is the given user, false otherwise.
     */
    public boolean isUserByUserId(String userId) {
        UUID parsedUserId = UUIDUtils.parseUUID(userId);
        User user = userRepository.findById(parsedUserId)
                .orElse(null);
        if (user == null) {
            return false;
        }
        return getCurrentUserUUID().equals(user.getId());
    }

    /**
     * Checks if the authenticated user is the given user by email.
     *
     * @param email The UUID of the user.
     * @return True if the user is the given user, false otherwise.
     */
    public boolean isUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if (user == null) {
            return false;
        }
        return getCurrentUserUUID().equals(user.getId());
    }

    /**
     * Checks if the authenticated user is the staff user by staffId.
     *
     * @param staffId The UUID of the user.
     * @return True if the user is the given staff, false otherwise.
     */
    public boolean isStaffByStaffId(String staffId) {
        UUID parsedStaffId = UUIDUtils.parseUUID(staffId);
        Staff staff = staffRepository.findById(parsedStaffId)
                .orElse(null);
        if (staff == null) {
            return false;
        }
        return getCurrentUserUUID().equals(staff.getId());
    }

    /**
     * Checks if the authenticated user is the owner of the reservation by reservation ID.
     *
     * @param reservationId The UUID of the reservation.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isReservationOwnerByReservationId(String reservationId) {
        UUID parsedReservationId = UUIDUtils.parseUUID(reservationId);
        Reservation reservation = reservationRepository.findById(parsedReservationId)
                .orElse(null);
        log.info("Reservation: " + reservation);
        if (reservation == null) {
            return false;
        }
        if (reservation.getUser() == null) {
            return true;
        }
        return getCurrentUserUUID().equals(reservation.getUser().getId());
    }

    /**
     * Checks if the authenticated user is the owner of the reservation by user ID.
     *
     * @param userId The UUID of the user.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isReservationsOwnerByUserId(String userId) {
        UUID parsedUserId = UUIDUtils.parseUUID(userId);
        List<Reservation> reservation = reservationRepository.findAllByUser_Id(parsedUserId);
        if (reservation == null || reservation.isEmpty()) {
            return false;
        }
        return getCurrentUserUUID().equals(reservation.get(0).getUser().getId());
    }

    /**
     * Checks if the authenticated user is the owner of the bill by order ID.
     *
     * @param orderId The UUID of the order.
     * @return True if the user is the owner, false otherwise.
     */
    public boolean isBillOwnerByOrderId(String orderId) {
        UUID parsedOrderId = UUIDUtils.parseUUID(orderId);
        OrderEntity order = orderRepository.findById(parsedOrderId)
                .orElse(null);
        if (order == null) {
            return false;
        }
        return getCurrentUserUUID().equals(order.getUser().getId());
    }

    /**
     * Checks if the authenticated user is accessing their own data.
     *
     * @param userId The UUID of the user.
     * @return True if the user is accessing their own data, false otherwise.
     */
    public boolean isUserSelf(String userId) {
        UUID parsedUserId = UUIDUtils.parseUUID(userId);
        return getCurrentUserUUID().equals(parsedUserId);
    }

}
