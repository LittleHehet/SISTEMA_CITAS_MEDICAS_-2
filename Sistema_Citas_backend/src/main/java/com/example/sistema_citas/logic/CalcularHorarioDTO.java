package com.example.sistema_citas.logic;

import java.time.LocalDate;
import java.util.List;

// DTO para CalcularHorario
public class CalcularHorarioDTO {
    private String horainicio;
    private String horafin;

    public CalcularHorarioDTO() {}

    public CalcularHorarioDTO(String horainicio, String horafin) {
        this.horainicio = horainicio;
        this.horafin = horafin;
    }

    public String getHorainicio() {
        return horainicio;
    }

    public void setHorainicio(String horainicio) {
        this.horainicio = horainicio;
    }

    public String getHorafin() {
        return horafin;
    }

    public void setHorafin(String horafin) {
        this.horafin = horafin;
    }
}

