package com.example.sistema_citas.logic;
import jakarta.persistence.*;
import java.sql.Blob;

@Entity
@Table(name = "foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB", nullable = false)
    private byte[] imagen;
    //private byte[] foto;

    @Column(name = "tipo_mime")
    private String tipoMime;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getTipoMime() { return tipoMime; }

    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }
    public boolean tieneImagen() {
        return imagen != null && imagen.length > 0;
    }
}

