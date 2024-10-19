package com.mitar.dipl;

import com.mitar.dipl.model.entity.*;
import com.mitar.dipl.model.entity.enums.*;
import com.mitar.dipl.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class DatabaseSeederService {

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
        MenuItem pasta = createMenuItemIfNotFound("Pasta", "Creamy Alfredo pasta", new BigDecimal("11.99"), "Main Course", dinnerMenu);
        MenuItem salad = createMenuItemIfNotFound("Caesar Salad", "Fresh Caesar salad", new BigDecimal("7.99"), "Appetizer", lunchMenu);
        MenuItem steak = createMenuItemIfNotFound("Steak", "Grilled ribeye steak", new BigDecimal("19.99"), "Main Course", dinnerMenu);
        MenuItem grilledFish = createMenuItemIfNotFound("Grilled Fish", "Seasoned grilled fish", new BigDecimal("14.99"), "Main Course", dinnerMenu);
        MenuItem tomatoSoup = createMenuItemIfNotFound("Tomato Soup", "Hot tomato soup", new BigDecimal("5.99"), "Appetizer", lunchMenu);
        MenuItem fries = createMenuItemIfNotFound("French Fries", "Crispy golden fries", new BigDecimal("3.99"), "Side Dish", lunchMenu);
        MenuItem iceCream = createMenuItemIfNotFound("Ice Cream", "Vanilla ice cream scoop", new BigDecimal("4.99"), "Dessert", lunchMenu);

        // Associate MenuItems with Menus
        associateMenuItemWithMenu(lunchMenu, burger, chickenBurger, salad, tomatoSoup, fries, iceCream);
        associateMenuItemWithMenu(dinnerMenu, pasta, steak, grilledFish);

        // Seed Tables
        TableEntity table1 = createTableIfNotFound(1, 4, true);
        TableEntity table2 = createTableIfNotFound(2, 6, true);

        // Seed Reservations
        createReservationIfNotFound(customer, table1, 2);

        // Seed Orders and OrderItems

        // Order 1: PENDING, Burger x1
        OrderEntity order1 = createOrderIfNotFound(customer, Status.PENDING);
        createOrderItemIfNotFound(order1, burger, 1, burger.getPrice());

        // Bill and Transaction for Order 1
        Bill bill1 = createBillIfNotFound(order1, new BigDecimal("9.99"));
        verifyBillAndCreateTransaction(bill1, order1);

        // Order 2: COMPLETED, Pasta x2, Salad x1
        OrderEntity order2 = createOrderIfNotFound(customer, Status.COMPLETED);
        createOrderItemIfNotFound(order2, pasta, 2, pasta.getPrice());
        createOrderItemIfNotFound(order2, salad, 1, salad.getPrice());

        // Bill and Transaction for Order 2
        BigDecimal totalOrder2 = pasta.getPrice().multiply(BigDecimal.valueOf(2)).add(salad.getPrice());
        Bill bill2 = createBillIfNotFound(order2, totalOrder2);
        verifyBillAndCreateTransaction(bill2, order2);

        // Order 3: CANCELLED, Steak x1
        OrderEntity order3 = createOrderIfNotFound(customer, Status.CANCELLED);
        createOrderItemIfNotFound(order3, steak, 1, steak.getPrice());

        // Bill and Transaction for Order 3
        Bill bill3 = createBillIfNotFound(order3, steak.getPrice());
        verifyBillAndCreateTransaction(bill3, order3);

        // Order 4: COMPLETED, Ice Cream x2, Fries x1, Chicken Burger x1
        OrderEntity order4 = createOrderIfNotFound(customer, Status.COMPLETED);
        createOrderItemIfNotFound(order4, iceCream, 2, iceCream.getPrice());
        createOrderItemIfNotFound(order4, fries, 1, fries.getPrice());
        createOrderItemIfNotFound(order4, chickenBurger, 1, chickenBurger.getPrice());

        // Bill and Transaction for Order 4
        BigDecimal totalOrder4 = iceCream.getPrice().multiply(BigDecimal.valueOf(2))
                .add(fries.getPrice())
                .add(chickenBurger.getPrice());
        Bill bill4 = createBillIfNotFound(order4, totalOrder4);
        verifyBillAndCreateTransaction(bill4, order4);

        // Order 5: PENDING, Tomato Soup x1, Grilled Fish x1
        OrderEntity order5 = createOrderIfNotFound(customer, Status.PENDING);
        createOrderItemIfNotFound(order5, tomatoSoup, 1, tomatoSoup.getPrice());
        createOrderItemIfNotFound(order5, grilledFish, 1, grilledFish.getPrice());

        // Bill and Transaction for Order 5
        BigDecimal totalOrder5 = tomatoSoup.getPrice().add(grilledFish.getPrice());
        Bill bill5 = createBillIfNotFound(order5, totalOrder5);
        verifyBillAndCreateTransaction(bill5, order5);

        log.info("Database seeding completed successfully with multiple orders and order items.");
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
        user.setHashPassword(passwordEncoder.encode(rawPassword));
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
        for (MenuItem item : menuItems) {
            if (!menu.getItems().contains(item)) {
                menu.addMenuItem(item);
                log.info("Added MenuItem: {} to Menu: {}", item.getName(), menu.getName());
            } else {
                log.info("MenuItem: {} is already associated with Menu: {}", item.getName(), menu.getName());
            }
        }
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

    private Bill createBillIfNotFound(OrderEntity order, BigDecimal totalAmount) {
        if (order.getBill() != null) {
            log.info("Bill already exists for order ID: {}", order.getId());
            return order.getBill();
        }

        Bill bill = new Bill();
        bill.setTotalAmount(totalAmount);
        bill.calculateFinalAmount();

        billRepository.save(bill);

        // Establish the relationship
        order.setBill(bill);

        // Save the OrderEntity, which cascades to Bill due to CascadeType.ALL
        orderRepository.save(order);


        if (bill.getId() == null) {
            log.error("Bill was not persisted correctly for order ID: {}", order.getId());
            throw new IllegalStateException("Bill was not persisted correctly.");
        }

        log.info("Created bill for order ID: {}, Bill ID: {}", order.getId(), bill.getId());
        return bill;
    }

    private Transaction createTransactionIfNotFound(Bill bill, BigDecimal amount, Type type) {
        if (bill.getId() == null) {
            log.error("Cannot create Transaction: Bill ID is null for Bill associated with order ID");
            throw new IllegalStateException("Cannot create Transaction: Bill is not persisted.");
        }

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

    /**
     * Verifies that the bill has been persisted and creates a transaction.
     *
     * @param bill   The Bill entity to verify and create a transaction for.
     * @param order  The associated OrderEntity.
     */
    private void verifyBillAndCreateTransaction(Bill bill, OrderEntity order) {
        if (bill.getId() == null) {
            log.error("Bill was not persisted correctly for order ID: {}", order.getId());
            throw new IllegalStateException("Bill was not persisted correctly.");
        }

        createTransactionIfNotFound(bill, bill.getFinalAmount(), Type.PAYMENT);
    }

}
