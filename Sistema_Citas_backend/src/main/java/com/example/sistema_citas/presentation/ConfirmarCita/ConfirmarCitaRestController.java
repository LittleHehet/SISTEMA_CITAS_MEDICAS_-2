package com.example.sistema_citas.presentation.ConfirmarCita;

import com.example.sistema_citas.data.CitaRepository;
import com.example.sistema_citas.logic.*;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
@RestController
@RequestMapping("/api/confirmarCita")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ConfirmarCitaRestController {

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


    @GetMapping("/vistaPrevia")
    public ResponseEntity<?> vistaPreviaCita(
            @RequestParam(name = "medicoId") Integer medicoId,
            @RequestParam(name = "dia") String dia,
            @RequestParam(name = "fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
            @RequestParam(name = "horaInicio") String horaInicio,
            @RequestParam(name = "horaFin") String horaFin,
            Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Optional<Usuario> optUsuario = service.findByCedula(Integer.parseInt(authentication.getName()));
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Optional<Medico> optMedico = service.findMedicoById(medicoId);
        if (optMedico.isEmpty()) {
            return ResponseEntity.status(404).body("Médico no encontrado");
        }

        Medico medico = optMedico.get();

        // Crear un DTO de vista previa
        Map<String, Object> vistaPrevia = new HashMap<>();
        vistaPrevia.put("medico", convertirAMedicoDTO(medico));
        vistaPrevia.put("dia", dia);
        vistaPrevia.put("fecha", fecha);
        vistaPrevia.put("horaInicio", horaInicio);
        vistaPrevia.put("horaFin", horaFin);

        return ResponseEntity.ok(vistaPrevia);
    }




    // ✅ POST: confirmar cita (nuevo REST)
    @PostMapping
    public ResponseEntity<?> confirmarCita(@RequestBody CitaDTO citaDTO, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Integer cedula = Integer.parseInt(authentication.getName());
        Optional<Usuario> optUsuario = service.findByCedula(cedula);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = optUsuario.get();

        Optional<Medico> medicoOptional = service.findMedicoById(citaDTO.getMedicoId());
        if (medicoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Médico no encontrado");
        }

        Medico medico = medicoOptional.get();
        LocalDate fechaInicio = LocalDate.parse(citaDTO.getFecha());

        Cita verificar = service.findCitaByMedicoHorario(
                medico.getId(), citaDTO.getHoraInicio(), citaDTO.getHoraFin(),
                citaDTO.getDia(), fechaInicio
        );

        if (verificar != null && (
                verificar.getEstado().equalsIgnoreCase("pendiente") ||
                        verificar.getEstado().equalsIgnoreCase("aprobado"))) {
            return ResponseEntity.status(409).body("Ya existe una cita en ese horario");
        }

        // Crear y guardar la cita
        Cita nuevaCita = new Cita();
        nuevaCita.setMedico(medico);
        nuevaCita.setUsuario(usuario);
        nuevaCita.setDia(citaDTO.getDia());
        nuevaCita.setHorainicio(citaDTO.getHoraInicio());
        nuevaCita.setHorafinal(citaDTO.getHoraFin());
        nuevaCita.setEstado("pendiente");
        nuevaCita.setNota("NA");
        nuevaCita.setInicio(Integer.parseInt(citaDTO.getHoraInicio().split(":")[0]));
        nuevaCita.setFin(Integer.parseInt(citaDTO.getHoraFin().split(":")[0]));
        nuevaCita.setFechaHora(fechaInicio);

        service.saveCita(nuevaCita);

        return ResponseEntity.ok("Cita confirmada correctamente");
    }

    private MedicoDTO convertirAMedicoDTO(Medico medico) {
        MedicoDTO dto = new MedicoDTO();
        dto.setId(medico.getId());
        dto.setNombre(medico.getUsuarios().getNombre());
        dto.setApellido(medico.getUsuarios().getApellido());
        dto.setEspecialidadNombre(medico.getEspecialidad().getEspecialidadNombre());
        dto.setLocalidadNombre(medico.getLocalidad().getLocalidadNombre());
        dto.setCosto(medico.getCosto());
        // dto.setDisponibilidad(...) si lo necesitas
        return dto;
    }


}
