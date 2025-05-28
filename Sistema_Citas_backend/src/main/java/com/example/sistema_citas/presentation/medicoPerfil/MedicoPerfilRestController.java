package com.example.sistema_citas.presentation.medicoPerfil;

import com.example.sistema_citas.data.FotoRepository;
import com.example.sistema_citas.logic.Foto;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.service.Service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/medico")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MedicoPerfilRestController {

    @Autowired
    private Service service;

    @Autowired
    private FotoRepository fotoRepository;

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfilDesdeToken(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Integer cedula = Integer.parseInt(authentication.getName());

        Optional<Medico> medicoOpt = service.findMedicobyCedula(cedula);
        if (medicoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Médico no encontrado");
        }

        Medico medico = medicoOpt.get();

        // IMPORTANTE: asegurarse de que medico.getUsuarios() NO sea null
        if (medico.getUsuarios() == null) {
            return ResponseEntity.status(403).body("El perfil aún no ha sido aprobado por el administrador.");
        }

        return ResponseEntity.ok(Map.of(
                "medico", medico,
                "especialidades", service.getAllEspecialidades(),
                "localidades", service.getAllLocalidades()
        ));
    }


    @GetMapping("/foto")
    public ResponseEntity<byte[]> obtenerFoto(@RequestParam("id") Integer idMedico) {
        Optional<Medico> medicoOpt = service.findMedicoById(idMedico);
        if (medicoOpt.isPresent()) {
            Foto foto = medicoOpt.get().getFoto();
            if (foto != null && foto.getImagen() != null) {
                byte[] imagen = foto.getImagen();
                String mimeType = "image/jpeg";
                if (imagen[0] == (byte) 0x89 && imagen[1] == (byte) 0x50) {
                    mimeType = "image/png";
                }
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).body(imagen);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/actualizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarPerfil(
            @RequestParam("id") Integer id,
            @RequestParam("costo") Integer costo,
            @RequestParam("frecuenciaCitas") Integer frecuenciaCitas,
            @RequestParam("nota") String nota,
            @RequestParam("especialidadId") Integer especialidadId,
            @RequestParam("localidadId") Integer localidadId,
            @RequestParam("horario") String horario,
            @RequestParam(value = "archivoFoto", required = false) MultipartFile archivoFoto,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Integer cedula = Integer.parseInt(authentication.getName());

        Optional<Medico> medicoOpt = service.findMedicobyCedula(cedula);
        if (medicoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Médico no encontrado");
        }

        Medico medico = medicoOpt.get();

        // Validar formato de horario
        // Validar que haya 7 días separados por punto y coma
        System.out.println("📌 DEBUG - Horario recibido: [" + horario + "]");
        String[] dias = horario.split(";");

// Si vienen menos de 7, se completa con strings vacíos
        if (dias.length < 7) {
            String[] diasCompletos = new String[7];
            System.arraycopy(dias, 0, diasCompletos, 0, dias.length);
            for (int i = dias.length; i < 7; i++) {
                diasCompletos[i] = ""; // rellena los días faltantes
            }
            dias = diasCompletos;
        } else if (dias.length > 7) {
            return ResponseEntity.badRequest().body("El horario tiene más de 7 días. Verifique el formato.");
        }

        // Validar el formato de cada día individualmente
        for (String dia : dias) {
            // Acepta: "8-12,13-17", "8-12,", ",13-17", ","
             //¿Qué permite esta nueva expresión? "8-12" "8-12,13-17" "8-12," ",13-17" "," "" (vacío)
            if (!dia.matches("^([0-9]{1,2}-[0-9]{1,2})?(,([0-9]{1,2}-[0-9]{1,2})?)?$")) {
                return ResponseEntity.badRequest().body("Formato de horario incorrecto en uno de los días: " + dia);
            }
        }


        // Procesar foto si se envío
        Foto fotoFinal = medico.getFoto();
        if (archivoFoto != null && !archivoFoto.isEmpty()) {
            try {
                Foto nuevaFoto = new Foto();
                nuevaFoto.setImagen(archivoFoto.getBytes());
                fotoFinal = service.create(nuevaFoto); // ✅ crea y guarda en base de datos
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body("Error al procesar la imagen");
            }
        }

        // Actualizar campos permitidos
        medico.setCosto(BigDecimal.valueOf(costo));
        medico.setFrecuenciaCitas(frecuenciaCitas);
        medico.setNota(nota);
        medico.setFoto(fotoFinal);
        medico.setHorario(horario);

        // NUEVO: actualizar especialidad y localidad
        service.getAllEspecialidades().stream()
                .filter(e -> e.getId().equals(especialidadId))
                .findFirst()
                .ifPresent(medico::setEspecialidad);

        service.getAllLocalidades().stream()
                .filter(l -> l.getId().equals(localidadId))
                .findFirst()
                .ifPresent(medico::setLocalidad);

        service.updateMedico(medico, fotoFinal);

        return ResponseEntity.ok("Perfil actualizado correctamente");
    }


    private boolean perfilIncompleto(Medico medico) {
        return medico.getEspecialidad() == null || medico.getCosto() == null ||
                medico.getLocalidad() == null || medico.getHorario() == null ||
                medico.getHorario().isBlank() || medico.getFrecuenciaCitas() == null ||
                medico.getFoto() == null || medico.getFoto().getImagen() == null ||
                medico.getNota() == null || medico.getNota().isBlank();
    }

}
