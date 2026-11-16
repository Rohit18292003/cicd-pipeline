package org.techpulse.cicdpipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CicdPipelineApplication {
    public static void main(String[] args) {
        System.out.println("Updated application version");
        SpringApplication.run(CicdPipelineApplication.class, args);
    }
}
