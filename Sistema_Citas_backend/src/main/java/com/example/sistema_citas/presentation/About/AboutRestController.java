package com.example.sistema_citas.presentation.About;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/about")
@CrossOrigin(origins = "http://localhost:5173")
public class AboutRestController {

    @GetMapping
    public Map<String, Object> getAboutInfo() {
        return Map.of(
                "titulo", "Acerca de Nosotros",
                "introduccion", "Bienvenido al Sistema de Citas Médicas",
                "equipo", List.of(
                        Map.of("nombre", "Kendra Artavia Caballero", "id", "402580003"),
                        Map.of("nombre", "William Rodríguez Campos", "id", "118070563"),
                        Map.of("nombre", "Alexia Alvarado Alfaro", "id", "402580319")
                ),
                "descripcion", Map.of(
                        "pacientes", List.of(
                                "Buscar médicos especialistas por especialidad y ubicación.",
                                "Ver los horarios disponibles para los próximos tres días.",
                                "Reservar citas en línea después de iniciar sesión o registrarse."
                        ),
                        "medicos", List.of(
                                "Registrarse y configurar su disponibilidad semanal.",
                                "Confirmar y gestionar citas.",
                                "Agregar notas y actualizar el estado de las consultas."
                        ),
                        "administradores", List.of(
                                "Aprobar o rechazar registros de médicos.",
                                "Gestionar cuentas de usuarios y configuraciones del sistema."
                        )
                ),
                "tecnologias", List.of("Java (Spring MVC)", "Thymeleaf", "MySQL", "HTML/CSS"),
                "mision", "Nuestro objetivo es simplificar el proceso de agendamiento de citas médicas..."
        );
    }
}