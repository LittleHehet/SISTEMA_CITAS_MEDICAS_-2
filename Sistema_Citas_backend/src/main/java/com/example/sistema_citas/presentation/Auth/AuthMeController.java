package com.example.sistema_citas.presentation.Auth;

import com.example.sistema_citas.data.UsuarioRepository;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class AuthMeController {

    private final UsuarioRepository usuarioRepository;
    private final Service service;

    public AuthMeController(UsuarioRepository usuarioRepository, Service service) {
        this.usuarioRepository = usuarioRepository;
        this.service = service;
    }

    @GetMapping("/api/auth/me")
    public Map<String, Object> me(Authentication authentication) {

        // Caso Google (Resource Server): principal es Jwt
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");

            Usuario u = usuarioRepository.findByGoogleSub(sub)
                    .or(() -> usuarioRepository.findByEmail(email))
                    .orElseThrow();

            return buildProfileResponse(u);
        }

        String name = authentication.getName();
        Integer cedula = Integer.parseInt(name);

        Usuario u = service.findByCedula(cedula).orElseThrow();
        return buildProfileResponse(u);
    }



    private String normalizeRole(String perfil) {
        if (perfil == null || perfil.isBlank()) return "ROLE_PACIENTE";
        return perfil.startsWith("ROLE_") ? perfil : "ROLE_" + perfil;
    }

    private Map<String, Object> buildProfileResponse(Usuario u) {
        String perfilFront = normalizeRole(u.getPerfil());

        boolean perfilCompleto = true;
        String medicoEstado = null;
        Integer medicoId = null;

        if ("ROLE_MEDICO".equalsIgnoreCase(perfilFront)) {
            if (u.getCedula() == null) {          // <-- importante si medico via Google
                perfilCompleto = false;
            } else {
                Optional<Medico> medicoOpt = service.findMedicobyCedula(u.getCedula());
                if (medicoOpt.isPresent()) {
                    Medico medico = medicoOpt.get();
                    medicoEstado = medico.getEstado();
                    medicoId = medico.getId();
                    perfilCompleto = !perfilIncompleto(medico);
                } else {
                    perfilCompleto = false;
                }
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("perfil", perfilFront);
        res.put("perfilCompleto", perfilCompleto);
        res.put("medicoEstado", medicoEstado);
        res.put("medicoId", medicoId);
        return res;
    }


    private boolean perfilIncompleto(Medico medico) {
        return medico.getEspecialidad() == null || medico.getCosto() == null ||
                medico.getLocalidad() == null || medico.getHorario() == null ||
                medico.getHorario().isBlank() || medico.getFrecuenciaCitas() == null ||
                medico.getFoto() == null || medico.getFoto().getImagen() == null ||
                medico.getNota() == null || medico.getNota().isBlank();
    }
}
