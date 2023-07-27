package com.example.safetravelmap.entities;

public class Mapa {
    private String url;

    public Mapa(String url) {
        this.url = url;
    }

    public Mapa() {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean cargarMapa(String apiKey) {
        if(apiKey != null)
            return true;
        else return false;
    }
}
