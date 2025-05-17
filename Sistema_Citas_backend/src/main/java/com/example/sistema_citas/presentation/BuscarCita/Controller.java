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


@org.springframework.stereotype.Controller("medico")
@SessionAttributes("medico")
public class Controller {

    @Autowired
    private Service service; // Servicio existente para la lógica de negocio

    @Autowired
    private EspecialidadRepository especialidadRepository; // Repositorio de especialidades

    @Autowired
    private LocalidadRepository localidadRepository; // Repositorio de localidades

    @GetMapping("/BuscarCita")
    public String buscarMedicos(@RequestParam(name = "especialidad", required = false)
                                    Especialidad especialidad,
                                @RequestParam(name = "localidad", required = false)
                                Localidad localidad,
                                HttpSession session,
                                HttpServletRequest request,
                                Model model) {

        if(session.getAttribute("usuario") == null) {
            Optional<Usuario> usuario = service.findByCedula(0);
            model.addAttribute("usuario", usuario.get());
            session.setAttribute("usuario", usuario.get());
            System.out.println("Iniciando session Como anomino");
        }
        else{
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            model.addAttribute("usuario", usuario);
        }

        // Guardar la URL completa con los parámetros en la sesión
        String urlCompleta = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {
            urlCompleta += "?" + queryString; // Agregar los parámetros de la consulta a la URL
        }
        session.setAttribute("prevUrl", urlCompleta);
        System.out.println("Redirigiendo a: " + urlCompleta);
        if(localidad != null) {
            System.out.println("=============================Localidad: " + localidad.getLocalidadNombre());
        }
        if(especialidad != null) {
            System.out.println("=============================Especialidad: " + especialidad.getEspecialidadNombre());
        }
        // Filtra los médicos por especialidad y localidad si se han seleccionado
        model.addAttribute("especialidades", service.getAllEspecialidades());
        model.addAttribute("localidades", service.getAllLocalidades());
        //List<Dia> Semana = new ArrayList<>();
        Cont_Citas cc = new Cont_Citas();
        if (especialidad == null && localidad == null) {
            model.addAttribute("medicos", service.findAllMedicosEyL());
            List<Medico> lista = service.findAllMedicosEyL();

//==============================================VIEJO FOR DE HORARIOS NO BORRAR POR AHORA==============================================================================
//            for (Medico m : lista) {
//                List<Dia> Semana = cc.EstimarSemanaHorario(m.getHorario(), m.getFrecuenciaCitas());
//                for(Dia d : Semana){
//                    d.setMedico(m);
//                    //m.setDia(d);
//                }
//                model.addAttribute("semana", Semana);
//            }
//            Map<Medico, List<Dia>> medicosSemana = new HashMap<>();
//
//            for (Medico m : lista) {
//                List<Dia> Semana = cc.EstimarSemanaHorario(m.getHorario(), m.getFrecuenciaCitas());
//                for (Dia d : Semana) {
//                    d.setMedico(m); // Asociamos el médico al día
//                }
//                medicosSemana.put(m, Semana); // Almacenamos la lista de días para cada médico
//            }
//============================================= LOCAL DATE DE ALEXIA NO BORRAR==================================================================

            Map<Medico, List<Dia>> medicosSemana = new HashMap<>();
            LocalDate today = LocalDate.now();
            List<String> diasProximos = new ArrayList<>();

// Del día siguiente al de hoy, por 3 días
            for (int i = 1; i <= 3; i++) {
                String diaNombre = today.plusDays(i).getDayOfWeek()
                        .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                // Capitaliza primera letra
                diaNombre = diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();
                diasProximos.add(diaNombre);
            }

            for (Medico m : lista) {
                List<Dia> semanaCompleta = cc.EstimarSemanaHorario(m.getHorario(), m.getFrecuenciaCitas());
                List<Dia> proximosDias = new ArrayList<>();

                for (int i = 1; i <= 3; i++) {
                    LocalDate fecha = today.plusDays(i);
                    String diaNombre = fecha.getDayOfWeek()
                            .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                    diaNombre = diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();

                    // Buscar el día correspondiente en la semana del médico
                    for (Dia d : semanaCompleta) {
                        if (d.getNombre().equals(diaNombre)) {
                            d.setMedico(m);
                            d.setFecha(fecha); // Aquí le asignamos la fecha real
                            proximosDias.add(d);
                            break;
                        }
                    }
                }

                medicosSemana.put(m, proximosDias);
            }
            model.addAttribute("medicosSemana", medicosSemana);
//============================================= LOCAL DATE DE ALEXIA NO BORRAR==================================================================

        } else {
            if(especialidad != null && localidad == null){
                model.addAttribute("medicos", service.findMedicobyEspecialidad(especialidad));
                List<Medico> lista = service.findMedicobyEspecialidad(especialidad);

//============================================= LOCAL DATE DE ALEXIA NO BORRAR==================================================================
                Map<Medico, List<Dia>> medicosSemana = new HashMap<>();
                LocalDate today = LocalDate.now();
                List<String> diasProximos = new ArrayList<>();

                // Del día siguiente al de hoy, por 3 días
                for (int i = 1; i <= 3; i++) {
                    String diaNombre = today.plusDays(i).getDayOfWeek()
                            .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                    // Capitaliza primera letra
                    diaNombre = diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();
                    diasProximos.add(diaNombre);
                }

                for (Medico m : lista) {
                    List<Dia> semanaCompleta = cc.EstimarSemanaHorario(m.getHorario(), m.getFrecuenciaCitas());
                    List<Dia> proximosDias = new ArrayList<>();

                    for (int i = 1; i <= 3; i++) {
                        LocalDate fecha = today.plusDays(i);
                        String diaNombre = fecha.getDayOfWeek()
                                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                        diaNombre = diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();

                        // Buscar el día correspondiente en la semana del médico
                        for (Dia d : semanaCompleta) {
                            if (d.getNombre().equals(diaNombre)) {
                                d.setMedico(m);
                                d.setFecha(fecha); // Aquí le asignamos la fecha real
                                proximosDias.add(d);
                                break;
                            }
                        }
                    }

                    medicosSemana.put(m, proximosDias);
                }
                model.addAttribute("medicosSemana", medicosSemana);
//=============================================================================================================================
            }
            else{
                if(especialidad == null && localidad != null){
                    model.addAttribute("medicos",service.findMedicobyLocalidad(localidad));
                    List<Medico> lista = service.findMedicobyLocalidad(localidad);

//============================================= LOCAL DATE DE ALEXIA NO BORRAR==================================================================
                    Map<Medico, List<Dia>> medicosSemana = new HashMap<>();
                    LocalDate today = LocalDate.now();
                    List<String> diasProximos = new ArrayList<>();

// Del día siguiente al de hoy, por 3 días
                    for (int i = 1; i <= 3; i++) {
                        String diaNombre = today.plusDays(i).getDayOfWeek()
                                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                        // Capitaliza primera letra
                        diaNombre = diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();
                        diasProximos.add(diaNombre);
                    }

                    for (Medico m : lista) {
                        List<Dia> semanaCompleta = cc.EstimarSemanaHorario(m.getHorario(), m.getFrecuenciaCitas());
                        List<Dia> proximosDias = new ArrayList<>();

                        for (int i = 1; i <= 3; i++) {
                            LocalDate fecha = today.plusDays(i);
                            String diaNombre = fecha.getDayOfWeek()
                                    .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                            diaNombre = diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();

                            // Buscar el día correspondiente en la semana del médico
                            for (Dia d : semanaCompleta) {
                                if (d.getNombre().equals(diaNombre)) {
                                    d.setMedico(m);
                                    d.setFecha(fecha); // Aquí le asignamos la fecha real
                                    proximosDias.add(d);
                                    break;
                                }
                            }
                        }

                        medicosSemana.put(m, proximosDias);
                    }
                    model.addAttribute("medicosSemana", medicosSemana);
//==================================================================================================================================================
                }
                else{
                    model.addAttribute("medicos",service.findMedicobyLocalidadAndEspecialidad(especialidad,localidad));
                    List<Medico> lista = service.findMedicobyLocalidadAndEspecialidad(especialidad,localidad);

 //============================================= LOCAL DATE DE ALEXIA NO BORRAR==================================================================
                    Map<Medico, List<Dia>> medicosSemana = new HashMap<>();
                    LocalDate today = LocalDate.now();
                    List<String> diasProximos = new ArrayList<>();

// Del día siguiente al de hoy, por 3 días
                    for (int i = 1; i <= 3; i++) {
                        String diaNombre = today.plusDays(i).getDayOfWeek()
                                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                        // Capitaliza primera letra
                        diaNombre = diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();
                        diasProximos.add(diaNombre);
                    }

                    for (Medico m : lista) {
                        List<Dia> semanaCompleta = cc.EstimarSemanaHorario(m.getHorario(), m.getFrecuenciaCitas());
                        List<Dia> proximosDias = new ArrayList<>();

                        for (int i = 1; i <= 3; i++) {
                            LocalDate fecha = today.plusDays(i);
                            String diaNombre = fecha.getDayOfWeek()
                                    .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                            diaNombre = diaNombre.substring(0, 1).toUpperCase() + diaNombre.substring(1).toLowerCase();

                            // Buscar el día correspondiente en la semana del médico
                            for (Dia d : semanaCompleta) {
                                if (d.getNombre().equals(diaNombre)) {
                                    d.setMedico(m);
                                    d.setFecha(fecha); // Aquí le asignamos la fecha real
                                    proximosDias.add(d);
                                    break;
                                }
                            }
                        }

                        medicosSemana.put(m, proximosDias);
                    }
                    model.addAttribute("medicosSemana", medicosSemana);
//==================================================================================================================================================
                }
            }
        }

        return "presentation/BuscarCita/view"; // La vista que muestra los médicos
    }

}


//Nota: no borrar de momento: update medicos SET estado = 'aprobado' Where medico_id = 3;
