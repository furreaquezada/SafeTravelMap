package com.STM.safetravelmap.entities;

public class Voto {
    public String cod_usuario;
    public double latitud;

    public boolean tipo_usuario;

    public Voto(String cod_usuario, double latitud, boolean tipo_usuario) {
        this.cod_usuario = cod_usuario;
        this.latitud = latitud;
        this.tipo_usuario = tipo_usuario;
    }

    public String getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(String cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public boolean isTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(boolean tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }
}
