package com.STM.safetravelmap.entities;

public class Tutorial {
    public String id;
    public String titulo;
    public String desc;
    public boolean tipo_usuario;
    public String cod_usuario;

    public Tutorial(String id, String titulo, String desc, boolean tipo_usuario, String cod_usuario) {
        this.id = id;
        this.titulo = titulo;
        this.desc = desc;
        this.tipo_usuario = tipo_usuario;
        this.cod_usuario = cod_usuario;
    }

    public Tutorial() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(boolean tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public String getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(String cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    public boolean listadoTutoriales(){
        return true;
    }
}
