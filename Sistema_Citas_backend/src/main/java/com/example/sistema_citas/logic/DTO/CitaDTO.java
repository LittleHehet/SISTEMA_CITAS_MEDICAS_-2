package com.example.sistema_citas.logic.DTO;

import com.example.sistema_citas.logic.Cita;

import java.time.LocalDate;

public class CitaDTO {
    private Integer id;
    private String horainicio;
    private String horafinal;
    private String dia;
    private String estado;
    private LocalDate fechaHora;
    private String usuarioNombre;
    private String usuarioApellido;
    private String medicoNombre;
    private String medicoApellido;
    private String nota;
    public CitaDTO(Cita cita) {
        this.id = cita.getId();
        this.horainicio = cita.getHorainicio();
        this.horafinal = cita.getHorafinal();
        this.dia = cita.getDia();
        this.estado = cita.getEstado();
        this.fechaHora = cita.getFechaHora();
        this.usuarioNombre = cita.getUsuario().getNombre();
        this.usuarioApellido = cita.getUsuario().getApellido();
        this.medicoNombre = cita.getMedico().getNombre();
        this.medicoApellido = cita.getMedico().getApellido();
        this.nota = cita.getNota();
    }

    public String getMedicoNombre() {
        return medicoNombre;
    }
    public void setMedicoNombre(String medicoNombre) {
        this.medicoNombre = medicoNombre;
    }
    public String getMedicoApellido() {
        return medicoApellido;
    }
    public void setMedicoApellido(String medicoApellido) {
        this.medicoApellido = medicoApellido;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHorainicio() {
        return horainicio;
    }

    public void setHorainicio(String horainicio) {
        this.horainicio = horainicio;
    }

    public String getHorafinal() {
        return horafinal;
    }

    public void setHorafinal(String horafinal) {
        this.horafinal = horafinal;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDate fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getUsuarioApellido() {
        return usuarioApellido;
    }

    public void setUsuarioApellido(String usuarioApellido) {
        this.usuarioApellido = usuarioApellido;
    }

    public String getNota() {
        return nota;
    }
    public void setNota(String nota) {
        this.nota = nota;
    }

}
