package com.example.laboratorio6_20201696;

public class Pieza {
    private int numero; // Número identificador de la pieza
    private int fila;   // Fila actual en la que se encuentra la pieza
    private int columna; // Columna actual en la que se encuentra la pieza
    private String imagen; // Nombre de la imagen asociada a la pieza

    // Constructor
    public Pieza(int numero, int fila, int columna, String imagen) {
        this.numero = numero;
        this.fila = fila;
        this.columna = columna;
        this.imagen = imagen;
    }

    // Getters y setters
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}

