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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.*;
import java.util.stream.Collectors;


@org.springframework.stereotype.Controller("medico")
@SessionAttributes("medico")
public class Controller {

    @Autowired
    private Service service;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private LocalidadRepository localidadRepository;


    @GetMapping({"/", "/BuscarCita"})
    public String buscarMedicos(
            @RequestParam(name = "especialidad", required = false) Especialidad especialidad,
            @RequestParam(name = "localidad", required = false) Localidad localidad,
            HttpSession session, HttpServletRequest request, Model model) {

        Usuario usuario = manejarSesionUsuario(session, model);

        if (!usuario.getPerfil().equals("PACIENTE") && !usuario.getPerfil().equals("ANONIMO")) {
            return "redirect:/error";
        }


        guardarUrlActual(request, session, especialidad, localidad);
        cargarFiltros(model);

        List<Medico> medicosFiltrados = filtrarMedicos(especialidad, localidad);
        model.addAttribute("medicos", medicosFiltrados);

        Map<Medico, List<Dia>> disponibilidad = calcularDisponibilidad(medicosFiltrados);
        model.addAttribute("medicosSemana", disponibilidad);
        return "presentation/BuscarCita/view";
    }

    // Métodos auxiliares

    private Usuario manejarSesionUsuario(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) {
            Optional<Usuario> usuarioAnonimo = service.findByCedula(0);
            Usuario usuario = usuarioAnonimo.get();
            model.addAttribute("usuario", usuario);
            session.setAttribute("usuario", usuario);
            System.out.println("Iniciando sesión como anónimo");
            return usuario;
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return usuario;
    }

    private void guardarUrlActual(HttpServletRequest request, HttpSession session,
                                  Especialidad especialidad, Localidad localidad) {
        String urlCompleta = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        if (queryString != null) {
            urlCompleta += "?" + queryString;
        }

        session.setAttribute("prevUrl", urlCompleta);
        System.out.println("Redirigiendo a: " + urlCompleta);

        if (localidad != null) {
            System.out.println("Localidad: " + localidad.getLocalidadNombre());
        }
        if (especialidad != null) {
            System.out.println("Especialidad: " + especialidad.getEspecialidadNombre());
        }
    }

    private void cargarFiltros(Model model) {
        model.addAttribute("especialidades", service.getAllEspecialidades());
        model.addAttribute("localidades", service.getAllLocalidades());
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


    private Map<Medico, List<Dia>> calcularDisponibilidad(List<Medico> medicos) {
        Cont_Citas cc = new Cont_Citas();
        Map<Medico, List<Dia>> disponibilidad = new HashMap<>();
        LocalDate hoy = LocalDate.now();

        for (Medico medico : medicos) {
            List<Dia> semanaCompleta = cc.EstimarSemanaHorario(medico.getHorario(), medico.getFrecuenciaCitas());
            List<Dia> proximosDias = obtenerProximosDiasDisponibles(medico, semanaCompleta, hoy);
            disponibilidad.put(medico, proximosDias);
        }

        return disponibilidad;
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

    private String obtenerNombreDia(LocalDate fecha) {
        String nombreDia = fecha.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return nombreDia.substring(0, 1).toUpperCase() + nombreDia.substring(1).toLowerCase();
    }


}
