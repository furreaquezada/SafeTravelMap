package com.example.safetravelmap.entities;

public class Reincidencia {
    public double latitud;
    public String imagen;
    public String desc;
    public int cod_usuario;
    public boolean tipo_usuario;

    public Reincidencia(double latitud, String imagen, String desc, int cod_usuario, boolean tipo_usuario) {
        this.latitud = latitud;
        this.imagen = imagen;
        this.desc = desc;
        this.cod_usuario = cod_usuario;
        this.tipo_usuario = tipo_usuario;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(int cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    public boolean isTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(boolean tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }
}
