package com.example.sistema_citas.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "cita")
public class Cita {
    @Id
    @Column(name = "codigo_cita", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Lob
    @Column(name = "nota")
    private String nota;

    @Lob
    @Column(name = "horainicio")
    private String horainicio;

    @Lob
    @Column(name = "horafinal")
    private String horafinal;

    @Lob
    @Column(name = "dia")
    private String dia;

    @NotNull
    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;

    @Lob
    @Column
    private int inicio;

    @Lob
    @Column
    private int fin;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private LocalDate fechaHora;

    public Cita(){}

    public Cita(Integer id,Usuario usuario,Medico medico,String nota,int inicio,int fin,String dia,String estado){
        this.id = id;
        this.usuario = usuario;
        this.medico = medico;
        this.nota = nota;
        this.dia = dia;
        this.estado = estado;
        this.inicio =inicio;
        this.fin = fin;
    }
    public Cita(int i, int f){
        this.inicio = i;
        this.fin = f;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getHorainicio(){return horainicio;}

    public void setHorainicio(String horainicio){this.horainicio = horainicio;}

    public String getHorafinal(){return horafinal;}

    public void setHorafinal(String horafinal){this.horafinal = horafinal;}

    public String getDia(){return dia;}

    public void setDia(String dia){this.dia = dia;}

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

    public int getInicio(){return inicio; }
    public void setInicio(int inicio){this.inicio = inicio;}
    public int getFin(){return fin; }
    public void setFin(int fin){this.fin = fin;}

    // Getter y Setter
    public LocalDate getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDate fechaHora) {
        this.fechaHora = fechaHora;
    }

}