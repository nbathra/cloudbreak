package com.sequenceiq.freeipa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = "com.sequenceiq.freeipa")
public class DatalakeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatalakeApplication.class, args);
    }

}

