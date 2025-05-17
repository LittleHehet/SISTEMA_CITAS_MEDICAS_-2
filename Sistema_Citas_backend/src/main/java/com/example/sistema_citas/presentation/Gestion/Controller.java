package com.example.sistema_citas.presentation.Gestion;


import com.example.sistema_citas.logic.Cita;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller("GestionCitas")
@SessionAttributes("usuario")
public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/GestionCitas")
    public String show(@RequestParam(value = "usuarioId", required = false) Integer usuarioId,
                       @RequestParam(value = "estado", required = false) String estado,
                       Model model, HttpSession session , HttpServletRequest request) {

        service.cancelarCitasPasadas();
        // Store the complete URL with query string in the session
        String urlCompleta = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {
            urlCompleta += "?" + queryString; // Add query parameters to the URL
        }
        session.setAttribute("prevUrl2", urlCompleta); // Store it in the session
        System.out.println("Redirigiendo a: " + urlCompleta);

        Medico medico = (Medico) session.getAttribute("medico");
        System.out.println("Usuario ID recibido: " + usuarioId);
        System.out.println("Estado recibido: " + estado);
        if (medico == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String cedulaStr = auth.getName(); // La cédula viene como String
            try {
                Integer cedula = Integer.parseInt(cedulaStr);
                Optional<Medico> medicoOptional = service.findMedicobyCedula(cedula);
                if (medicoOptional.isPresent()) {
                    medico = medicoOptional.get();
                    session.setAttribute("medico", medico);
                } else {
                    System.out.println("⚠️ No se encontró médico con la cédula: " + cedula);
                    return "redirect:/Sign-in?error=missingMedico";
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error al convertir cédula: " + cedulaStr);
                return "redirect:/Sign-in?error=invalidCedula";
            }
        }
        List<Cita> citas = service.findAllCitasbyMedico(medico.getId());
        List<Usuario> usuarios = citas.stream()
                .map(Cita::getUsuario)
                .distinct()
                .collect(Collectors.toList());

        // Aplicar filtros si se proporcionan
        System.out.println("Citas antes del filtrado: " + citas.size());

        if (usuarioId != null && usuarioId > 0) {
            citas = citas.stream()
                    .filter(c -> {
                        System.out.println("Comparando con usuarioId: " + c.getUsuario().getId());
                        return c.getUsuario().getId().equals(usuarioId);
                    })
                    .collect(Collectors.toList());
        }

        if (estado != null && !estado.equals("all")) {
            citas = citas.stream()
                    .filter(c -> {
                        System.out.println("Comparando con estado: " + c.getEstado());
                        return c.getEstado().equalsIgnoreCase(estado);
                    })
                    .collect(Collectors.toList());
        }

        System.out.println("Citas después del filtrado: " + citas.size());

        model.addAttribute("citas", citas);
        model.addAttribute("usuarios", usuarios);
        return "presentation/Gestion/view";
    }


    @PostMapping("/completarCita")
    public String completarCita(@RequestParam Integer id) {
        service.cambiarEstadoCita(id, "completada");
        return "redirect:/GestionCitas";
    }

    @PostMapping("/cancelarCita")
    public String cancelarCita(@RequestParam Integer id) {
        service.cambiarEstadoCita(id, "cancelada");
        return "redirect:/GestionCitas";
    }

    @GetMapping("/verDetalleCita")
    public String verDetalleCita(@RequestParam Integer id, Model model , HttpSession session, HttpServletRequest request)  {
        Optional<Cita> citaOptional = service.findCitaById(id);

        if (citaOptional.isPresent()) {
            model.addAttribute("cita", citaOptional.get());

            // Recuperar la última URL almacenada
            String prevUrl = (String) session.getAttribute("prevUrl2");
            model.addAttribute("prevUrl2", prevUrl);

            return "presentation/Citas/view";
        }else {
            // Si la cita no existe, puedes redirigir o mostrar un mensaje de error
            model.addAttribute("error", "Cita no encontrada");
            return "presentation/About/view"; // Vista de error (puedes cambiar esto si tienes una página de error específica)
        }
    }

    @GetMapping("/editarNota")
    public String editarNota(@RequestParam Integer id, Model model) {
        Optional<Cita> citaOptional = service.findCitaById(id);
        if (citaOptional.isPresent()) {
            model.addAttribute("cita", citaOptional.get());
            return "presentation/Gestion/editarNota";
        } else {
            return "redirect:/GestionCitas"; // o a una página de error
        }
    }

    @PostMapping("/guardarNota")
    public String guardarNota(@RequestParam Integer id,
                              @RequestParam String nota) {
        Optional<Cita> citaOptional = service.findCitaById(id);
        if (citaOptional.isPresent()) {
            Cita cita = citaOptional.get();
            cita.setNota(nota);
            service.saveCita(cita);
        }
        return "redirect:/GestionCitas";
    }


}
