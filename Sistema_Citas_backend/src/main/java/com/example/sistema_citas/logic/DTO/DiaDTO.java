package com.example.sistema_citas.logic.DTO;

import java.time.LocalDate;
import java.util.List;

// DTO para Dia
public class DiaDTO {
    private String nombre;
    private LocalDate fecha;
    private List<CalcularHorarioDTO> horarios;

    public DiaDTO() {}

    public DiaDTO(String nombre, LocalDate fecha, List<CalcularHorarioDTO> horarios) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.horarios = horarios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<CalcularHorarioDTO> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<CalcularHorarioDTO> horarios) {
        this.horarios = horarios;
    }
}
