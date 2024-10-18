package com.mitar.dipl;

import com.mitar.dipl.model.entity.*;
import com.mitar.dipl.model.entity.enums.*;
import com.mitar.dipl.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
//@Profile({"dev", "test"}) // Ensure seeding runs only in development or testing environments
@AllArgsConstructor
@Slf4j
public class DBSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BillRepository billRepository;
    private final TransactionRepository transactionRepository;
    private final InventoryRepository inventoryRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedDatabase();
    }

    @Transactional
    public void seedDatabase() {
        log.info("Starting database seeding...");

        // Seed Users
        User customer = createUserIfNotFound("mkajganic@outlook.com", "password", Role.CUSTOMER);
        User admin = createUserIfNotFound("admin@admin.com", "password", Role.SUPER_ADMIN);

        // Seed Staff
        createStaffIfNotFound(admin, "John", "Doe", Position.MANAGER, "123456789");

        // Seed Inventory Items
        Inventory beefInventory = createInventoryIfNotFound("Beef", 10, "kg", false);
        Inventory chickenInventory = createInventoryIfNotFound("Chicken", 15, "kg", false);

        // Seed Menus and MenuItems
        Menu lunchMenu = createMenuIfNotFound("Lunch");
        Menu dinnerMenu = createMenuIfNotFound("Dinner");

        MenuItem burger = createMenuItemIfNotFound("Burger", "Delicious beef burger", new BigDecimal("9.99"), "Main Course", lunchMenu);
        MenuItem chickenBurger = createMenuItemIfNotFound("Chicken Burger", "Tasty chicken burger", new BigDecimal("8.99"), "Main Course", lunchMenu);

        // Associate MenuItems with Menus
        associateMenuItemWithMenu(lunchMenu, burger, chickenBurger);
        associateMenuItemWithMenu(dinnerMenu, burger); // Example: Dinner menu also has Burger

        // Seed Tables
        TableEntity table1 = createTableIfNotFound(1, 4, true);
        TableEntity table2 = createTableIfNotFound(2, 6, true);

        // Seed Reservations
        createReservationIfNotFound(customer, table1, 2);

        // Seed Orders and OrderItems
        OrderEntity order = createOrderIfNotFound(customer, Status.PENDING);
        createOrderItemIfNotFound(order, burger, 1, burger.getPrice());

        // Seed Bills
        Bill bill = createBillIfNotFound(order, new BigDecimal("9.99"), new BigDecimal("0.99"), new BigDecimal("1.00"));

        // Seed Transactions
        createTransactionIfNotFound(bill, bill.getFinalAmount(), Type.PAYMENT);

        log.info("Database seeding completed successfully.");
    }

    // Helper Methods

    private User createUserIfNotFound(String email, String rawPassword, Role role) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            log.info("User already exists: {}", email);
            return userOpt.get();
        }

        User user = new User();
        user.setEmail(email);
        user.setHashPassword(rawPassword);
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);
        log.info("Created user: {}", email);
        return user;
    }

    private Staff createStaffIfNotFound(User user, String name, String surname, Position position, String contactInfo) {
        Optional<Staff> staffOpt = staffRepository.findByUser(user);
        if (staffOpt.isPresent()) {
            log.info("Staff already exists for user: {}", user.getEmail());
            return staffOpt.get();
        }

        Staff staff = new Staff();
        staff.setName(name);
        staff.setSurname(surname);
        staff.setPosition(position);
        staff.setContactInfo(contactInfo);
        staff.setUser(user);

        staffRepository.save(staff);
        log.info("Created staff: {} {}", name, surname);
        return staff;
    }

    private Inventory createInventoryIfNotFound(String itemName, int quantity, String unit, boolean lowStock) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByItemName(itemName);
        if (inventoryOpt.isPresent()) {
            log.info("Inventory item already exists: {}", itemName);
            return inventoryOpt.get();
        }


        Inventory inventory = new Inventory();
        inventory.setItemName(itemName);
        inventory.setQuantity(quantity);
        inventory.setUnit(unit);
        inventory.setLowStock(lowStock);

        inventoryRepository.save(inventory);
        log.info("Created inventory item: {}", itemName);
        return inventory;
    }

    private Menu createMenuIfNotFound(String menuName) {
        Optional<Menu> menuOpt = menuRepository.findByName(menuName);
        if (menuOpt.isPresent()) {
            log.info("Menu already exists: {}", menuName);
            return menuOpt.get();
        }

        Menu menu = new Menu();
        menu.setName(menuName);

        menuRepository.save(menu);
        log.info("Created menu: {}", menuName);
        return menu;
    }

    private MenuItem createMenuItemIfNotFound(String name, String description, BigDecimal price, String category, Menu menu) {
        Optional<MenuItem> menuItemOpt = menuItemRepository.findByName(name);
        if (menuItemOpt.isPresent()) {
            log.info("MenuItem already exists: {}", name);
            return menuItemOpt.get();
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        menuItem.setMenu(menu);

        menuItemRepository.save(menuItem);
        log.info("Created menu item: {}", name);
        return menuItem;
    }

    private void associateMenuItemWithMenu(Menu menu, MenuItem... menuItems) {
        Set<MenuItem> items = new HashSet<>(menu.getItems());
        for (MenuItem item : menuItems) {
            items.add(item);
            item.setMenu(menu);
        }
        menu.setItems(items);
        menuRepository.save(menu);
        log.info("Associated {} items with menu: {}", menuItems.length, menu.getName());
    }

    private TableEntity createTableIfNotFound(int tableNumber, int capacity, boolean isAvailable) {
        Optional<TableEntity> tableOpt = tableRepository.findByTableNumber(tableNumber);
        if (tableOpt.isPresent()) {
            log.info("Table already exists: {}", tableNumber);
            return tableOpt.get();
        }

        TableEntity table = new TableEntity();
        table.setTableNumber(tableNumber);
        table.setCapacity(capacity);
        table.setIsAvailable(isAvailable);

        tableRepository.save(table);
        log.info("Created table number: {}", tableNumber);
        return table;
    }

    private Reservation createReservationIfNotFound(User user, TableEntity table, int numberOfGuests) {
        Optional<Reservation> reservationOpt = reservationRepository.findByUserAndTable(user, table);
        if (reservationOpt.isPresent()) {
            log.info("Reservation already exists for user: {} at table: {}", user.getEmail(), table.getTableNumber());
            return reservationOpt.get();
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setNumberOfGuests(numberOfGuests);

        reservationRepository.save(reservation);
        log.info("Created reservation for user: {} at table: {}", user.getEmail(), table.getTableNumber());
        return reservation;
    }

    private OrderEntity createOrderIfNotFound(User user, Status status) {
        Optional<OrderEntity> orderOpt = orderRepository.findByUserAndStatus(user, status);
        if (orderOpt.isPresent()) {
            log.info("Order already exists for user: {} with status: {}", user.getEmail(), status);
            return orderOpt.get();
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(status);

        orderRepository.save(order);
        log.info("Created order for user: {} with status: {}", user.getEmail(), status);
        return order;
    }

    private OrderItem createOrderItemIfNotFound(OrderEntity order, MenuItem menuItem, int quantity, BigDecimal price) {
        Optional<OrderItem> orderItemOpt = orderItemRepository.findByOrderEntityAndMenuItem(order, menuItem);
        if (orderItemOpt.isPresent()) {
            log.info("OrderItem already exists: {} x{} for order ID: {}", menuItem.getName(), quantity, order.getId());
            return orderItemOpt.get();
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderEntity(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price.multiply(BigDecimal.valueOf(quantity)));

        orderItemRepository.save(orderItem);
        log.info("Created OrderItem: {} x{} for order ID: {}", menuItem.getName(), quantity, order.getId());
        return orderItem;
    }

    private Bill createBillIfNotFound(OrderEntity order, BigDecimal totalAmount, BigDecimal tax, BigDecimal discount) {
        Optional<Bill> billOpt = billRepository.findByOrderEntity(order);
        if (billOpt.isPresent()) {
            log.info("Bill already exists for order ID: {}", order.getId());
            return billOpt.get();
        }

        Bill bill = new Bill();
        bill.setOrderEntity(order);
        bill.setTotalAmount(totalAmount);
        bill.setTax(tax);
        bill.setDiscount(discount);
        bill.calculateFinalAmount();

        billRepository.save(bill);
        log.info("Created bill for order ID: {}", order.getId());
        return bill;
    }

    private Transaction createTransactionIfNotFound(Bill bill, BigDecimal amount, Type type) {
        Optional<Transaction> transactionOpt = transactionRepository.findByBillAndType(bill, type);
        if (transactionOpt.isPresent()) {
            log.info("Transaction already exists for bill ID: {} with type: {}", bill.getId(), type);
            return transactionOpt.get();
        }

        Transaction transaction = new Transaction();
        transaction.setBill(bill);
        transaction.setAmount(amount);
        transaction.setType(type);

        transactionRepository.save(transaction);
        log.info("Created transaction of type: {} for bill ID: {}", type, bill.getId());
        return transaction;
    }
}
