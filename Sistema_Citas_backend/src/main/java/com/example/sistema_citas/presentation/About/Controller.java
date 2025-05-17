package com.example.sistema_citas.presentation.About;

import com.example.sistema_citas.service.Service;
import com.example.sistema_citas.logic.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Optional;


@org.springframework.stereotype.Controller("About")
@SessionAttributes("usuario")

public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/About")
    public String show(Model model, HttpSession session) {
        if(session.getAttribute("usuario") == null) {
            Optional<Usuario> usuario = service.findByCedula(0);

            model.addAttribute("usuario", usuario.get());
            session.setAttribute("usuario", usuario.get());
        }
        else{
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            model.addAttribute("usuario", usuario);
        }

        return "presentation/About/view";
    }
}
