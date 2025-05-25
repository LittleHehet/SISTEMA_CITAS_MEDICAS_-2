package com.example.sistema_citas.logic;


public class CitaDTO {
    private Integer medicoId;
    private String dia;
    private String fecha; // Formato: yyyy-MM-dd
    private String horaInicio;
    private String horaFin;
    private MedicoDTO medico;
    // Getters y Setters

    public CitaDTO() {}


    public MedicoDTO getMedico() {
        return medico;
    }
    public void setMedico(MedicoDTO medico) {
        this.medico = medico;
    }

    public Integer getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}
