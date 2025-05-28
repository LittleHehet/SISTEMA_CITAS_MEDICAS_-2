package com.example.sistema_citas.presentation.BuscarCita;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import com.example.sistema_citas.logic.*;
import com.example.sistema_citas.data.EspecialidadRepository;
import com.example.sistema_citas.data.LocalidadRepository;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/BuscarCita")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BuscarCitaRestController {

    @Autowired
    private Service service;

    @GetMapping("/busqueda")
    public Map<String, Object> buscarMedicos(
            @RequestParam(required = false) Integer especialidadId,
            @RequestParam(required = false) Integer localidadId) {

        // Obtener objetos si vienen los IDs
        Especialidad especialidad = (especialidadId != null)
                ? service.getAllEspecialidades().stream()
                .filter(e -> e.getId().equals(especialidadId))
                .findFirst().orElse(null)
                : null;

        Localidad localidad = (localidadId != null)
                ? service.getAllLocalidades().stream()
                .filter(l -> l.getId().equals(localidadId))
                .findFirst().orElse(null)
                : null;

        // Filtrar médicos
        List<Medico> medicosFiltrados = filtrarMedicos(especialidad, localidad);
        List<MedicoDTO> medicosDTO = medicosFiltrados.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Enviar todo en una sola respuesta
        return Map.of(
                "medicos", medicosDTO,
                "especialidades", service.getAllEspecialidades(),
                "localidades", service.getAllLocalidades()
        );
    }

    private List<Medico> filtrarMedicos(Especialidad especialidad, Localidad localidad) {
        if (especialidad == null && localidad == null) {
            return service.findAllMedicosEyL();
        } else if (especialidad != null && localidad == null) {
            return service.findMedicobyEspecialidad(especialidad);
        } else if (especialidad == null && localidad != null) {
            return service.findMedicobyLocalidad(localidad);
        } else {
            return service.findMedicobyLocalidadAndEspecialidad(especialidad, localidad);
        }
    }

    private MedicoDTO convertToDTO(Medico medico) {
        Cont_Citas cc = new Cont_Citas();
        LocalDate hoy = LocalDate.now();
        List<Dia> semanaCompleta = cc.EstimarSemanaHorario(medico.getHorario(), medico.getFrecuenciaCitas());
        List<Dia> proximosDias = obtenerProximosDiasDisponibles(medico, semanaCompleta, hoy);

//        List<DiaDTO> disponibilidadDTO = proximosDias.stream()
//                .map(dia -> new DiaDTO(
//                        dia.getNombre(),
//                        dia.getFecha(),
//                        dia.getHorarios().stream()
//                                .map(h -> new CalcularHorarioDTO(h.getHorainicio(), h.getHorafin()))
//                                .collect(Collectors.toList())
//                )).collect(Collectors.toList());
        List<DiaDTO> disponibilidadDTO = proximosDias.stream()
                .map(dia -> new DiaDTO(
                        dia.getNombre(),
                        dia.getFecha(),
                        dia.getHorarios().stream()
                                .map(h -> new CalcularHorarioDTO(h.getHorainicio(), h.getHorafin(), h.isReservado()))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        System.out.println("=== DEBUG MÉDICO ===");
        System.out.println("ID: " + medico.getId());

        System.out.println("Usuario: " +
                (medico.getUsuarios() != null ? medico.getUsuarios().getNombre() + " " + medico.getUsuarios().getApellido() : "null"));

        System.out.println("Especialidad: " +
                (medico.getEspecialidad() != null ? medico.getEspecialidad().getEspecialidadNombre() : "null"));

        System.out.println("Localidad: " +
                (medico.getLocalidad() != null ? medico.getLocalidad().getLocalidadNombre() : "null"));

        return new MedicoDTO(
                medico.getId(),
                medico.getUsuarios().getNombre(),
                medico.getUsuarios().getApellido(),
                medico.getEspecialidad() != null ? medico.getEspecialidad().getEspecialidadNombre() : null,
                medico.getLocalidad() != null ? medico.getLocalidad().getLocalidadNombre() : null,
                medico.getCosto(),
                disponibilidadDTO
        );

    }

    private List<Dia> obtenerProximosDiasDisponibles(Medico medico, List<Dia> semanaCompleta, LocalDate fechaBase) {
        List<Dia> diasDisponibles = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            LocalDate fecha = fechaBase.plusDays(i);
            String nombreDia = obtenerNombreDia(fecha);

            semanaCompleta.stream()
                    .filter(d -> d.getNombre().equals(nombreDia))
                    .findFirst()
                    .ifPresent(diaOriginal -> {
                        Dia diaDisponible = new Dia(diaOriginal.getNombre(), new ArrayList<>());
                        diaDisponible.setMedico(medico);
                        diaDisponible.setFecha(fecha);

//                        List<CalcularHorario> horariosDisponibles = diaOriginal.getHorarios().stream()
//                                .filter(horario -> {
//                                    Cita cita = service.findCitaByMedicoHorario(
//                                            medico.getId(),
//                                            horario.getHorainicio(),
//                                            horario.getHorafin(),
//                                            nombreDia,
//                                            fecha
//                                    );
//                                    return cita == null || "cancelada".equalsIgnoreCase(cita.getEstado());
//                                })
//                                .collect(Collectors.toList());
                        List<CalcularHorario> horariosDisponibles = diaOriginal.getHorarios().stream()
                                .peek(horario -> {
                                    Cita cita = service.findCitaByMedicoHorario(
                                            medico.getId(),
                                            horario.getHorainicio(),
                                            horario.getHorafin(),
                                            nombreDia,
                                            fecha
                                    );
                                    if (cita != null && !"cancelada".equalsIgnoreCase(cita.getEstado())) {
                                        horario.setReservado(true);
                                    }
                                }).collect(Collectors.toList());

                        diaDisponible.setHorarios(horariosDisponibles);

                        if (!horariosDisponibles.isEmpty()) {
                            diasDisponibles.add(diaDisponible);
                        }
                    });
        }

        return diasDisponibles;
    }

    private String obtenerNombreDia(LocalDate fecha) {
        String nombreDia = fecha.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return nombreDia.substring(0, 1).toUpperCase() + nombreDia.substring(1).toLowerCase();
    }
}

