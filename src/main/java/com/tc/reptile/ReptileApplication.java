package com.tc.reptile;

import com.tc.reptile.config.ReptileProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({ReptileProperties.class})
public class ReptileApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReptileApplication.class, args);
    }

}
