package com.example.gonavigator.models;

public class Rutas {

    private String nombe_ruta;
    private String distancia;
    private String tiempo;

    // constructor
    public Rutas()
    {
    }

    public String getNombe_Ruta() {
        return nombe_ruta;
    }

    public void setNombe_Ruta(String nombe_Ruta) {
        nombe_ruta = nombe_Ruta;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        distancia = distancia;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        tiempo = tiempo;
    }
}
