package com.example.sistema_citas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomFailureHandler customFailureHandler;

    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider,
                          CustomSuccessHandler customSuccessHandler,
                          CustomFailureHandler customFailureHandler) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customSuccessHandler = customSuccessHandler;
        this.customFailureHandler = customFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(customAuthenticationProvider) // ⬅️ Aquí usamos el tuyo

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/",
                                "/Sign-up/**", "/About/**", "/Sign-in/**", "/Salir/**","/medico-foto/**",
                                "/css/**", "/images/**","/BuscarCita","/ConfirmarCita/**","/HorarioExtendido/**"
                        ).permitAll()
                        .requestMatchers("/Approve/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/Medico-Perfil/**",
                                "/GestionCitas/**", "/completarCita/**",
                                "/cancelarCita/**", "/guardarNota/**",
                                "/editarNota/**", "/actualizar/**"
                        ).hasRole("MEDICO")
                        .requestMatchers("/historicoPaciente/**",
                                "/BuscarCita", "/ConfirmarCita/**",
                                "/HorarioExtendido/**"
                        ).hasRole("PACIENTE")
                        .requestMatchers("/verDetalleCita/**"
                        ).hasAnyRole("PACIENTE", "MEDICO")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/Sign-in")  // Vista personalizada
                        .loginProcessingUrl("/Sign-in/Sign-in")  // Acción del form
                        .successHandler(customSuccessHandler)
                        .failureHandler(customFailureHandler)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/Salir")
                        .logoutSuccessUrl("/Sign-in?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .csrf(csrf -> csrf.disable())  // Opcional, si no usas CSRF tokens
                .sessionManagement(session -> session
                        .invalidSessionUrl("/Sign-in")  // Redirección si la sesión expira
                );

        return http.build();
    }

    //    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthenticationProvider)
                .build();
    }
}

// Rutas específicas para cada perfil
//                        .requestMatchers("/Medico-Perfil/**","/medico-foto/**" ,
//                                "/actualizar/**", "/medico-foto/**").hasRole("MEDICO")
//                        .requestMatchers("/ConfirmarCita/**", "/BuscarCita").hasRole("PACIENTE")
//                        .requestMatchers("/Approve/**" ).hasRole("ADMINISTRADOR")
//                        .anyRequest().authenticated()