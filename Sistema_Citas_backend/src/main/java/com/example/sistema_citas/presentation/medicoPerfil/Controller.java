package com.example.sistema_citas.presentation.medicoPerfil;
import com.example.sistema_citas.data.FotoRepository;
import com.example.sistema_citas.logic.Foto;
import com.example.sistema_citas.logic.Medico;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.example.sistema_citas.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

@org.springframework.stereotype.Controller("MedicoPerfil")
@SessionAttributes("medicos")
public class Controller {
    @Autowired
    private FotoRepository fotoRepository;
    @Autowired
    private Service service;
    @GetMapping("/Medico-Perfil")
    public String show(HttpSession session,
                       Model model) {
//        Integer id = (Integer) session.getAttribute("medicoId");
//        if (id != null) {
//            Optional<Medico> medicoOpt = service.findMedicoById(id);
//            if (medicoOpt.isPresent()) {
//                model.addAttribute("medico", medicoOpt.get());
//            } else {
//                model.addAttribute("error", "Médico no encontrado");
//                return "redirect:/error";
//            }
//        } else {
//            model.addAttribute("medico", new Medico());
//        }
        Medico medico = (Medico) session.getAttribute("medico");

        if (medico != null) {
            model.addAttribute("medico", medico);
        } else {
            return "redirect:/Sign-in";
        }

        // Cargar especialidades y localidades desde la base de datos
        model.addAttribute("especialidades", service.getAllEspecialidades());
        model.addAttribute("localidades", service.getAllLocalidades());

        return "presentation/medicoPerfil/view";
    }

    @GetMapping("/medico-foto")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarFoto(@RequestParam("id") Integer idMedico) {
        Optional<Medico> medicoOpt = service.findMedicoById(idMedico);
        if (medicoOpt.isPresent()) {
            Medico medico = medicoOpt.get();
            Foto foto = medico.getFoto();
            if (foto != null && foto.getImagen() != null) {
                byte[] imagenBytes = foto.getImagen();

                String mimeType = "image/jpeg";
                if (imagenBytes[0] == (byte) 0x89 && imagenBytes[1] == (byte) 0x50) {
                    mimeType = "image/png";
                } else if (imagenBytes[0] == (byte) 0xFF && imagenBytes[1] == (byte) 0xD8) {
                    mimeType = "image/jpeg";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        .body(imagenBytes);
            }
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("medico") Medico medico,
                             @RequestParam("archivoFoto") MultipartFile archivoFoto,
                             Model model) {
        if (medico.getId() == null) {
            model.addAttribute("error", "ID del médico no proporcionado");
            return "presentation/medicoPerfil/view";
        }

        Optional<Medico> medicoExistenteOpt = service.findMedicoById(medico.getId());
        if (medicoExistenteOpt.isEmpty()) {
            model.addAttribute("error", "Médico no encontrado en la base de datos");
            return "presentation/medicoPerfil/view";
        }

        Medico medicoExistente = medicoExistenteOpt.get();

        // Actualizar foto si fue subida
        Foto fotoGuardada = null;

        if (archivoFoto != null && !archivoFoto.isEmpty()) {
            try {
                byte[] imagenBytes = archivoFoto.getBytes();
                Foto nuevaFoto = new Foto();
                nuevaFoto.setImagen(imagenBytes);
                fotoGuardada = service.create(nuevaFoto);
            } catch (IOException e) {
                model.addAttribute("error", "Error al procesar la imagen.");
                return "presentation/medicoPerfil/view";
            }
        } else {
            // No se subió una nueva foto, conservar la anterior
            fotoGuardada = medicoExistente.getFoto();
        }

        if (medicoExistente.getUsuarios() != null && medico.getUsuarios() != null) {
            medicoExistente.getUsuarios().setNombre(medico.getUsuarios().getNombre());
            medicoExistente.getUsuarios().setApellido(medico.getUsuarios().getApellido());
        }

        // Validar y actualizar otros campos
        medicoExistente.setEspecialidad(medico.getEspecialidad());
        medicoExistente.setCosto(medico.getCosto());
        medicoExistente.setLocalidad(medico.getLocalidad());
        // Solo actualizar el horario si estaba vacío previamente
        if (medicoExistente.getHorario() == null || medicoExistente.getHorario().isEmpty()) {
            medicoExistente.setHorario(medico.getHorario());
        }
        medicoExistente.setFrecuenciaCitas(medico.getFrecuenciaCitas());
        medicoExistente.setNota(medico.getNota());

        if (!medicoExistente.getHorario().matches("^(([0-9]{1,2}-[0-9]{1,2})(,[0-9]{1,2}-[0-9]{1,2})?(;)?)+$")) {
            model.addAttribute("error", "El horario ingresado no cumple con el formato correcto.");
            return "presentation/medicoPerfil/view";
        }

        service.updateMedico(medico, fotoGuardada);

        // Recargar el médico actualizado para que la imagen se muestre
        Medico medicoActualizado = service.findMedicoById(medico.getId()).orElse(medico);
        model.addAttribute("medico", medicoActualizado);

        // 🔥 Agregar nuevamente las listas para que los combo box se carguen bien
        model.addAttribute("especialidades", service.getAllEspecialidades());
        model.addAttribute("localidades", service.getAllLocalidades());
        model.addAttribute("medico", medicoActualizado);
        model.addAttribute("mensaje", "Perfil actualizado correctamente");

        return "presentation/medicoPerfil/view";
    }


} //fin de controller
