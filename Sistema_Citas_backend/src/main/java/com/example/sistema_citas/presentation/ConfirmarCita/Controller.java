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

import java.time.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@org.springframework.stereotype.Controller("confirmarCita")
@SessionAttributes("medico")
public class Controller {

    @Autowired
    private Service service;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private LocalidadRepository localidadRepository;
    @Qualifier("enableGlobalAuthenticationAutowiredConfigurer")
    @Autowired
    private GlobalAuthenticationConfigurerAdapter enableGlobalAuthenticationAutowiredConfigurer;

    @Autowired
    private CitaRepository citaRepository;


    @GetMapping("/ConfirmarCita")
    public String confirmarCita(
            @RequestParam(name = "medicoId", required = false) Integer medicoId,  // Parámetro para el ID del médico (Integer)
            @RequestParam(name = "dia", required = false) String dia,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
            @RequestParam(name = "horaInicio", required = false) String horaInicio,  // Parámetro para la hora de inicio
            @RequestParam(name = "horaFin", required = false) String horaFin,  // Parámetro para la hora de fin
            HttpSession session,
            Model model) {

        Usuario usuario = null;
        if (session.getAttribute("usuario") == null) {
            Optional<Usuario> usuario2 = service.findByCedula(0);
            model.addAttribute("usuario", usuario2.get());
            session.setAttribute("usuario", usuario2.get());
            System.out.println("Iniciando session Como anomino");
        } else {
            usuario = (Usuario) session.getAttribute("usuario");
            model.addAttribute("usuario", usuario);
        }

        if (usuario.getPerfil().equals("PACIENTE") || usuario.getPerfil().equals("ANONIMO")) {
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

            return "presentation/ConfirmarCita/view";
        }
        return "redirect:/error";
    }


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

        LocalDate fechaInicio = LocalDate.parse(fecha);
        Cita verificar = service.findCitaByMedicoHorario(medico.getId(),horaInicio,horaFin,dia, fechaInicio);

        if(verificar !=null && verificar.getEstado().equals("pendiente") || verificar !=null && verificar.getEstado().equals("aprobado")){
            return "presentation/ConfirmarCita/rechazada";
        }
        else {
            // Crear una nueva cita con los valores proporcionados
            Cita cita = new Cita();
            cita.setMedico(medico);
            cita.setUsuario(usuario);
            cita.setDia(dia);
            cita.setHorainicio(horaInicio);
            cita.setHorafinal(horaFin);
            cita.setEstado("pendiente");  // Estado predeterminado
            cita.setNota("NA");  // Nota predeterminada

            cita.setInicio(Integer.parseInt(horaInicio.split(":")[0]));  // Extracting hour from "horaInicio"
            cita.setFin(Integer.parseInt(horaFin.split(":")[0]));  // Extracting hour from "horaFin"
            cita.setFechaHora(LocalDate.parse(fecha));
            // Guardar la cita en la base de datos
            citaRepository.save(cita);

            // Agregar al modelo la cita guardada
            model.addAttribute("cita", cita);


            return "presentation/ConfirmarCita/confirmada";
        }
    }
}
