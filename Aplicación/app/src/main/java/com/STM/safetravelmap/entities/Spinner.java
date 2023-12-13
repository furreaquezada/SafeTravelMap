package com.STM.safetravelmap.entities;

public class Spinner {
    String nombre;

    public Spinner(String nombre) {
        this.nombre = nombre;
    }

    public Spinner() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean cargarListado() {
        return true;
    }
}
