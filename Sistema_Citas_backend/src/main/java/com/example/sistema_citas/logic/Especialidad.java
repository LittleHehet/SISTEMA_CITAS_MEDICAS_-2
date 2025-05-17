package com.example.sistema_citas.logic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "especialidad")
public class Especialidad {
    @Id
    @Column(name = "especialidad_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "especialidad_nombre", length = 50)
    private String especialidadNombre;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEspecialidadNombre() {
        return especialidadNombre;
    }

    public void setEspecialidadNombre(String especialidadNombre) {
        this.especialidadNombre = especialidadNombre;
    }

}