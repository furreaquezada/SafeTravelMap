package com.example.safetravelmap.entities;

public class Cuenta {
    public String cod_usuario;
    public String nombre;
    public String apellido;
    public String rut;
    public int edad;

    public String pass;

    public String nombre_usuario;

    public String correo;

    public int puntaje;

    public Cuenta(String cod_usuario, String nombre, String apellido, String rut, int edad, String pass, String nombre_usuario, String correo, int puntaje) {
        this.cod_usuario = cod_usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.edad = edad;
        this.pass = pass;
        this.nombre_usuario = nombre_usuario;
        this.correo = correo;
        this.puntaje = puntaje;
    }

    public Cuenta() {

    }

    public String getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(String cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
}
