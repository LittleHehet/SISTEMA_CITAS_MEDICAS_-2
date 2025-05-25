package com.example.sistema_citas.presentation.ConfirmarCita;

import com.example.sistema_citas.data.CitaRepository;
import com.example.sistema_citas.logic.*;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/ConfirmarCita")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ConfirmarCitaRestController {

    @Autowired
    private Service service;

    @Autowired
    private CitaRepository citaRepository;

    @GetMapping
    public ResponseEntity<?> obtenerDatosCita(
            @RequestParam Integer medicoId,
            @RequestParam String dia,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin,
            HttpSession session
    ) {
//        Usuario usuario = (Usuario) session.getAttribute("usuario");
//        if (usuario == null) {
//            usuario = service.findByCedula(0).orElse(null);
//            session.setAttribute("usuario", usuario);
//        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String cedulaStr = auth.getName(); // o obtener usuario desde token
        Optional<Usuario> usuarioOpt = service.findByCedula(Integer.parseInt(cedulaStr));
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }
        Usuario usuario = usuarioOpt.get();

//        if (!usuario.getPerfil().equals("ROLE_PACIENTE") && !usuario.getPerfil().equals("ROLE_ANONIMO")) {
//            return ResponseEntity.status(403).body("No autorizado");
//        }
//        if (!usuario.getPerfil().equals("ROLE_PACIENTE")) {
//            return ResponseEntity.status(403).body("No autorizado");
//        }
        String perfil = usuario.getPerfil();
        System.out.println(perfil);
        System.out.println(usuario.getNombre());
        if (!"ROLE_PACIENTE".equalsIgnoreCase(perfil)) {
            return ResponseEntity.status(403).body("No autorizado");
        }

        Optional<Medico> medicoOpt = service.findMedicoById(medicoId);
        if (medicoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Médico no encontrado");
        }

        Medico medico = medicoOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("medico", medico);
        response.put("dia", dia);
        response.put("fecha", fecha);
        response.put("horaInicio", horaInicio);
        response.put("horaFin", horaFin);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> confirmarCita(
            @RequestParam Integer medicoId,
            @RequestParam String dia,
            @RequestParam String fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin,
            HttpSession session
    ) {
//        Usuario usuario = (Usuario) session.getAttribute("usuario");
//        if (usuario == null || usuario.getPerfil().equals("ANONIMO")) {
//            return ResponseEntity.status(401).body("Debe iniciar sesión para reservar la cita.");
//        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).body("Debe iniciar sesión para reservar la cita.");
        }
        String cedulaStr = auth.getName();
        Optional<Usuario> usuarioOpt = service.findByCedula(Integer.parseInt(cedulaStr));
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Debe iniciar sesión para reservar la cita.");
        }
        Usuario usuario = usuarioOpt.get();

        Optional<Medico> medicoOpt = service.findMedicoById(medicoId);
        if (medicoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Médico no encontrado");
        }

        Medico medico = medicoOpt.get();
        LocalDate fechaInicio = LocalDate.parse(fecha);

        Cita citaExistente = service.findCitaByMedicoHorario(medicoId, horaInicio, horaFin, dia, fechaInicio);

        if (citaExistente != null && (
                citaExistente.getEstado().equalsIgnoreCase("pendiente") ||
                        citaExistente.getEstado().equalsIgnoreCase("aprobado"))) {
            return ResponseEntity.status(409).body("Este horario ya está reservado");
        }

        Cita cita = new Cita();
        cita.setMedico(medico);
        cita.setUsuario(usuario);
        cita.setDia(dia);
        cita.setHorainicio(horaInicio);
        cita.setHorafinal(horaFin);
        cita.setEstado("pendiente");
        cita.setNota("NA");
        cita.setInicio(Integer.parseInt(horaInicio.split(":")[0]));
        cita.setFin(Integer.parseInt(horaFin.split(":")[0]));
        cita.setFechaHora(fechaInicio);

        citaRepository.save(cita);

        return ResponseEntity.ok("Cita confirmada correctamente");
    }
}
