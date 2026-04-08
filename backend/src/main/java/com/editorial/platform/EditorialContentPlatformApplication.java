package com.editorial.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EditorialContentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EditorialContentPlatformApplication.class, args);
    }
}
