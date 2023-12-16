package com.example.examenfinalpm1.Model;

import java.util.Date;

public class Entrevista {
    private String id, nombre;
    private String descripcion;
    private String periodista;
    private String fecha;
    private String imagenUrl; // URL de la imagen en Firebase Storage
    private String audioUrl; // URL del audio en Firebase Storage

    public Entrevista() {
    }

    @Override
    public String toString() {
        return  "Entrevista : " + nombre + '\n'+
                "Descripcion : " + descripcion + '\n'+
                "Periodista : " + periodista + '\n'+
                "Fecha : " + fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPeriodista() {
        return periodista;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Entrevista(String id, String nombre, String descripcion, String periodista, String fecha, String imagenUrl, String audioUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.periodista = periodista;
        this.fecha = fecha;
        this.imagenUrl = imagenUrl;
        this.audioUrl = audioUrl;
    }
}
