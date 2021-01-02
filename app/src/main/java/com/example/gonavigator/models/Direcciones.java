package com.example.gonavigator.models;

public class Direcciones
{

    private int nombre;
    private int direccion;

    private String nombre_dir;
    private String cordenada;

    // Constructor
    public Direcciones()
    {
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public int getDireccion() {
        return direccion;
    }

    public void setDireccion(int direccion) {
        this.direccion = direccion;
    }

    public String getNombre_dir() {
        return nombre_dir;
    }

    public void setNombre_dir(String nombre_dir) {
        this.nombre_dir = nombre_dir;
    }

    public String getCordenada() {
        return cordenada;
    }

    public void setCordenada(String cordenada) {
        this.cordenada = cordenada;
    }
}


