package com.example.sistema_citas.presentation.HorarioExtendido;

import com.example.sistema_citas.logic.*;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller("horarioextendido")
@SessionAttributes("horarioextendido")
public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/HorarioExtendido")
    public String HorarioExtendido(@RequestParam(name = "medicoId", required = false) Integer medicoId,
                                   HttpSession session,
                                   Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

        if (!esPacienteOAnonimo(usuario)) {
            return "redirect:/error";
        }

        Optional<Medico> om = service.findMedicoById(medicoId);
        if (om.isEmpty()) {
            return "redirect:/error";
        }

        Medico medico = om.get();
        model.addAttribute("medico", medico);

        Map<Medico, List<Dia>> medicoSemana = new HashMap<>();
        Cont_Citas contadorCitas = new Cont_Citas();

        List<Dia> semanaCompleta = calcularDisponibilidad(medico);
        List<Dia> proximosDias = obtenerProximosDiasDisponibles(medico, semanaCompleta , LocalDate.now());

        medicoSemana.put(medico, proximosDias);
        model.addAttribute("medicoSemana", medicoSemana);
        model.addAttribute("semanaCompleta", semanaCompleta);

        return "presentation/HorarioExtendido/view";
    }


    private List<Dia> calcularDisponibilidad(Medico medico) {
        Cont_Citas cc = new Cont_Citas();
        LocalDate hoy = LocalDate.now();

        List<Dia> semanaCompleta = cc.EstimarSemanaHorario(medico.getHorario(), medico.getFrecuenciaCitas());
        List<Dia> proximosDias = obtenerProximosDiasDisponibles(medico, semanaCompleta, hoy);

        return proximosDias;
    }




    private List<Dia> obtenerProximosDiasDisponibles(Medico medico, List<Dia> semanaCompleta, LocalDate fechaBase) {
        List<Dia> diasDisponibles = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {
            LocalDate fecha = fechaBase.plusDays(i);
            String nombreDia = obtenerNombreDia(fecha);

            semanaCompleta.stream()
                    .filter(d -> d.getNombre().equals(nombreDia))
                    .findFirst()
                    .ifPresent(diaOriginal -> {
                        // Crear una copia del día para no modificar el original
                        Dia diaDisponible = new Dia(diaOriginal.getNombre(), new ArrayList<>());
                        diaDisponible.setMedico(medico);
                        diaDisponible.setFecha(fecha);

                        // Filtrar horarios disponibles
                        List<CalcularHorario> horariosDisponibles = diaOriginal.getHorarios().stream()
                                .filter(horario -> {
                                    // Verificar si existe una cita activa para este horario
                                    Cita citaExistente = service.findCitaByMedicoHorario(
                                            medico.getId(),
                                            horario.getHorainicio(),
                                            horario.getHorafin(),
                                            nombreDia,
                                            fecha
                                    );

                                    // El horario está disponible si no hay cita o si está cancelada
                                    return citaExistente == null || "cancelada".equalsIgnoreCase(citaExistente.getEstado());
                                })
                                .collect(Collectors.toList());

                        diaDisponible.setHorarios(horariosDisponibles);

                        // Solo agregar el día si tiene horarios disponibles
                        if (!horariosDisponibles.isEmpty()) {
                            diasDisponibles.add(diaDisponible);
                        }
                    });
        }

        return diasDisponibles;
    }

    private boolean esPacienteOAnonimo(Usuario usuario) {
        if (!usuario.getPerfil().equals("PACIENTE") && !usuario.getPerfil().equals("ANONIMO")) {
            return false;
        }
        return true;
    }

    private String obtenerNombreDia(LocalDate fecha) {
        String diaNombre = fecha.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();
    }



}

