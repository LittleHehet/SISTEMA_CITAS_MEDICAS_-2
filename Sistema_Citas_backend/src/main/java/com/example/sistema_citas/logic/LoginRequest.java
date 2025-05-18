package com.example.sistema_citas.logic;

// Para recibir los datos del usuario que quiere registrarse
public class LoginRequest {
    private Integer cedula;
    private String clave;

    public LoginRequest() {}

    public LoginRequest(Integer cedula, String clave) {
        this.cedula = cedula;
        this.clave = clave;
    }

    public Integer getCedula() {return cedula;}
    public void setCedula(Integer cedula) {this.cedula = cedula;}
    public String getClave() {return clave;}
    public void setClave(String clave) {this.clave = clave;}

}
