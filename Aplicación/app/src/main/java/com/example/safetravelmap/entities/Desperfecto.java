package com.example.safetravelmap.entities;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Desperfecto {
    public String con_usuario;
    public String desc;
    public String desperfecto;
    public String imagen;
    public double latitud;
    public double longitud;
    public int riesgo;
    public boolean tipo_usuario;

    public int puntos = 100;

    public boolean solucionado;

    public boolean tres_reincidencias;

    public ArrayList<Reincidencia> reincidencias = new ArrayList<Reincidencia>();


    public Desperfecto(String con_usuario, String desc, String desperfecto, String imagen, double latitud, double longitud, int riesgo, boolean tipo_usuario, int puntos, boolean solucionado, boolean tres_reincidencias) {
        this.con_usuario = con_usuario;
        this.desc = desc;
        this.desperfecto = desperfecto;
        this.imagen = imagen;
        this.latitud = latitud;
        this.longitud = longitud;
        this.riesgo = riesgo;
        this.tipo_usuario = tipo_usuario;
        this.puntos = puntos;
        this.solucionado = solucionado;
        this.tres_reincidencias = tres_reincidencias;
    }

    public Desperfecto() {
    }

    public String getCon_usuario() {
        return con_usuario;
    }

    public void setCon_usuario(String con_usuario) {
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

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public boolean isSolucionado() {
        return solucionado;
    }

    public void setSolucionado(boolean solucionado) {
        this.solucionado = solucionado;
    }

    public boolean isTres_reincidencias() {
        return tres_reincidencias;
    }

    public void setTres_reincidencias(boolean tres_reincidencias) {
        this.tres_reincidencias = tres_reincidencias;
    }

    public ArrayList<Reincidencia> getReincidencias() {
        return reincidencias;
    }

    public void setReincidencias(ArrayList<Reincidencia> reincidencias) {
        this.reincidencias = reincidencias;
    }



    @NonNull
    @Override
    public String toString() {
        return "El desperfecto es: " + this.desc + ", (" + this.latitud + ", " + this.longitud + ")";
    }

    public boolean restarPuntos(int i) {
        return true;
    }
}
