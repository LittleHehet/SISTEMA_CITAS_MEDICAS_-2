package com.example.sistema_citas.security;

import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider{

    @Autowired
    private Service service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String cedulaRaw = authentication.getName(); // cedula como String
        String clave = authentication.getCredentials().toString();

        Integer cedula;
        try {
            cedula = Integer.parseInt(cedulaRaw);
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("Cédula inválida");
        }

        // Buscar el usuario por cédula
        Usuario usuario = service.findByCedula(cedula)
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        // Validar contraseña
        if (!passwordEncoder.matches(clave, usuario.getClave())) {
            throw new BadCredentialsException("Clave incorrecta");
        }

        // Validar si el usuario es médico y su estado
        if ("MEDICO".equals(usuario.getPerfil())) {
            Optional<Medico> optionalMedico = service.findMedicoByUsuario(usuario);
            if (optionalMedico.isPresent()) {
                Medico medico = optionalMedico.get();
                System.out.println("Estado del médico: " + medico.getEstado());
                if (!"aprobado".equalsIgnoreCase(medico.getEstado())) {
                    System.out.println("¡Es un médico pendiente!");
                    throw new BadCredentialsException("MEDICO_PENDIENTE");
                }
            } else {
                System.out.println("No se encontró el médico relacionado");
            }
        }


        return new UsernamePasswordAuthenticationToken(
                usuario.getCedula().toString(),
                usuario.getClave(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil()))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
