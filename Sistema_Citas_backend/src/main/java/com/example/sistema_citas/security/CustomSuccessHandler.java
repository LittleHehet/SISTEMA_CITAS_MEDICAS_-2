package com.example.sistema_citas.security;

import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private Service service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String cedulaRaw = authentication.getName();
        Integer cedula = Integer.parseInt(cedulaRaw);

        Optional<Usuario> userOpt = service.findByCedula(cedula);
        if (userOpt.isEmpty()) {
            response.sendRedirect("/Sign-in?error=true");
            return;
        }

        Usuario user = userOpt.get();
        HttpSession session = request.getSession();
        session.setAttribute("usuario", user);

        switch (user.getPerfil()) {
            case "PACIENTE" -> response.sendRedirect("/BuscarCita");

            case "MEDICO" -> {
                Optional<Medico> medicoOpt = service.findMedicobyCedula(cedula);
                if (medicoOpt.isPresent()) {
                    Medico medico = medicoOpt.get();
                    session.setAttribute("medico", medico);
                    session.setAttribute("medicoId", medico);
                    if (!"aprobado".equalsIgnoreCase(medico.getEstado())) {
                        response.sendRedirect("/Sign-in?error=pending");
                    } else if (perfilIncompleto(medico)) {
                        response.sendRedirect("/Medico-Perfil?id=" + medico.getId());
                    } else {
                        response.sendRedirect("/GestionCitas");
                    }
                } else {
                    response.sendRedirect("/Sign-in?error=true");
                }
            }

            case "ADMINISTRADOR" -> response.sendRedirect("/Approve");

            default -> response.sendRedirect("/Sign-in?error=true");
        }
    }

    private boolean perfilIncompleto(Medico medico) {
        return medico.getEspecialidad() == null || medico.getCosto() == null ||
                medico.getLocalidad() == null || medico.getHorario() == null ||
                medico.getHorario().isBlank() || medico.getFrecuenciaCitas() == null ||
                medico.getFoto() == null || medico.getFoto().getImagen() == null ||
                medico.getNota() == null || medico.getNota().isBlank();
    }
}
