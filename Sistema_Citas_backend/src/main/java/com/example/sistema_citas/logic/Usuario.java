package com.example.sistema_citas.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuarios_id", nullable = false)
    private Integer id;

    @Column(name = "cedula", unique = true)
    private Integer cedula;

    @Size(max = 30)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Size(max = 30)
    @NotNull
    @Column(name = "apellido", nullable = false, length = 30)
    private String apellido;

    @Size(max = 100)
    @Column(name = "clave", length = 100)
    private String clave;

    @NotNull
    @Column(name = "perfil")
    private String perfil;

    @Size(max = 120)
    @Column(name = "email", unique = true, length = 120)
    private String email;


    @NotNull
    @Column(name = "auth_provider", nullable = false, length = 20)
    private String authProvider = "LOCAL";


    @Size(max = 64)
    @Column(name = "google_sub", unique = true, length = 64)
    private String googleSub;

    public Usuario() {}

    public Usuario(Integer cedula, String nombre, String apellido, String clave, String perfil ) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.clave = clave;
        this.perfil = perfil;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCedula() {
        return cedula;
    }

    public void setCedula(Integer cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getAuthProvider() {return authProvider;}
    public void setAuthProvider(String authProvider) {this.authProvider = authProvider;}
    public String getGoogleSub() {return googleSub;}
    public void setGoogleSub(String googleSub) {this.googleSub = googleSub;}
    public void setNombre(String givenName) {this.nombre = givenName;}
    public void setApellido(String familyName) {this.apellido = familyName;}
}