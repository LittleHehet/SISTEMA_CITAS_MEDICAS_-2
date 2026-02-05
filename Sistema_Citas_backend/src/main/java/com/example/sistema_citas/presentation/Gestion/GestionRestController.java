package com.example.sistema_citas.presentation.Gestion;

import com.example.sistema_citas.logic.*;
import com.example.sistema_citas.logic.DTO.CitaDTO;
import com.example.sistema_citas.logic.DTO.UsuarioConEstadoDTO;
import com.example.sistema_citas.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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

    private Optional<Usuario> getUsuarioAutenticado(Authentication authentication) {
        if (authentication == null) return Optional.empty();

        Object principal = authentication.getPrincipal();

        // Si viene de Google/JWT
        if (principal instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");
            return service.findByGoogleSubOrEmail(sub, email);
        }

        // Si viene login normal (cedula)
        try {
            Integer cedula = Integer.parseInt(authentication.getName());
            return service.findByCedula(cedula);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Obtiene el médico autenticado desde el usuario:
     * - Google: por relación Medico -> Usuario (findByUsuario)
     * - Local: por cédula (findMedicobyCedula)
     */
    private Medico getMedicoAutenticadoOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> optUsuario = getUsuarioAutenticado(auth);

        if (optUsuario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        Usuario usuario = optUsuario.get();

        // Validar rol
        String perfil = usuario.getPerfil();
        if (perfil == null || !perfil.equalsIgnoreCase("ROLE_MEDICO")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        // 1) Intentar por relación usuario -> medico (ideal para Google)
        Optional<Medico> medicoOpt = service.findMedicoByUsuario(usuario);
        if (medicoOpt.isPresent()) return medicoOpt.get();

        // 2) Fallback: si tiene cédula (ideal para login local)
        if (usuario.getCedula() != null) {
            Optional<Medico> medicoCedulaOpt = service.findMedicobyCedula(usuario.getCedula());
            if (medicoCedulaOpt.isPresent()) return medicoCedulaOpt.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el médico autenticado");
    }

    @GetMapping("/citas")
    public List<CitaDTO> obtenerCitasFiltradas(
            @RequestParam(value = "usuarioId", required = false) Integer usuarioId,
            @RequestParam(value = "estado", required = false) String estado
    ) {
        service.cancelarCitasPasadas();

        Medico medico = getMedicoAutenticadoOrThrow();
        List<Cita> citas = service.findAllCitasbyMedico(medico.getId());

        if (usuarioId != null && usuarioId > 0) {
            citas = citas.stream()
                    .filter(c -> c.getUsuario() != null && c.getUsuario().getId().equals(usuarioId))
                    .collect(Collectors.toList());
        }

        if (estado != null && !estado.equalsIgnoreCase("all")) {
            citas = citas.stream()
                    .filter(c -> c.getEstado() != null && c.getEstado().equalsIgnoreCase(estado))
                    .collect(Collectors.toList());
        }

        return citas.stream()
                .map(CitaDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/usuarios")
    public List<UsuarioConEstadoDTO> obtenerUsuariosDeCitas() {
        Medico medico = getMedicoAutenticadoOrThrow();

        List<Cita> citas = service.findAllCitasbyMedico(medico.getId());
        return citas.stream()
                .map(Cita::getUsuario)
                .filter(u -> u != null)
                .distinct()
                .map(UsuarioConEstadoDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/completar")
    public void completarCita(@RequestParam Integer id) {
        // opcional: podrías validar que el médico autenticado sea dueño de esa cita
        getMedicoAutenticadoOrThrow();
        service.cambiarEstadoCita(id, "completada");
    }

    @PostMapping("/cancelar")
    public void cancelarCita(@RequestParam Integer id) {
        getMedicoAutenticadoOrThrow();
        service.cambiarEstadoCita(id, "cancelada");
    }

    @GetMapping("/cita")
    public CitaDTO obtenerCita(@RequestParam Integer id) {
        getMedicoAutenticadoOrThrow();

        Optional<Cita> citaOpt = service.findCitaById(id);
        if (citaOpt.isPresent()) return new CitaDTO(citaOpt.get());

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada");
    }

    @PostMapping("/nota")
    public ResponseEntity<Void> guardarNota(@RequestParam Integer id, @RequestParam String nota) {
        getMedicoAutenticadoOrThrow();

        Optional<Cita> citaOptional = service.findCitaById(id);
        if (citaOptional.isEmpty()) return ResponseEntity.notFound().build();

        Cita cita = citaOptional.get();
        cita.setNota(nota);
        service.saveCita(cita);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notaYCompletar")
    public ResponseEntity<Void> guardarNotaYCompletar(@RequestParam Integer id, @RequestParam String nota) {
        getMedicoAutenticadoOrThrow();

        Optional<Cita> citaOptional = service.findCitaById(id);
        if (citaOptional.isEmpty()) return ResponseEntity.notFound().build();

        Cita cita = citaOptional.get();
        cita.setNota(nota);
        cita.setEstado("completada");
        service.saveCita(cita);
        return ResponseEntity.ok().build();
    }
}
