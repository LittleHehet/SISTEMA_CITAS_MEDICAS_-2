package com.example.sistema_citas.presentation.Secure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureRestController {

    @GetMapping("/api/secure")
    public String secureEndpoint() {
        return "Acceso autorizado a contenido seguro.";
    }
}
