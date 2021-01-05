package com.example.gonavigator.models;

public class Pasos {

    private int id_ruta;
    private int numero;

    private String instruccion;
    private String polilinea;

    //CONSTRUCTOR
    public  Pasos()
    {
    }

    public int getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(int id_ruta) {
        this.id_ruta = id_ruta;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }

    public String getPolilinea() {
        return polilinea;
    }

    public void setPolilinea(String polilinea) {
        this.polilinea = polilinea;
    }
}
