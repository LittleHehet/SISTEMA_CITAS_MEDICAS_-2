package com.example.sistema_citas.logic;

public class UsuarioConEstadoDTO {
    private Integer cedula;
    private String nombre;
    private String apellido;
    private String estado;
    private Integer id;

    public UsuarioConEstadoDTO() {
        this.cedula = 0;
        this.nombre = "";
        this.apellido =  "";
        this.estado =  "";
        this.id= 0;
    }

    public UsuarioConEstadoDTO(Usuario usuario) {
        this.cedula = usuario.getCedula();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.estado = "";
        this.id = usuario.getId();
    }

    public UsuarioConEstadoDTO(Integer cedula, String nombre, String apellido, String estado,Integer id) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.estado = estado;
        this.id = 0;
    }

    public Integer getCedula() {return cedula;}
    public void setCedula(Integer cedula) {this.cedula = cedula;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getApellido() {return apellido;}
    public void setApellido(String apellido) {this.apellido = apellido;}
    public String getEstado() {return estado;}
    public void setEstado(String estado) {this.estado = estado;}
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

}
