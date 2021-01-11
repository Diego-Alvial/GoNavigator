package com.example.gonavigator.models;

public class Direcciones
{
    private String nombre_dir;
    private String ciudad_dir;
    private double latitud;
    private double longitud;

    // Constructor
    public Direcciones()
    {
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
}


