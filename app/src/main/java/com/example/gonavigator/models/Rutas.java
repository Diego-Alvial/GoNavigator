package com.example.gonavigator.models;

public class Rutas {

    private int id_ruta;

    private String nombe_ruta;
    private String distancia;
    private String tiempo;

    // constructor
    public Rutas()
    {
    }

    public int getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(int id_ruta) {
        this.id_ruta = id_ruta;
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
