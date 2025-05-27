package com.example.sistema_citas.logic;

public class LoginResponse {
    private String token;
    private String perfil;
    private String medicoEstado;
    private boolean perfilCompleto;
    private Integer medicoId;

    public LoginResponse(String token, String perfil) {
        this.token = token;
        this.perfil = perfil;
    }

    public LoginResponse(String token, String perfil, String medicoEstado , boolean perfilCompleto, Integer medicoId) {
        this.token = token;
        this.perfil = perfil;
        this.medicoEstado = medicoEstado;
        this.perfilCompleto = perfilCompleto;
        this.medicoId = medicoId;
    }

    public String getMedicoEstado() {
        return medicoEstado;
    }
    public void setMedicoEstado(String medicoEstado) {
        this.medicoEstado = medicoEstado;
    }

    public Integer getMedicoId() {
        return medicoId;
    }
    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

    public boolean isPerfilCompleto() {
        return perfilCompleto;
    }
    public void setPerfilCompleto(boolean perfilCompleto) {
        this.perfilCompleto = perfilCompleto;
    }

    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}
    public String getPerfil() {return perfil;}
    public void setPerfil(String perfil) {this.perfil = perfil;}

}
