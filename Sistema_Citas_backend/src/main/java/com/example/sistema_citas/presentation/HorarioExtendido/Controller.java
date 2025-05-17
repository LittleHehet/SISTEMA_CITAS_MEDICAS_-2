package com.example.sistema_citas.presentation.HorarioExtendido;

import com.example.sistema_citas.logic.Cont_Citas;
import com.example.sistema_citas.logic.Dia;
import com.example.sistema_citas.logic.Medico;
import com.example.sistema_citas.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import java.util.*;

@org.springframework.stereotype.Controller("horarioextendido")
@SessionAttributes("horarioextendido")
public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/HorarioExtendido")
    public String HorarioExtendido(@RequestParam(name = "medicoId", required = false) Integer medicoId,  // Parámetro para el ID del médico (Integer)
                                   Model model) {

        //model.addAttribute("medico", service.findMedicoById(medicoId));
        //Si hubieramos creado una tabla de horarios desde el inicio: model.addAttribute("horarios",service.findAllHorarioByMedico(medicoId));
        Optional<Medico> om = service.findMedicoById(medicoId);

        if (om.isPresent()) {
            Medico m = om.get();
            model.addAttribute("medico",m);
            //model.addAttribute("localidad", service.getMedicoLocalidad(m.getLocalidad().getId()));
            //model.addAttribute("especialidad",service.getMedicoEspecialidad(m.getEspecialidad().getId()));
            Map<Medico, List<Dia>> medicoSemana = new HashMap<>();
            Cont_Citas cc = new Cont_Citas();

            List<Dia> semanaCompleta = cc.EstimarSemanaHorario(m.getHorario(), m.getFrecuenciaCitas());

                // Buscar el día correspondiente en la semana del médico
//                for (Dia d : semanaCompleta) {
//                    if (d.getNombre().equals(diaNombre)) {
//                        d.setMedico(m);
//                        d.setFecha(fecha); // Aquí le asignamos la fecha real
//                        proximosDias.add(d);
//                        break;
//                    }
//                }
            model.addAttribute("semanaCompleta", semanaCompleta);
        }

        return "presentation/HorarioExtendido/view";
    }

}
