package com.example.sistema_citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(scanBasePackages = "com.example.sistema_citas")
public class SistemaCitasApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaCitasApplication.class, args);
    }

}
