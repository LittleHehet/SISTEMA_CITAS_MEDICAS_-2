package com.example.sistema_citas.presentation.medicoPerfil;

import com.example.sistema_citas.data.FotoRepository;
import com.example.sistema_citas.logic.Foto;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.logic.Usuario;
import com.example.sistema_citas.service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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

    private Optional<Usuario> getUsuarioAutenticado(Authentication authentication) {
        if (authentication == null) return Optional.empty();

        Object principal = authentication.getPrincipal();

        // Google JWT
        if (principal instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");
            return service.findByGoogleSubOrEmail(sub, email);
        }

        // Login normal (cedula)
        try {
            Integer cedula = Integer.parseInt(authentication.getName());
            return service.findByCedula(cedula);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Optional<Medico> getMedicoAutenticado(Authentication authentication) {
        Optional<Usuario> optUsuario = getUsuarioAutenticado(authentication);
        if (optUsuario.isEmpty()) return Optional.empty();

        Usuario usuario = optUsuario.get();

        // Seguridad: solo MEDICO
        if (usuario.getPerfil() == null || !usuario.getPerfil().equalsIgnoreCase("ROLE_MEDICO")) {
            return Optional.empty();
        }

        // 1) Ideal para Google: Medico por relación Usuario
        Optional<Medico> medicoOpt = service.findMedicoByUsuario(usuario);
        if (medicoOpt.isPresent()) return medicoOpt;

        // 2) Fallback para login local: Medico por cédula
        if (usuario.getCedula() != null) {
            return service.findMedicobyCedula(usuario.getCedula());
        }

        return Optional.empty();
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfilDesdeToken(Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication)
                .orElse(null);

        if (usuario == null) return ResponseEntity.status(401).body("No autenticado");

        if (!"ROLE_MEDICO".equalsIgnoreCase(usuario.getPerfil()))
            return ResponseEntity.status(403).body("No autorizado");

        Optional<Medico> medicoOpt = service.findMedicoByUsuario(usuario);
        if (medicoOpt.isEmpty())
            return ResponseEntity.status(404).body("Médico no encontrado");

        Medico medico = medicoOpt.get();

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
                if (imagen.length >= 2 && imagen[0] == (byte) 0x89 && imagen[1] == (byte) 0x50) {
                    mimeType = "image/png";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        .body(imagen);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/actualizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarPerfil(
            // ⚠️ Recomendado: NO confiar en el id que manda el front. Igual lo dejamos por compatibilidad.
            @RequestParam(value = "id", required = false) Integer idFront,

            @RequestParam("costo") Integer costo,
            @RequestParam("frecuenciaCitas") Integer frecuenciaCitas,
            @RequestParam("nota") String nota,
            @RequestParam("especialidadId") Integer especialidadId,
            @RequestParam("localidadId") Integer localidadId,
            @RequestParam("horario") String horario,
            @RequestParam(value = "archivoFoto", required = false) MultipartFile archivoFoto,
            Authentication authentication
    ) {
        Optional<Medico> medicoOpt = getMedicoAutenticado(authentication);

        if (medicoOpt.isEmpty()) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Medico medico = medicoOpt.get();

        // Seguridad extra: si mandan id y no coincide, rechazás
        if (idFront != null && !idFront.equals(medico.getId())) {
            return ResponseEntity.status(403).body("No autorizado (id no corresponde al médico autenticado).");
        }

        // Validar formato de horario: 7 días separados por ;
        String[] dias = horario.split(";");
        if (dias.length < 7) {
            String[] diasCompletos = new String[7];
            System.arraycopy(dias, 0, diasCompletos, 0, dias.length);
            for (int i = dias.length; i < 7; i++) diasCompletos[i] = "";
            dias = diasCompletos;
        } else if (dias.length > 7) {
            return ResponseEntity.badRequest().body("El horario tiene más de 7 días. Verifique el formato.");
        }

        for (String dia : dias) {
            if (!dia.matches("^([0-9]{1,2}-[0-9]{1,2})?(,([0-9]{1,2}-[0-9]{1,2})?)?$")) {
                return ResponseEntity.badRequest().body("Formato de horario incorrecto en uno de los días: " + dia);
            }
        }

        // Foto
        Foto fotoFinal = medico.getFoto();
        if (archivoFoto != null && !archivoFoto.isEmpty()) {
            try {
                Foto nuevaFoto = new Foto();
                nuevaFoto.setImagen(archivoFoto.getBytes());
                fotoFinal = service.create(nuevaFoto);
            } catch (IOException e) {
                return ResponseEntity.internalServerError().body("Error al procesar la imagen");
            }
        }

        medico.setCosto(BigDecimal.valueOf(costo));
        medico.setFrecuenciaCitas(frecuenciaCitas);
        medico.setNota(nota);
        medico.setFoto(fotoFinal);
        medico.setHorario(horario);

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
