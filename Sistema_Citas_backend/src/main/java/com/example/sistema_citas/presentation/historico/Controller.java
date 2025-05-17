package com.example.sistema_citas.presentation.historico;


import com.example.sistema_citas.logic.Cita;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller("historicoPaciente")
@SessionAttributes("usuario")
public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/historicoPaciente")
    public String show(@RequestParam(value = "medicoId", required = false) Integer medicoId,
                       @RequestParam(value = "estado", required = false) String estado,
            Model model, HttpSession session , HttpServletRequest request) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        service.cancelarCitasPasadas();
        String urlCompleta = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {
            urlCompleta += "?" + queryString; // Add query parameters to the URL
        }
        session.setAttribute("prevUrl2", urlCompleta); // Store it in the session
        System.out.println("Redirigiendo a: " + urlCompleta);


        System.out.println("Medico ID recibido: " + medicoId);
        System.out.println("Estado recibido: " + estado);

        List<Cita> citas = service.findAllCitasbyUser(usuario.getId());
        List<Medico> medicos = citas.stream()
                .map(Cita::getMedico)
                .distinct()
                .collect(Collectors.toList());


        // Aplicar filtros si se proporcionan
        System.out.println("Citas antes del filtrado: " + citas.size());

        if (medicoId != null && medicoId > 0) {
            citas = citas.stream()
                    .filter(c -> {
                        System.out.println("Comparando con medicoId: " + c.getMedico().getId());
                        return c.getMedico().getId().equals(medicoId);
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
        model.addAttribute("medicos", medicos);
        return "presentation/historial/view";
    }





}
