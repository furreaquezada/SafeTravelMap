package com.STM.safetravelmap.entities;

public class GaleriaTutorial {
    public String id;
    public String desc;
    public String idTutorial;
    public String titulo;

    public GaleriaTutorial(String id, String desc, String idTutorial, String titulo) {
        this.id = id;
        this.desc = desc;
        this.idTutorial = idTutorial;
        this.titulo = titulo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIdTutorial() {
        return idTutorial;
    }

    public void setIdTutorial(String idTutorial) {
        this.idTutorial = idTutorial;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
