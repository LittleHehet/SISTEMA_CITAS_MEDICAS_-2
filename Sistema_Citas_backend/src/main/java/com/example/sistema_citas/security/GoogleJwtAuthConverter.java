package com.example.sistema_citas.security;

import com.example.sistema_citas.data.UsuarioRepository;
import com.example.sistema_citas.logic.Usuario;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public class GoogleJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioRepository usuarioRepository;

    public GoogleJwtAuthConverter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    private String normalizeRole(String perfil) {
        if (perfil == null || perfil.isBlank()) return "ROLE_PACIENTE";
        return perfil.startsWith("ROLE_") ? perfil : "ROLE_" + perfil;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        final String sub = jwt.getClaimAsString("sub");
        final String email = jwt.getClaimAsString("email");

        String givenName = jwt.getClaimAsString("given_name");
        String familyName = jwt.getClaimAsString("family_name");

        final String safeGivenName = (givenName != null) ? givenName : "Usuario";
        final String safeFamilyName = (familyName != null) ? familyName : "Google";

        Usuario user = usuarioRepository.findByGoogleSub(sub)
                .or(() -> usuarioRepository.findByEmail(email))
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setNombre(safeGivenName);
                    u.setApellido(safeFamilyName);
                    u.setPerfil("ROLE_PACIENTE");
                    u.setAuthProvider("GOOGLE");
                    u.setGoogleSub(sub);
                    u.setEmail(email);
                    u.setCedula(null);
                    u.setClave(null);
                    return usuarioRepository.save(u);
                });

        String role = normalizeRole(user.getPerfil());

        var authorities = List.of(new SimpleGrantedAuthority(role));
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
