package com.example.sistema_citas.presentation.signUp;

import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/signup")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioSignUpRestController {

    @Autowired
    private Service service;

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        int cedula = nuevoUsuario.getCedula();

        // Validar si ya existe
        Optional<Usuario> existente = service.findByCedula(cedula);
        if (existente.isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya está registrado.");
        }

        try {
            // Guardar usuario
            service.saveUsuario(nuevoUsuario);

            // Si el perfil es médico, crear médico también
            if ("MEDICO".equalsIgnoreCase(nuevoUsuario.getPerfil())) {
                service.saveMedicoByCedula(cedula);
            }

            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Agregado
            return ResponseEntity.internalServerError().body("Error al registrar el usuario");
        }

    }
}
