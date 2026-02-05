package com.example.sistema_citas.presentation.historico;


import com.example.sistema_citas.logic.Cita;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/historicoPaciente")
@CrossOrigin(origins = "http://localhost:5173")
public class HistoricoRestController {
    @Autowired
    private Service service;

    private Optional<Usuario> getUsuarioAutenticado(Authentication authentication) {
        if (authentication == null) return Optional.empty();

        Object principal = authentication.getPrincipal();

        // Si viene de Google/JWT
        if (principal instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");

            return service.findByGoogleSubOrEmail(sub, email); // te muestro abajo cómo
        }

        // Si viene login normal (cedula)
        try {
            Integer cedula = Integer.parseInt(authentication.getName());
            return service.findByCedula(cedula);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }


    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfilDesdeToken(Authentication authentication) {
        Optional<Usuario> opt = getUsuarioAutenticado(authentication);

        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        return ResponseEntity.ok(opt.get());
    }

    @GetMapping("/historico")
    public ResponseEntity<?> obtenerHistorico(
            @RequestParam(value = "medicoId", required = false) Integer medicoId,
            @RequestParam(value = "estado", required = false) String estado,
            Authentication authentication) {

        Optional<Usuario> optUsuario = getUsuarioAutenticado(authentication);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Usuario usuario = optUsuario.get();
        service.cancelarCitasPasadas();

        List<Cita> citas = service.findAllCitasbyUser(usuario.getId());

        if (medicoId != null && medicoId > 0) {
            citas = citas.stream()
                    .filter(c -> c.getMedico().getId().equals(medicoId))
                    .collect(Collectors.toList());
        }

        if (estado != null && !estado.equalsIgnoreCase("all")) {
            citas = citas.stream()
                    .filter(c -> c.getEstado().equalsIgnoreCase(estado))
                    .collect(Collectors.toList());
        }

        List<Medico> medicos = citas.stream()
                .map(Cita::getMedico)
                .distinct()
                .collect(Collectors.toList());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("citas", citas);
        respuesta.put("medicos", medicos);

        return ResponseEntity.ok(respuesta);
    }


    @GetMapping("/medicosDelPaciente")
    public ResponseEntity<?> obtenerTodosLosMedicosDelPaciente(Authentication authentication) {

        Optional<Usuario> optUsuario = getUsuarioAutenticado(authentication);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Usuario usuario = optUsuario.get();

        List<Cita> citas = service.findAllCitasbyUser(usuario.getId());
        List<Medico> medicos = citas.stream()
                .map(Cita::getMedico)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(medicos);
    }






}
