package com.example.sistema_citas.security;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomFailureHandler customFailureHandler;

    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider,
                          CustomSuccessHandler customSuccessHandler,
                          CustomFailureHandler customFailureHandler , JwtAuthFilter jwtAuthFilter) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customSuccessHandler = customSuccessHandler;
        this.customFailureHandler = customFailureHandler;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:5173")); // frontend React
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))

                .csrf(csrf -> csrf.disable())
                .authenticationProvider(customAuthenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/","/api/**" , "/api/about" ,  "/api/login/login",
                                "/medico-foto/**", "/css/**", "/images/**",
                                "/BuscarCita", "/ConfirmarCita/**", "/HorarioExtendido/**"
                                // Permitir APIs REST
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
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthenticationProvider)
                .build();
    }
}



//                .formLogin(form -> form
//                        .loginPage("/Sign-in")
//                        .loginProcessingUrl("/Sign-in/Sign-in")
//                        .successHandler(customSuccessHandler)
//                        .failureHandler(customFailureHandler)
//                        .permitAll()
//                )

//                .logout(logout -> logout
//                        .logoutUrl("/Salir")
//                        .logoutSuccessUrl("/Sign-in?logout=true")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                )

////                .csrf(csrf -> csrf.disable())
////                .sessionManagement(session -> session
////                        .invalidSessionUrl("/Sign-in")