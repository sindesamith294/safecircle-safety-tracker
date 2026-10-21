package com.safecircle.config;

import com.safecircle.model.*;
import com.safecircle.repository.LocationRecordRepository;
import com.safecircle.repository.SafetyCheckinRepository;
import com.safecircle.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final LocationRecordRepository locationRecordRepository;
    private final SafetyCheckinRepository checkinRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
        UserRepository userRepository,
        LocationRecordRepository locationRecordRepository,
        SafetyCheckinRepository checkinRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.locationRecordRepository = locationRecordRepository;
        this.checkinRepository = checkinRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Pre-register User A (Alice)
        User alice = userRepository.findByPhoneNumber("+15551000001").orElseGet(() -> {
            User u = new User();
            u.setName("Alice Walker");
            u.setPhoneNumber("+15551000001");
            u.setPasswordHash(passwordEncoder.encode("Password123!"));
            u.setAccountStatus(AccountStatus.ACTIVE);
            return userRepository.save(u);
        });

        // Pre-register User B (Bob)
        User bob = userRepository.findByPhoneNumber("+15551000002").orElseGet(() -> {
            User u = new User();
            u.setName("Bob Smith");
            u.setPhoneNumber("+15551000002");
            u.setPasswordHash(passwordEncoder.encode("Password123!"));
            u.setAccountStatus(AccountStatus.ACTIVE);
            return userRepository.save(u);
        });

        // Seed initial GPS position for Bob (San Francisco downtown)
        if (locationRecordRepository.findFirstByUserIdOrderByRecordedAtDesc(bob.getId()).isEmpty()) {
            LocationRecord bobLoc = new LocationRecord(
                bob,
                37.774929,
                -122.419416,
                6.5,
                Instant.now()
            );
            locationRecordRepository.save(bobLoc);
        }

        // Seed sample safety checkin for Bob
        if (checkinRepository.findByUserIdOrderByDueAtDesc(bob.getId()).isEmpty()) {
            SafetyCheckin checkin = new SafetyCheckin(
                bob,
                "Commute: Office to Home",
                Instant.now().plus(45, ChronoUnit.MINUTES)
            );
            checkinRepository.save(checkin);
        }

        log.info("=================================================================");
        log.info(" SafeCircle Initialized Ready For Testing:");
        log.info(" Test User A: Alice Walker | Phone: +15551000001 | Password: Password123!");
        log.info(" Test User B: Bob Smith    | Phone: +15551000002 | Password: Password123!");
        log.info("=================================================================");
    }
}
