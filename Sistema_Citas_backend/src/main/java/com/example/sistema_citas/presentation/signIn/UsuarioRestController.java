package com.example.sistema_citas.presentation.signIn;

import org.springframework.security.core.Authentication;
import com.example.sistema_citas.logic.LoginRequest;
import com.example.sistema_citas.logic.LoginResponse;
import com.example.sistema_citas.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.sistema_citas.service.Service;
import com.example.sistema_citas.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioRestController {
    @Autowired
    private Service service;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("Intento login: cedula=" + loginRequest.getCedula() + ", clave=" + loginRequest.getClave());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            String.valueOf(loginRequest.getCedula()),
                            loginRequest.getClave()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        UserDetails userDetails = service.load(String.valueOf(loginRequest.getCedula()));
        String token = jwtUtil.generateToken(userDetails);

        Optional<Usuario> usuarioOpt = service.findByCedula(loginRequest.getCedula());
        String perfil = usuarioOpt.map(Usuario::getPerfil).orElse("desconocido");

        return ResponseEntity.ok(new LoginResponse(token, perfil));
    }



    // Cierre de sesión
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

}
