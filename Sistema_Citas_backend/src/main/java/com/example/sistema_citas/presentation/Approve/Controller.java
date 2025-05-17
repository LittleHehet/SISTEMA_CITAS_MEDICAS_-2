package com.example.sistema_citas.presentation.Approve;


import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.service.Service;
import com.example.sistema_citas.logic.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Controller("Approve")
@SessionAttributes("usuario")
public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/Approve")
    public String show(Model model , HttpSession session) {
        // Recupera todos los usuarios con perfil "Médico"
        List<Usuario> usuariosaux = service.findByPerfil("MEDICO");

        // Crear una lista para almacenar los usuarios con estado pendiente
        List<Usuario> usuarios = new ArrayList<>();

        for (Usuario usuario : usuariosaux) {
            System.out.println("Usuario con cédula: " + usuario.getCedula());

            // Busca el médico por la cédula del usuario
            Optional<Medico> medicoOpt = service.findMedicobyCedula(usuario.getCedula());

            // Verifica si se encontró el médico
            if (medicoOpt.isPresent()) {
                Medico medico = medicoOpt.get(); // Obtén el médico si está presente
                System.out.println("Estado de médico: '" + medico.getEstado() + "'");

                // Verifica si el estado es "pendiente" (ignorando mayúsculas/minúsculas)
                if ("pendiente".equalsIgnoreCase(medico.getEstado())) {
                    System.out.println("Usuario agregado: " + usuario.getCedula());
                    usuarios.add(usuario); // Agrega el usuario a la lista
                }
            } else {
                // Si no se encontró el médico, imprime un mensaje
                System.out.println("No se encontró médico para la cédula: " + usuario.getCedula());
            }
        }

        // Agrega los usuarios recuperados al modelo para pasarlos a la vista
        model.addAttribute("usuarios", usuarios);

        // Retorna la vista para mostrar los usuarios pendientes
        return "presentation/Approve/view";
    }



    @PostMapping("/Approve/Approve")
    public String approveDoctors(@RequestParam Map<String, String> estadoMap , RedirectAttributes redirectAttributes) {
        estadoMap.forEach((key, value) -> {
            String[] estadoCedula = value.split("-"); // Dividir el valor en estado y cédula

            // Validar que la cédula tiene el formato correcto
            if (estadoCedula.length == 2) {
                String estado = estadoCedula[0]; // Estado (pendiente o aprobado)
                String cedula = estadoCedula[1]; // Cédula del usuario

                if (cedula.matches("\\d+") && "aprobado".equals(estado)) { // Solo si el estado es 'aprobado'
                    // Buscar el Médico relacionado con la cédula
                    Optional<Medico> medico = service.findMedicobyCedula(Integer.parseInt(cedula));

                    if (medico.isPresent()) {
                        // Cambiar el estado del médico a 'aprobado'
                        medico.get().setEstado("aprobado");
                        service.saveMedicoByCedula(Integer.parseInt(cedula));
                    }
                }
            }
        });
        redirectAttributes.addFlashAttribute("confirmacion", "Médico aprobado.");
        // Redirigir a la página de aprobación después de procesar
        return "redirect:/Approve";
    }



}