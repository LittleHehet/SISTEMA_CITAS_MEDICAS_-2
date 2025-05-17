package com.example.sistema_citas.logic;

import java.util.List;
import java.time.LocalDate;

public class Dia {

    private LocalDate fecha;
    String Nombre;
    List<CalcularHorario> Horarios;
    private Medico medico;
    public void setMedico(Medico medico) {this.medico = medico;}
    public Dia()
    {}

    public Dia(String nombre, List<CalcularHorario> horarios)
    {
        this.Nombre = nombre;
        this.Horarios = horarios;
        this.medico = new Medico();
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setNombre(int dia) {
        Nombre = GetDayName(dia);
    }

    public List<CalcularHorario> getCitas() {
        return Horarios;
    }

    public void setHorarios(List<CalcularHorario> horarios) {
        Horarios = horarios;
    }

    public List<CalcularHorario> getHorarios() {
        return Horarios;
    }

    private String GetDayName(int day)
    {
        switch (day)
        {
            case 1:
                return "Lunes";
            case 2:
                return "Martes";
            case 3:
                return "Miércoles";
            case 4:
                return "Jueves";
            case 5:
                return "Viernes";
            case 6:
                return "Sábado";
            case 7:
                return "Domingo";
        }
        return "";

    }

    @Override
    public String toString() {
        // Crear un StringBuilder para construir la cadena final
        StringBuilder sb = new StringBuilder();

        // Agregar el nombre del día
        sb.append("Día: ").append(Nombre).append("\n");

        // Si hay horarios, agregar la representación de cada uno
        if (Horarios != null && !Horarios.isEmpty()) {
            sb.append("Horarios: ");
            for (CalcularHorario horario : Horarios) {
                sb.append("[").append(horario.getHorainicio()).append(" - ").append(horario.getHorafin()).append("] ");
            }
        } else {
            sb.append("No hay horarios disponibles.");
        }

        // Devolver la cadena resultante
        return sb.toString();
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getInicio(String inicio){
        for(CalcularHorario horario : Horarios){
            if(horario.getHorainicio().equals(inicio)){
                return horario.getHorainicio();
            }
        }
        return "no existe el horario inicio";
    }
    public String getFin(String fin){
        for(CalcularHorario horario : Horarios){
            if(horario.getHorafin().equals(fin)){
                return horario.getHorafin();
            }
        }
        return "no existe el horario final";
    }

}
