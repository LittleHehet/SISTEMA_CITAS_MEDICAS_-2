package com.example.sistema_citas.security;

import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.example.sistema_citas.service.Service;

import java.util.Collections;

@org.springframework.stereotype.Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private Service service;  // Tu clase de servicio actual

    @Override
    public UserDetails loadUserByUsername(String cedulaAsString) throws UsernameNotFoundException {
        Integer cedula;

        try {
            cedula = Integer.parseInt(cedulaAsString);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Cédula inválida");
        }

        Usuario usuario = service.findByCedula(cedula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con cédula: " + cedula));

        // Validación de médico pendiente
        if ("MEDICO".equals(usuario.getPerfil())) {
            Medico medico = service.findMedicobyCedula(cedula).orElse(null);
            if (medico != null && !"aprobado".equalsIgnoreCase(medico.getEstado())) {
                throw new BadCredentialsException("MEDICO_PENDIENTE");
            }
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getCedula().toString(), // Username
                usuario.getClave(),             // Password (encriptada)
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil()))
        );
    }
}