package com.example.sistema_citas.presentation.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private")
public class RoleRestController {

    @GetMapping("/anonimo")
    @PreAuthorize("hasRole('ANONIMO')")
    public ResponseEntity<?> anonimoOnly() {
        return ResponseEntity.ok("Bienvenido anonimo");
    }

    @GetMapping("/paciente")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<?> pacienteOnly() {
        return ResponseEntity.ok("Bienvenido paciente");
    }

    @GetMapping("/medico")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<?> medicoOnly() {
        return ResponseEntity.ok("Bienvenido medico");
    }

    @GetMapping("/administrador")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> administradorOnly() {
        return ResponseEntity.ok("Bienvenido administrador");
    }
}
