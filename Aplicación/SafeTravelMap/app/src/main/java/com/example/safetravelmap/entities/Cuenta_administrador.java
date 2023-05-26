package com.example.safetravelmap.entities;

public class Cuenta_administrador extends Cuenta{
    public int id_cuenta_administrador;

    public Cuenta_administrador(int cod_usuario, String nombre, String apellido, String rut, int edad, String pass, String nombre_usuario, String correo, int id_cuenta_administrador) {
        super(cod_usuario, nombre, apellido, rut, edad, pass, nombre_usuario, correo);
        this.id_cuenta_administrador = id_cuenta_administrador;
    }

    public int getId_cuenta_administrador() {
        return id_cuenta_administrador;
    }

    public void setId_cuenta_administrador(int id_cuenta_administrador) {
        this.id_cuenta_administrador = id_cuenta_administrador;
    }
}
