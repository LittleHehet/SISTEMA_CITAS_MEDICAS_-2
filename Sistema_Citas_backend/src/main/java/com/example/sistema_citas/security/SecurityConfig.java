package com.example.sistema_citas.security;

import com.example.sistema_citas.data.UsuarioRepository;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
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
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider,
                          CustomSuccessHandler customSuccessHandler,
                          CustomFailureHandler customFailureHandler,
                          JwtAuthFilter jwtAuthFilter,
                          UsuarioRepository usuarioRepository) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customSuccessHandler = customSuccessHandler;
        this.customFailureHandler = customFailureHandler;
        this.jwtAuthFilter = jwtAuthFilter;
        this.usuarioRepository = usuarioRepository;
    }


    @Bean
    @Order(1)
    public SecurityFilterChain googleChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/auth/**")
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:5173", "https://sistema-citas-frontend.onrender.com"));
                    corsConfig.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","Origin","X-Requested-With"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/me").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(new GoogleJwtAuthConverter(usuarioRepository))
                ))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain appChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:5173", "https://sistema-citas-frontend.onrender.com"));
                    corsConfig.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","Origin","X-Requested-With"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(customAuthenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/api/signup", "/api/login/login", "/api/about",
                                "/api/medico-foto/**", "/api/medico/foto","/api/BuscarCita",
                                "/api/BuscarCita/busqueda","/api/HorarioExtendido/**","/error",
                                "/css/**", "/images/**"
                        ).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/api/Approve/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/historicoPaciente/**", "/api/ConfirmarCita/**").hasRole("PACIENTE")
                        .requestMatchers("/api/gestion/**", "/api/medico/perfil", "/api/medico/actualizar").hasRole("MEDICO")

                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(customAuthenticationProvider));
    }

}
