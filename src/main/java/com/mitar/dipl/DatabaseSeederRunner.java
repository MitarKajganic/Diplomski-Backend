package com.mitar.dipl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeederRunner implements CommandLineRunner {

    private final DatabaseSeederService databaseSeederService;

    // Inject the 'spring.jpa.hibernate.ddl-auto' property value
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Override
    public void run(String... args) throws Exception {
        // Check if ddl-auto is set to 'create' or 'create-drop'
        if ("create".equalsIgnoreCase(ddlAuto) || "create-drop".equalsIgnoreCase(ddlAuto)) {
            log.info("Hibernate ddl-auto is set to '{}'. Starting database seeding.", ddlAuto);
            databaseSeederService.seedDatabase();
        } else {
            log.info("Hibernate ddl-auto is set to '{}'. Skipping database seeding.", ddlAuto);
        }
    }
}
