package com.example.sistema_citas.presentation.Gestion;

import com.example.sistema_citas.logic.*;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gestion")
public class GestionRestController {

    @Autowired
    private Service service;

    @GetMapping("/citas")
    public List<CitaDTO> obtenerCitasFiltradas(@RequestParam(value = "usuarioId", required = false) Integer usuarioId,
                                               @RequestParam(value = "estado", required = false) String estado,
                                               HttpSession session) {

        service.cancelarCitasPasadas();
        Medico medico = (Medico) session.getAttribute("medico");

        if (medico == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            try {
                Integer cedula = Integer.parseInt(auth.getName());
                Optional<Medico> medicoOptional = service.findMedicobyCedula(cedula);
                if (medicoOptional.isPresent()) {
                    medico = medicoOptional.get();
                    session.setAttribute("medico", medico);
                } else {
                    throw new RuntimeException("No se encontró el médico autenticado");
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cédula inválida en sesión");
            }
        }

        List<Cita> citas = service.findAllCitasbyMedico(medico.getId());

        if (usuarioId != null && usuarioId > 0) {
            citas = citas.stream()
                    .filter(c -> c.getUsuario().getId().equals(usuarioId))
                    .collect(Collectors.toList());
        }

        if (estado != null && !estado.equals("all")) {
            citas = citas.stream()
                    .filter(c -> c.getEstado().equalsIgnoreCase(estado))
                    .collect(Collectors.toList());
        }

        // Convertir a DTOs antes de devolver
        return citas.stream()
                .map(CitaDTO::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/usuarios")
    public List<UsuarioConEstadoDTO> obtenerUsuariosDeCitas(HttpSession session) {
        Medico medico = (Medico) session.getAttribute("medico");
        if (medico == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            try {
                Integer cedula = Integer.parseInt(auth.getName());
                Optional<Medico> medicoOptional = service.findMedicobyCedula(cedula);
                if (medicoOptional.isPresent()) {
                    medico = medicoOptional.get();
                    session.setAttribute("medico", medico);
                } else {
                    throw new RuntimeException("No se encontró el médico autenticado");
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cédula inválida en sesión");
            }
        }

        List<Cita> citas = service.findAllCitasbyMedico(medico.getId());
        return citas.stream()
                .map(Cita::getUsuario)
                .distinct()
                .map(UsuarioConEstadoDTO::new)
                .collect(Collectors.toList());
    }



    @PostMapping("/completar")
    public void completarCita(@RequestParam Integer id) {
        service.cambiarEstadoCita(id, "completada");
    }

    @PostMapping("/cancelar")
    public void cancelarCita(@RequestParam Integer id) {
        service.cambiarEstadoCita(id, "cancelada");
    }

    @GetMapping("/cita")
    public CitaDTO obtenerCita(@RequestParam Integer id) {
        Optional<Cita> citaOpt = service.findCitaById(id);
        if (citaOpt.isPresent()) {
            return new CitaDTO(citaOpt.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada");
        }
    }


    @PostMapping("/nota")
    public void guardarNota(@RequestParam Integer id, @RequestParam String nota) {
        Optional<Cita> citaOptional = service.findCitaById(id);
        if (citaOptional.isPresent()) {
            Cita cita = citaOptional.get();
            cita.setNota(nota);
            service.saveCita(cita);
        }
    }
}
