package com.example.sistema_citas.presentation.signIn;

import com.example.sistema_citas.logic.Medico;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.example.sistema_citas.service.Service;
import com.example.sistema_citas.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@org.springframework.stereotype.Controller
@SessionAttributes("usuario")
public class Controller {
    @Autowired
    private Service service;

    @GetMapping({"/", "/Sign-in"})
    public String show( HttpSession  session,
                        Model model) {
        Optional<Usuario> usuario = service.findByCedula(0);
        System.out.println(usuario.get().getPerfil());
        model.addAttribute("usuario", usuario.get());
        session.setAttribute("usuario", usuario.get());
        return "presentation/signIn/view";
    }


//    @PostMapping("/Sign-in/Sign-in")
//    public String approve(@RequestParam("cedula") Integer cedula,
//                          @RequestParam("clave") String clave,
//                          HttpSession session,
//                          RedirectAttributes redirectAttributes,
//                          Model model) {
//
//        String prevUrl = (String) session.getAttribute("prevUrl");
//        System.out.println("URL recuperada de la sesión: " + prevUrl);
//
//        Optional<Usuario> usuario = service.findByIdAndClave(cedula, clave);
//
//        if (usuario.isPresent()) {
//            Usuario user = usuario.get();
//            model.addAttribute("usuario", user);
//            session.setAttribute("usuario", user);
//            System.out.println("Perfil ID: " + user.getPerfil());
//
//            switch (user.getPerfil()) {
//                case "MEDICO":  // Médico
//                    return Medico(cedula, redirectAttributes , session);
//                case "PACIENTE":  // Paciente
//                    return Paciente(prevUrl);
//                case "ADMINISTRADOR":  // Administrador
//                    return Administrador();
//            }
//        }
//
//// Si no se encontró usuario con clave
//        if (service.findByCedula(cedula).isPresent()) {
//            Usuario user = service.findByCedula(cedula).get();
//
//            // Validar si es médico pendiente
//            if ("MEDICO".equals(user.getPerfil())) {
//                Optional<Medico> optMedico = service.findMedicobyCedula(cedula);
//                if (optMedico.isPresent()) {
//                    Medico medico = optMedico.get();
//                    if (!"aprobado".equalsIgnoreCase(medico.getEstado())) {
//                        return "redirect:/Sign-in?error=pending";
//                    }
//                }
//            }
//
//            return "redirect:/Sign-in?error=true";
//        }
//
//        System.out.println("No se encontró el usuario");
//        return "/presentation/signUp/view";
//    }
//
    private String Medico(Integer cedula, RedirectAttributes redirectAttributes , HttpSession session) {
        Optional<Medico> medico = service.findMedicobyCedula(cedula);
        if (medico.isPresent()) {
            Medico med = medico.get();
            session.setAttribute("medicoId", med);
            session.setAttribute("medico", med);
            if ("aprobado".equalsIgnoreCase(med.getEstado())) {
                if (med.getEspecialidad() == null || med.getCosto() == null || med.getLocalidad() == null ||
                        med.getHorario() == null || med.getHorario().isBlank() ||
                        med.getFrecuenciaCitas() == null || med.getFoto() == null ||
                        med.getFoto().getImagen() == null || med.getFoto().getImagen().length == 0 ||
                        med.getNota() == null || med.getNota().isBlank())
                {

                    redirectAttributes.addFlashAttribute("error",
                            "Debe completar todos los campos obligatorios antes de continuar.");
                    return "redirect:/Medico-Perfil?id=";
                }
                else{
                    return "redirect:/GestionCitas";
                }

            } else {
                redirectAttributes.addFlashAttribute("confirmacion",
                        "Su cuenta está en proceso de aprobación.");
                return "redirect:/Sign-in";
            }
        }
        return "redirect:/Sign-in";
    }


    private String Paciente(String prevUrl) {
        if (prevUrl != null && !prevUrl.isEmpty()) {
            System.out.println("Redirigiendo a: " + prevUrl);
            return "redirect:" + prevUrl;
        } else {
            return "redirect:/BuscarCita";
        }
    }


    private String Administrador() {
        System.out.println("Redirigiendo a: Approve");
        return "redirect:/Approve";
    }



    @GetMapping("/Salir")
    public String salir(HttpSession session,
                        Model model) {
        Optional<Usuario> usuario = service.findByCedula(0);
        model.addAttribute("usuario", usuario.get());
        session.setAttribute("usuario", null);
        session.invalidate();
        return "presentation/signIn/view";
    }




}
