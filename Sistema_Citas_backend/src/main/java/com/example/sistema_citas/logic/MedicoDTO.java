package com.example.sistema_citas.logic;

import java.math.BigDecimal;
import java.util.List;

public class MedicoDTO {
    private Integer id;
    private String nombre;            // nombre del usuario asociado
    private String apellido;          // apellido del usuario asociado
    private  String estado;
    private String especialidadNombre;
    private String localidadNombre;
    private BigDecimal costo;
    private List<DiaDTO> disponibilidad;

    public MedicoDTO() {}

    public MedicoDTO(Integer id, String nombre, String apellido, String especialidadNombre,
                     String localidadNombre, BigDecimal costo, List<DiaDTO> disponibilidad , String estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidadNombre = especialidadNombre;
        this.localidadNombre = localidadNombre;
        this.costo = costo;
        this.disponibilidad = disponibilidad;
        this.estado = estado;
    }

    public String getEstado() {return estado;}
    public void setEstado(String estado) {this.estado = estado;}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEspecialidadNombre() {
        return especialidadNombre;
    }

    public void setEspecialidadNombre(String especialidadNombre) {
        this.especialidadNombre = especialidadNombre;
    }

    public String getLocalidadNombre() {
        return localidadNombre;
    }

    public void setLocalidadNombre(String localidadNombre) {
        this.localidadNombre = localidadNombre;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public List<DiaDTO> getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(List<DiaDTO> disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}
