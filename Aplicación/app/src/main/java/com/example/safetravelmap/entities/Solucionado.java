package com.example.safetravelmap.entities;

public class Solucionado {
    public String cod_usuario;
    public String latitud;

    public Solucionado(String cod_usuario, String latitud) {
        this.cod_usuario = cod_usuario;
        this.latitud = latitud;
    }

    public String getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(String cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }
}
