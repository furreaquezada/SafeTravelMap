package com.example.safetravelmap.entities;

import androidx.annotation.NonNull;

public class Desperfecto {
    public int con_usuario;
    public String desc;
    public String desperfecto;
    public String imagen;
    public double latitud;
    public double longitud;
    public int riesgo;
    public boolean tipo_usuario;

    public Desperfecto(int con_usuario, String desc, String desperfecto, String imagen, double latitud, double longitud, int riesgo, boolean tipo_usuario) {
        this.con_usuario = con_usuario;
        this.desc = desc;
        this.desperfecto = desperfecto;
        this.imagen = imagen;
        this.latitud = latitud;
        this.longitud = longitud;
        this.riesgo = riesgo;
        this.tipo_usuario = tipo_usuario;
    }

    public Desperfecto() {
    }

    public int getCon_usuario() {
        return con_usuario;
    }

    public void setCon_usuario(int con_usuario) {
        this.con_usuario = con_usuario;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesperfecto() {
        return desperfecto;
    }

    public void setDesperfecto(String desperfecto) {
        this.desperfecto = desperfecto;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(int riesgo) {
        this.riesgo = riesgo;
    }

    public boolean isTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(boolean tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    @NonNull
    @Override
    public String toString() {
        return "El desperfecto es: " + this.desc + ", (" + this.latitud + ", " + this.longitud + ")";
    }
}
