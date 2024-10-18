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

    private final DatabaseSeederService databaseSeederService;

    @Override
    public void run(String... args) throws Exception {
        databaseSeederService.seedDatabase();
    }
}
