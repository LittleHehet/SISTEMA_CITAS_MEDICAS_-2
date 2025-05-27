package com.example.sistema_citas.presentation.HorarioExtendido;

import com.example.sistema_citas.logic.*;
import com.example.sistema_citas.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/HorarioExtendido")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class HorarioExtendidoRestController {

    @Autowired
    private Service service;

    @GetMapping
    public ResponseEntity<?> obtenerHorarioExtendido(@RequestParam("medicoId") Integer medicoId) {
        Optional<Medico> om = service.findMedicoById(medicoId);
        if (om.isEmpty()) {
            return ResponseEntity.status(404).body("Médico no encontrado");
        }

        Medico medico = om.get();

        // Datos del médico
        Map<String, Object> medicoMap = new HashMap<>();
        medicoMap.put("id", medico.getId());
        medicoMap.put("nombre", medico.getUsuarios().getNombre());
        medicoMap.put("apellido", medico.getUsuarios().getApellido());
        medicoMap.put("especialidad", medico.getEspecialidad() != null ? medico.getEspecialidad().getEspecialidadNombre() : null);
        medicoMap.put("localidad", medico.getLocalidad() != null ? medico.getLocalidad().getLocalidadNombre() : null);

        // Cálculo de la semana completa
        Cont_Citas cc = new Cont_Citas();
        List<Dia> semanaCompleta = cc.EstimarSemanaHorario(medico.getHorario(), medico.getFrecuenciaCitas());
        List<Dia> proximosDias = obtenerDiasDisponibles(medico, semanaCompleta, LocalDate.now());

        // Transformar a estructura para el frontend
        List<Map<String, Object>> dias = proximosDias.stream().map(dia -> {
            Map<String, Object> diaMap = new HashMap<>();
            diaMap.put("nombre", dia.getNombre());
            diaMap.put("fecha", dia.getFecha());
            diaMap.put("horarios", dia.getHorarios().stream().map(h -> Map.of(
                    "horainicio", h.getHorainicio(),
                    "horafin", h.getHorafin()
            )).collect(Collectors.toList()));
            return diaMap;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("medico", medicoMap);
        response.put("semanaCompleta", dias);

        return ResponseEntity.ok(response);
    }

    private List<Dia> obtenerDiasDisponibles(Medico medico, List<Dia> semanaCompleta, LocalDate fechaBase) {
        List<Dia> diasDisponibles = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {
            LocalDate fecha = fechaBase.plusDays(i);

            // Paso 1: obtener el nombre original del día
            final String rawNombreDia = fecha.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

            // Paso 2: formatear correctamente
            final String nombreDia = rawNombreDia.substring(0, 1).toUpperCase() + rawNombreDia.substring(1).toLowerCase();

            semanaCompleta.stream()
                    .filter(d -> d.getNombre().equals(nombreDia))
                    .findFirst()
                    .ifPresent(diaOriginal -> {
                        Dia diaDisponible = new Dia(diaOriginal.getNombre(), new ArrayList<>());
                        diaDisponible.setMedico(medico);
                        diaDisponible.setFecha(fecha);

                        List<CalcularHorario> horariosDisponibles = diaOriginal.getHorarios().stream()
                                .filter(horario -> {
                                    Cita cita = service.findCitaByMedicoHorario(
                                            medico.getId(),
                                            horario.getHorainicio(),
                                            horario.getHorafin(),
                                            nombreDia,
                                            fecha
                                    );
                                    return cita == null || "cancelada".equalsIgnoreCase(cita.getEstado());
                                })
                                .collect(Collectors.toList());

                        diaDisponible.setHorarios(horariosDisponibles);

                        if (!horariosDisponibles.isEmpty()) {
                            diasDisponibles.add(diaDisponible);
                        }
                    });
        }


        return diasDisponibles;
    }
}


