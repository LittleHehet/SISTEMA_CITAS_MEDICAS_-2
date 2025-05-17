package com.example.sistema_citas.logic;

public class CalcularHorario {
    private int inicio;
    private int fin;
    private String horainicio;
    private String horafin;
    private Medico medico;
    public CalcularHorario() {}
    public CalcularHorario(int inicio, int fin) {
        this.inicio = inicio;
        this.fin = fin;
        horainicio = convertMinutesHour(inicio);
        horafin = convertMinutesHour(fin);
        medico = new Medico();
    }
    //metodo para crear horainicio y horafinal
    public String convertMinutesHour(int minutes)
    {
        String formatted;
        int hour, minute;

        hour = minutes / 60;
        minute = minutes % 60;

        formatted = Integer.toString(hour);
        formatted +=":";

        if(minute==0)
        {
            formatted +="00";
        }else {
            formatted +=Integer.toString(minute);
        }
        return formatted;
    }
    public Medico getMedico() {return medico;}
    public void setMedico(Medico medico) {this.medico = medico;}
    public int getInicio() {return inicio;}
    public void setInicio(int inicio) {this.inicio = inicio;}
    public int getFin() {return fin;}
    public void setFin(int fin) {this.fin = fin;}
    public String getHorainicio() {return horainicio;}
    public void setHorainicio(String horainicio) {this.horainicio = horainicio;}
    public String getHorafin() {return horafin;}
    public void setHorafin(String horafin) {this.horafin = horafin;}
}
