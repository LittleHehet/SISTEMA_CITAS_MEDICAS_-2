package com.example.sistema_citas.logic.DTO;

// DTO para CalcularHorario
public class CalcularHorarioDTO {
    private String horainicio;
    private String horafin;
    private boolean reservado;

    public CalcularHorarioDTO() {}

    public CalcularHorarioDTO(String horainicio, String horafin) {
        this.horainicio = horainicio;
        this.horafin = horafin;
    }
    public CalcularHorarioDTO(String horainicio, String horafin,boolean reservado) {
        this.horainicio = horainicio;
        this.horafin = horafin;
        this.reservado = reservado;
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

    public boolean isReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }
}

