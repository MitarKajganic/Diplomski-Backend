package com.mitar.dipl.utils;

import com.mitar.dipl.model.dto.reservation.ReservationCreateDto;
import com.mitar.dipl.model.entity.*;
import com.mitar.dipl.model.entity.enums.*;
import com.mitar.dipl.repository.*;
import com.mitar.dipl.service.ReservationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class DatabaseSeederService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;
    private final ReservationService reservationService; // Injected ReservationService
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BillRepository billRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void seedDatabase() {
        log.info("Starting database seeding...");

        // Seed Users
        User customer = createUserIfNotFound("mkajganic@outlook.com", "password", Role.CUSTOMER);
        User admin = createUserIfNotFound("admin@admin.com", "password", Role.SUPER_ADMIN);

        // Seed Staff
        Staff staff1 = createStaffIfNotFound("manager@staff.com", "password", "John", "Doe", Position.MANAGER, "123-456-7890");
        Staff staff2 = createStaffIfNotFound("waiter@staff.com", "password", "Jane", "Smith", Position.WAITER, "098-765-4321");

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
        TableEntity table1 = createTableIfNotFound(1, 6, true);
        TableEntity table2 = createTableIfNotFound(2, 6, true);
        createTableIfNotFound(3, 6, true);
        createTableIfNotFound(4, 6, false);



        // Seed Reservations
        createReservation(customer, table1, 2, LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));
        createReservation(customer, table2, 4, LocalDateTime.now().plusDays(1).withHour(18).withMinute(30));

        // Seed Orders and OrderItems
        DeliveryInfo deliveryInfo = new DeliveryInfo("John", "Doe", "Main St", "123", 2, "1234567890");
        DeliveryInfo deliveryInfo2 = new DeliveryInfo("Jane", "Smith", "Elm St", "456", 4, "0987654321");
        DeliveryInfo deliveryInfo3 = new DeliveryInfo("Alice", "Wonderland", "Oak St", "789", 3, "1112223333");
        DeliveryInfo deliveryInfo4 = new DeliveryInfo("Bob", "Builder", "Pine St", "101", 5, "4445556666");
        DeliveryInfo deliveryInfo5 = new DeliveryInfo("Charlie", "Chaplin", "Maple St", "202", 2, "7778889999");

        // Order 1: PENDING, Burger x1
        OrderEntity order1 = createOrderIfNotFound(customer, Status.PENDING, deliveryInfo);
        createOrderItemIfNotFound(order1, burger, 1, burger.getPrice(), burger.getName());

        // Bill and Transaction for Order 1
        Bill bill1 = createBillIfNotFound(order1, new BigDecimal("9.99"));
        verifyBillAndCreateTransaction(bill1, order1, Method.CASH); // Set method here

        // Order 2: COMPLETED, Pasta x2, Salad x1
        OrderEntity order2 = createOrderIfNotFound(customer, Status.COMPLETED, deliveryInfo2);
        createOrderItemIfNotFound(order2, pasta, 2, pasta.getPrice(), pasta.getName());
        createOrderItemIfNotFound(order2, salad, 1, salad.getPrice(), salad.getName());

        // Bill and Transaction for Order 2
        BigDecimal totalOrder2 = pasta.getPrice().multiply(BigDecimal.valueOf(2)).add(salad.getPrice());
        Bill bill2 = createBillIfNotFound(order2, totalOrder2);
        verifyBillAndCreateTransaction(bill2, order2, Method.CARD); // Set method here

        // Order 3: CANCELLED, Steak x1
        OrderEntity order3 = createOrderIfNotFound(customer, Status.CANCELLED, deliveryInfo3);
        createOrderItemIfNotFound(order3, steak, 1, steak.getPrice(), steak.getName());

        // Bill and Transaction for Order 3
        Bill bill3 = createBillIfNotFound(order3, steak.getPrice());
        verifyBillAndCreateTransaction(bill3, order3, Method.CASH); // Set method here

        // Order 4: COMPLETED, Ice Cream x2, Fries x1, Chicken Burger x1
        OrderEntity order4 = createOrderIfNotFound(customer, Status.COMPLETED, deliveryInfo4);
        createOrderItemIfNotFound(order4, iceCream, 2, iceCream.getPrice(), iceCream.getName());
        createOrderItemIfNotFound(order4, fries, 1, fries.getPrice(), fries.getName());
        createOrderItemIfNotFound(order4, chickenBurger, 1, chickenBurger.getPrice(), chickenBurger.getName());

        // Bill and Transaction for Order 4
        BigDecimal totalOrder4 = iceCream.getPrice().multiply(BigDecimal.valueOf(2))
                .add(fries.getPrice())
                .add(chickenBurger.getPrice());
        Bill bill4 = createBillIfNotFound(order4, totalOrder4);
        verifyBillAndCreateTransaction(bill4, order4, Method.CARD); // Set method here

        // Order 5: PENDING, Tomato Soup x1, Grilled Fish x1
        OrderEntity order5 = createOrderIfNotFound(customer, Status.PENDING, deliveryInfo5);
        createOrderItemIfNotFound(order5, tomatoSoup, 1, tomatoSoup.getPrice(), tomatoSoup.getName());
        createOrderItemIfNotFound(order5, grilledFish, 1, grilledFish.getPrice(), grilledFish.getName());

        // Bill and Transaction for Order 5
        BigDecimal totalOrder5 = tomatoSoup.getPrice().add(grilledFish.getPrice());
        Bill bill5 = createBillIfNotFound(order5, totalOrder5);
        verifyBillAndCreateTransaction(bill5, order5, Method.CASH); // Set method here

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
        user.setHashPassword(rawPassword); // setHashPassword handles encoding
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);
        log.info("Created user: {}", email);
        return user;
    }

    private Staff createStaffIfNotFound(String email, String rawPassword, String name, String surname, Position position, String contactInfo) {
        Optional<Staff> staffOpt = staffRepository.findByEmail(email);
        if (staffOpt.isPresent()) {
            log.info("Staff already exists: {}", email);
            return staffOpt.get();
        }

        Staff staff = new Staff();
        staff.setEmail(email);
        staff.setHashPassword(rawPassword); // setHashPassword handles encoding
        staff.setRole(Role.STAFF);
        staff.setActive(true);
        staff.setName(name);
        staff.setSurname(surname);
        staff.setPosition(position);
        staff.setContactInfo(contactInfo);

        staffRepository.save(staff);
        log.info("Created staff: {} {}", name, surname);
        return staff;
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
            log.info("Menu item already exists: {}", name);
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
            menu.addMenuItem(item);
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

    private void createReservation(User user, TableEntity table, int numberOfGuests, LocalDateTime reservationTime) {
        ReservationCreateDto reservationDto = new ReservationCreateDto();
        reservationDto.setUserId(user.getId().toString());
        reservationDto.setTableId(table.getId().toString());
        reservationDto.setNumberOfGuests(numberOfGuests);
        reservationDto.setReservationTime(reservationTime);
        reservationDto.setGuestName(null);
        reservationDto.setGuestEmail(null);
        reservationDto.setGuestPhone(null);

        try {
            reservationService.createReservation(reservationDto);
            log.info("Created reservation for user: {} at table: {}", user.getEmail(), table.getTableNumber());
        } catch (Exception e) {
            log.error("Failed to create reservation for user: {} at table: {}", user.getEmail(), table.getTableNumber(), e);
        }
    }

    private OrderEntity createOrderIfNotFound(User user, Status status, DeliveryInfo deliveryInfo) {
        // It's possible to have multiple orders with the same status for a user
        // So, always create a new order without checking
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(status);
        order.setDeliveryInfo(deliveryInfo);

        orderRepository.save(order);
        log.info("Created order for user: {} with status: {}", user.getEmail(), status);
        return order;
    }

    private OrderItem createOrderItemIfNotFound(OrderEntity order, MenuItem menuItem, int quantity, BigDecimal price, String name) {
        // Similarly, allow multiple order items for the same menu item in an order
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderEntity(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price.multiply(BigDecimal.valueOf(quantity)));
        orderItem.setName(name);

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
        bill.calculateFinalAmount(); // Assuming this method sets the final amount based on totalAmount

        billRepository.save(bill);

        // Establish the relationship
        order.setBill(bill);

        // Save the OrderEntity, which cascades to Bill due to CascadeType.ALL
        orderRepository.save(order);

        if (bill.getId() == null) {
            log.error("Failed to create bill for order ID: {}", order.getId());
            throw new IllegalStateException("Failed to create bill for order ID: " + order.getId());
        }

        log.info("Created bill for order ID: {}, Bill ID: {}", order.getId(), bill.getId());
        return bill;
    }

    /**
     * Creates a transaction if it does not already exist.
     *
     * @param bill    The associated Bill entity.
     * @param amount  The amount for the transaction.
     * @param type    The type of the transaction.
     * @param method  The payment method for the transaction.
     * @return The created or existing Transaction entity.
     */
    private Transaction createTransactionIfNotFound(Bill bill, BigDecimal amount, Type type, Method method) {
        Optional<Transaction> transactionOpt = transactionRepository.findByBillAndType(bill, type);
        if (transactionOpt.isPresent()) {
            log.info("Transaction already exists for bill ID: {}", bill.getId());
            return transactionOpt.get();
        }

        Transaction transaction = new Transaction();
        transaction.setBill(bill);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setMethod(method); // Set the method here

        transactionRepository.save(transaction);
        log.info("Created transaction of type: {} with method: {} for bill ID: {}", type, method, bill.getId());
        return transaction;
    }

    /**
     * Verifies that the bill has been persisted and creates a transaction.
     *
     * @param bill    The Bill entity to verify and create a transaction for.
     * @param order   The associated OrderEntity.
     * @param method  The payment method to be used for the transaction.
     */
    private void verifyBillAndCreateTransaction(Bill bill, OrderEntity order, Method method) {
        if (bill.getId() == null) {
            log.error("Bill has not been persisted for order ID: {}", order.getId());
            throw new IllegalStateException("Bill has not been persisted for order ID: " + order.getId());
        }

        createTransactionIfNotFound(bill, bill.getFinalAmount(), Type.PAYMENT, method);
    }
}