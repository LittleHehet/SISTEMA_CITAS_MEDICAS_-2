package com.example.sistema_citas.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "localidad")
public class Localidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "localidad_id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @Column(name = "localidad_nombre", length = 20 , unique = true)
    private String localidadNombre;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocalidadNombre() {
        return localidadNombre;
    }

    public void setLocalidadNombre(String localidadNombre) {
        this.localidadNombre = localidadNombre;
    }

}
