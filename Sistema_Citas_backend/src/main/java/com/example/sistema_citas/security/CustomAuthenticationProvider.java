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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        String cedulaRaw = authentication.getName();
        String clave = authentication.getCredentials().toString();

        Integer cedula;
        try {
            cedula = Integer.parseInt(cedulaRaw);
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("Cédula inválida");
        }

        Usuario usuario = service.findByCedula(cedula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(clave, usuario.getClave())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        return new UsernamePasswordAuthenticationToken(
                cedulaRaw,
                clave,
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getPerfil()))
        );
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
