package com.safecircle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SafeCircleApplication {

    private static final Logger log = LoggerFactory.getLogger(SafeCircleApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SafeCircleApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("=================================================================");
        log.info("🛡️  SafeCircle Safety Tracker Backend is RUNNING!");
        log.info("🌐 Web App & Live Map Dashboard : http://localhost:8080/");
        log.info("🗄️  H2 Database Console         : http://localhost:8080/h2-console");
        log.info("🤖 ML Microservice (if running): http://localhost:5000/");
        log.info("=================================================================");
    }
}
