package com.example.sistema_citas.presentation.historico;


import com.example.sistema_citas.logic.Cita;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;
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

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfilDesdeToken(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }


        Integer cedula = Integer.parseInt(authentication.getName());
        Optional<Usuario> Opt = service.findByCedula(cedula);

        if (Opt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        return ResponseEntity.ok(Opt.get());
    }


    @GetMapping("/historico")
    public ResponseEntity<?> obtenerHistorico(
            @RequestParam(value = "medicoId", required = false) Integer medicoId,
            @RequestParam(value = "estado", required = false) String estado,
            Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Integer cedula = Integer.parseInt(authentication.getName());

        Optional<Usuario> optUsuario = service.findByCedula(cedula);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
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
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Integer cedula = Integer.parseInt(authentication.getName());
        Optional<Usuario> optUsuario = service.findByCedula(cedula);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
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
