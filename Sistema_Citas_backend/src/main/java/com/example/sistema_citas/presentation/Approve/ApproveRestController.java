package com.example.sistema_citas.presentation.Approve;


import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.UsuarioConEstadoDTO;
import com.example.sistema_citas.service.Service;
import com.example.sistema_citas.logic.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@RestController
@RequestMapping("/api/Approve")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ApproveRestController {
    @Autowired
    private Service service;


    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfilDesdeToken(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        boolean esAdministrador = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!esAdministrador) {
            return ResponseEntity.status(403).body("Acceso denegado: no es administrador");
        }

        Integer cedula = Integer.parseInt(authentication.getName());
        Optional<Usuario> Opt= service.findByCedula(cedula);

        if (Opt.isEmpty()) {
            return ResponseEntity.status(404).body("Administrador no encontrado");
        }

        return ResponseEntity.ok(Opt.get());
    }



    @GetMapping
    public ResponseEntity<?> obtenerMedicosPendientes() {

        System.out.println(">>>>>>>>>> HOLA desde obtenerMedicosPendiente <<<<<<<<<<");
        System.out.println(">>>>>>>>>> HOLA desde obtenerMedicosPendiente <<<<<<<<<<");
        System.out.println(">>>>>>>>>> HOLA desde obtenerMedicosPendiente <<<<<<<<<<");
        List<Usuario> usuariosAux = service.findByPerfil("ROLE_MEDICO");
        System.out.println("Usuarios con ROLE_MEDICO: " + usuariosAux.size());
        List<Usuario> usuariosMostrar = new ArrayList<>();

       

        List<UsuarioConEstadoDTO> listaDTO = new ArrayList<>();

        for (Usuario usuario : usuariosAux) {
            Optional<Medico> medicoOpt = service.findMedicobyCedula(usuario.getCedula());
            if (medicoOpt.isPresent()) {
                String estado = medicoOpt.get().getEstado();
                listaDTO.add(new UsuarioConEstadoDTO(
                        usuario.getCedula(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        estado
                ));
            }
        }

        return ResponseEntity.ok(listaDTO);


       // System.out.println("Total usuarios mostrados: " + usuariosMostrar.size());

       // return ResponseEntity.ok(usuariosMostrar);
    }




    // ✅ Aprobar médicos desde el frontend (JSON)
    @PostMapping("/approve")
    public ResponseEntity<?> aprobarMedicoIndividual(@RequestBody Map<String, String> data) {
        String cedulaStr = data.get("cedula");
        String estado = data.get("estado");

        if (cedulaStr == null || estado == null) {
            return ResponseEntity.badRequest().body("Faltan datos");
        }

        if (!cedulaStr.matches("\\d+")) {
            return ResponseEntity.badRequest().body("Cédula inválida");
        }

        int cedula = Integer.parseInt(cedulaStr);
        Optional<Medico> medicoOpt = service.findMedicobyCedula(cedula);

        if (medicoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Médico no encontrado");
        }

        Medico medico = medicoOpt.get();
        medico.setEstado(estado.toLowerCase());
        service.saveMedicoByCedula(cedula);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Estado actualizado correctamente");

        return ResponseEntity.ok(response);
    }


}