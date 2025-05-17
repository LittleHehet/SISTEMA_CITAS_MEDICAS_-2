package com.example.sistema_citas.presentation.ConfirmarCita;

import com.example.sistema_citas.data.CitaRepository;
import com.example.sistema_citas.logic.*;
import com.example.sistema_citas.data.EspecialidadRepository;
import com.example.sistema_citas.data.LocalidadRepository;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@org.springframework.stereotype.Controller("confirmarCita")
@SessionAttributes("medico")
public class Controller {

    @Autowired
    private Service service; // Servicio existente para la lógica de negocio

    @Autowired
    private EspecialidadRepository especialidadRepository; // Repositorio de especialidades

    @Autowired
    private LocalidadRepository localidadRepository; // Repositorio de localidades
    @Qualifier("enableGlobalAuthenticationAutowiredConfigurer")
    @Autowired
    private GlobalAuthenticationConfigurerAdapter enableGlobalAuthenticationAutowiredConfigurer;

    @Autowired
    private CitaRepository citaRepository;

//    @GetMapping("/ConfirmarCita")
//    public String confirmarCita(
//            @RequestParam(name = "especialidad", required = false)
//            Especialidad especialidad,
//            @RequestParam(name = "localidad", required = false)
//            Localidad localidad,
//            HttpSession session,
//            HttpServletRequest request,Model model) {
//
////        // Guardar la URL completa con los parámetros en la sesión
////        String urlCompleta = request.getRequestURL().toString();
////        String queryString = request.getQueryString();
////        if (queryString != null) {
////            urlCompleta += "?" + queryString; // Agregar los parámetros de la consulta a la URL
////        }
////        session.setAttribute("prevUrl", urlCompleta);
////        System.out.println("Redirigiendo a: " + urlCompleta);
////
////        // Filtra los médicos por especialidad y localidad si se han seleccionado
////        model.addAttribute("especialidades", service.getAllEspecialidades());
////        model.addAttribute("localidades", service.getAllLocalidades());
////        if (especialidad == null && localidad == null) {
////            model.addAttribute("medicos", service.findAllMedicosEyL());
////        } else {
////            if (especialidad != null && localidad == null) {
////                model.addAttribute("medicos", service.findMedicobyEspecialidad(especialidad));
////            } else {
////                if (especialidad == null && localidad != null) {
////                    model.addAttribute("medicos", service.findMedicobyLocalidad(localidad));
////                } else {
////                    model.addAttribute("medicos", service.findMedicobyLocalidadAndEspecialidad(especialidad, localidad));
////                }
////            }
////        }
//        // Aquí se redirige a la vista correspondiente
//        return "presentation/ConfirmarCita/view"; // La vista que muestra la confirmación de la cita
//    }
@GetMapping("/ConfirmarCita")
public String confirmarCita(
        @RequestParam(name = "medicoId", required = false) Integer medicoId,  // Parámetro para el ID del médico (Integer)
        @RequestParam(name = "dia", required = false) String dia,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
        @RequestParam(name = "horaInicio", required = false) String horaInicio,  // Parámetro para la hora de inicio
        @RequestParam(name = "horaFin", required = false) String horaFin,  // Parámetro para la hora de fin\

        Model model) {

    // Lógica para cargar el médico y otros detalles
    if (medicoId != null && dia != null && horaInicio != null && horaFin != null) {
        // Cargar el médico usando su ID
        Optional<Medico> medicoOptional = service.findMedicoById(medicoId);
        if (medicoOptional.isPresent()) {
            Medico medico = medicoOptional.get();  // Obtener el médico desde el Optional
            model.addAttribute("medico", medico);  // Pasar el médico al modelo
            model.addAttribute("dia", dia);  // Pasar el día
            model.addAttribute("fecha", fecha);
            model.addAttribute("horaInicio", horaInicio);  // Pasar la hora de inicio
            model.addAttribute("horaFin", horaFin);  // Pasar la hora de fin

        } else {
            // Si no se encuentra el médico, puedes redirigir o mostrar un mensaje de error
            return "error";  // O una vista de error específica
        }
    }

    // Aquí se retorna la vista para confirmar la cita
    return "presentation/ConfirmarCita/view"; // La vista donde se muestra la confirmación
}

//    @PostMapping("/ConfirmarCita")
//    public String confirmarCita(@ModelAttribute("medico")Medico medico, Model model){
//        model.addAttribute("medico", medico);
//        return "presentation/ConfirmarCita/view";
//    }
@PostMapping("/ConfirmarCita")
public String confirmarCita(@ModelAttribute("medico") Medico medico,
                            @RequestParam(name = "dia") String dia,
                            @RequestParam(name = "fecha") String fecha,
                            @RequestParam(name = "horaInicio") String horaInicio,
                            @RequestParam(name = "horaFin") String horaFin,
                            HttpSession session,   HttpServletRequest request,
                            Model model) {

    // Obtener el usuario de la sesión (suponiendo que ya está almacenado en la sesión)
    Usuario usuario = (Usuario) session.getAttribute("usuario");

    if (usuario == null || Objects.equals(usuario.getPerfil(), "ANONIMO")) {
        return "redirect:/Sign-in";
    }

    Cita verificar = service.findCitaByMedicoHorario(medico.getId(),horaInicio,horaFin,dia);

if(verificar !=null && verificar.getEstado().equals("pendiente") || verificar !=null && verificar.getEstado().equals("aprobado")){
    return "presentation/ConfirmarCita/rechazada";
}
else {
    // Crear una nueva cita con los valores proporcionados
    Cita cita = new Cita();
    cita.setMedico(medico);
    cita.setUsuario(usuario);
    //cita.setDia(dia != null ? dia : "Lunes");  // Valor por defecto: "Lunes"
    cita.setDia(dia);
    cita.setHorainicio(horaInicio);
    cita.setHorafinal(horaFin);
    cita.setEstado("pendiente");  // Estado predeterminado
    cita.setNota("NA");  // Nota predeterminada
    //dudas
    cita.setInicio(Integer.parseInt(horaInicio.split(":")[0]));  // Extracting hour from "horaInicio"
    cita.setFin(Integer.parseInt(horaFin.split(":")[0]));  // Extracting hour from "horaFin"
    cita.setFechaHora(LocalDate.parse(fecha));
    // Guardar la cita en la base de datos
    citaRepository.save(cita);

    // Agregar al modelo la cita guardada
    model.addAttribute("cita", cita);

    // Redirigir o mostrar vista de confirmación
    return "presentation/ConfirmarCita/confirmada";
    }// Redirigir a la página de confirmación
}


}
