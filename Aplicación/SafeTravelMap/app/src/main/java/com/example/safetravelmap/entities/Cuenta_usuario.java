package com.example.safetravelmap.entities;

public class Cuenta_usuario extends Cuenta{
    public int id_cuenta_usuario;

    public Cuenta_usuario(int cod_usuario, String nombre, String apellido, String rut, int edad, String pass, String nombre_usuario, String correo, int id_cuenta_usuario) {
        super(cod_usuario, nombre, apellido, rut, edad, pass, nombre_usuario, correo);
        this.id_cuenta_usuario = id_cuenta_usuario;
    }

    public int getId_cuenta_usuario() {
        return id_cuenta_usuario;
    }

    public void setId_cuenta_usuario(int id_cuenta_usuario) {
        this.id_cuenta_usuario = id_cuenta_usuario;
    }
}
