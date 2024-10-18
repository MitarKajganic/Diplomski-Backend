package com.mitar.dipl;

import com.mitar.dipl.model.entity.*;
import com.mitar.dipl.model.entity.enums.Position;
import com.mitar.dipl.model.entity.enums.Role;
import com.mitar.dipl.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@AllArgsConstructor
public class DBSeeder implements CommandLineRunner {

    private UserRepository userRepository;
    private MenuRepository menuRepository;
    private MenuItemRepository menuItemRepository;
    private TableRepository tableRepository;
    private ReservationRepository reservationRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private BillRepository billRepository;
    private TransactionRepository transactionRepository;
    private InventoryRepository inventoryRepository;
    private StaffRepository staffRepository;

    @Override
    public void run(String... args) throws Exception {

    }
}