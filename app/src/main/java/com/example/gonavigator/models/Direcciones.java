package com.example.gonavigator.models;

public class Direcciones
{

    private int id_ruta;
    private String nombre_dir;
    private String ciudad_dir;
    private double latitud;
    private double longitud;
    private boolean inicial;

    // Constructor
    public Direcciones()
    {
    }

    public int getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(int id_ruta) {
        this.id_ruta = id_ruta;
    }

    public String getNombre_dir() {
        return nombre_dir;
    }

    public void setNombre_dir(String nombre_dir) {
        this.nombre_dir = nombre_dir;
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

    public String getCiudad_dir() {
        return ciudad_dir;
    }

    public void setCiudad_dir(String ciudad_dir) {
        this.ciudad_dir = ciudad_dir;
    }

    public boolean isInicial() {
        return inicial;
    }

    public void setInicial(boolean inicial) {
        this.inicial = inicial;
    }
}


