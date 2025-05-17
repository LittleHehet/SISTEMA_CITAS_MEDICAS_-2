package com.example.sistema_citas.security;

import jakarta.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        if ("MEDICO_PENDIENTE".equalsIgnoreCase(exception.getMessage())) {
            response.sendRedirect("/Sign-in?error=pending");
        } else {
            response.sendRedirect("/Sign-in?error=true");
        }
        System.out.println("Excepción capturada: " + exception.getMessage());
    }
}