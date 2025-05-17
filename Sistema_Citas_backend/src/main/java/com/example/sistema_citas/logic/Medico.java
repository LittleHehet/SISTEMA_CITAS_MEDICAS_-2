package com.example.sistema_citas.logic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Transient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "medicos")
public class Medico {
    @Id
    @Column(name = "medico_id", nullable = false)
    private Integer id;

    @MapsId
    //@OneToOne(fetch = FetchType.LAZY, optional = false)
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Usuario usuario;


    //@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "especialidad")
    private Especialidad especialidad;

    @Column(name = "costo", precision = 10, scale = 2)
    private BigDecimal costo;


    //@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "localidad")
    private Localidad localidad;

    @Size(max = 100)
    @Column(name = "horario", length = 100)
    private String horario;


    @Column(name = "frecuencia_citas")
    private Integer frecuenciaCitas;


    @Lob
    @Column(name = "nota")
    private String nota;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "foto_id")
    private Foto foto;


    @Column(name = "estado")
    private String estado;

    public Medico() {}

    public Medico(Integer id, Usuario usuarios, Especialidad especialidad, BigDecimal costo,
                  Localidad localidad, String horario, Integer frecuenciaCitas
                  , String nota, Foto foto,  String estado)
    {
        this.id = id;
        this.usuario = usuarios;
        this.especialidad = especialidad;
        this.costo = costo;
        this.localidad = localidad;
        this.horario = horario;
        this.frecuenciaCitas = frecuenciaCitas;
        this.nota = nota;
        this.foto = foto;
        this.estado = estado;
    }

    public Medico(Integer id) {
        this.id = id;
        // Dejar todos los demás campos como null
        this.usuario = null;
        this.especialidad = null;
        this.costo = null;
        this.localidad = null;
        this.horario = null;
        this.frecuenciaCitas = null;
        this.nota = null;
        this.foto = null;
        this.estado = "pendiente";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuarios() {
        return usuario;
    }

    public void setUsuarios(Usuario usuarios) {
        this.usuario = usuarios;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Integer getFrecuenciaCitas() {
        return frecuenciaCitas;
    }

    public void setFrecuenciaCitas(Integer frecuenciaCitas) {
        this.frecuenciaCitas = frecuenciaCitas;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto=foto; }

    public String getNombre() {
        return this.usuario != null ? this.usuario.getNombre() : "";
    }

    public String getApellido() {
        return this.usuario != null ? this.usuario.getApellido() : "";
    }

    public boolean isPendiente() {
        return this.especialidad != null || this.costo != null
                || this.localidad != null || this.horario != null || this.frecuenciaCitas != null
                || this.nota != null || this.foto != null;
    }


}
