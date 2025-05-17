package com.example.sistema_citas.presentation.signUp;

import com.example.sistema_citas.security.PasswordEncoderConfig;
import com.example.sistema_citas.service.Service;

import com.example.sistema_citas.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Optional;

@org.springframework.stereotype.Controller("Usuarios")
@SessionAttributes({"Usuarios ,UsuariosList"})

public class Controller {
    @Autowired
    private Service service;
//    @Autowired
//    private PasswordEncoderConfig passwordEncoder;

    @GetMapping("/Sign-up")
    public String show( Model model) {
        Optional<Usuario> usuario = service.findByCedula(0);
        model.addAttribute("usuario", usuario.get());
        return "/presentation/signUp/view";
    }

    @PostMapping("/Sign-up/Sign-up")
    public String registrar(@RequestParam("cedula") Integer cedula,
                            @RequestParam("nombre") String nombre,
                            @RequestParam("apellido") String apellido,
                            @RequestParam("clave") String clave,
                            @RequestParam("perfil") String perfil,
                            Model model) {
        Optional<Usuario> usuarioExistente = service.findByCedula(cedula);

        if (usuarioExistente.isPresent()) {
            model.addAttribute("error", "Usuario ya creado");
            return "/presentation/signUp/view"; // Devuelve la misma vista con los errores
        }
        // Crear el usuario con los datos ingresados
        Usuario usuario = new Usuario(cedula, nombre, apellido, clave, perfil);

        try {
            // Guardar el usuario
            service.saveUsuario(usuario);

            if ("MEDICO".equals(perfil)) {
                // Si el perfil es Médico, crear un Medico y asociarlo al Usuario
                service.saveMedicoByCedula(cedula);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error");
        }

        return "redirect:/Sign-in";
    }


}
