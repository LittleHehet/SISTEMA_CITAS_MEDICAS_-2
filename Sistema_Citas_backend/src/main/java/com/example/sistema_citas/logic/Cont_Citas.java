package com.example.sistema_citas.logic;

import java.util.ArrayList;
import java.util.List;

public class Cont_Citas {

//    private List<CalcularHorario> EstimarHorarioDia(String rango, int frecuencia) {
//        int Inicio = 0, Fin = 0, Frecuencia = frecuencia;
//
//        List<CalcularHorario> horarios = new ArrayList<>();
//
//        String regex = "[,]";
//        String[] rangos = rango.split(regex);
//        for (String nodo : rangos) {
//            regex = "[-]";
//            String[] dias = nodo.split(regex);
//            Inicio = Integer.parseInt(dias[0].toString());
//            Fin = Integer.parseInt(dias[1].toString());
//            horarios.addAll(EstimarHorarioDia(Inicio, Fin, Frecuencia));
//        }
//        return horarios;
//    }
private List<CalcularHorario> EstimarHorarioDia(String rango, int frecuencia) {
    List<CalcularHorario> horarios = new ArrayList<>();
    String[] rangos = rango.split(",");

    for (String nodo : rangos) {
        if (nodo.isBlank()) continue; // ✅ Ignora rangos vacíos

        String[] horas = nodo.split("-");
        if (horas.length != 2) continue; // ✅ Ignora pares mal formateados

        int Inicio = Integer.parseInt(horas[0]);
        int Fin = Integer.parseInt(horas[1]);
        horarios.addAll(EstimarHorarioDia(Inicio, Fin, frecuencia));
    }

    return horarios;
}


    private List<CalcularHorario> EstimarHorarioDia(int Inicio, int Fin, int Frecuencia) {
        List<CalcularHorario> horarios = new ArrayList<>();
        Inicio *= 60; // se convierte a minutos
        Fin *= 60;// se convierte a minutos
        int TotalHorarios = (Fin - Inicio) / Frecuencia;
        CalcularHorario horario = new CalcularHorario();

        while (TotalHorarios > 0) {
            horario = new CalcularHorario(Inicio, (Inicio + Frecuencia));
            horarios.add(horario);
            TotalHorarios--;
            Inicio += Frecuencia;
        }

        return horarios;
    }

    public List<Dia> EstimarSemanaHorario(String horario, int frecuencia) {
        String regex = "[;]";
        List<Dia> agenda = new ArrayList<>();
        String[] dias = horario.split(regex);
        int diacont = 1;

        Dia dia;

//        for (String nodo : dias) {
//            dia = new Dia();
//            dia.setNombre(diacont++);
//            agenda.add(dia);
//            if (nodo.length() > 0)
//                dia.setHorarios(EstimarHorarioDia(nodo, frecuencia));
//        }
        for (String nodo : dias) {
            dia = new Dia();
            dia.setNombre(diacont++);
            if (nodo.length() > 0) {
                dia.setHorarios(EstimarHorarioDia(nodo, frecuencia));
            } else {
                dia.setHorarios(new ArrayList<>()); // 🛠️ Asegura lista vacía si no hay horario
            }
            agenda.add(dia); // 💡 Asegura que el día se agregue siempre
        }
        return agenda;
    }
}
