package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import java.awt.Image;


public class Proyectil {
    
    private ObjetoDeJuego base;
    
    private static final double ANCHO = 50;
    private static final double ALTO = 50;
    private static final double VELOCIDAD = 5.0; // Píxeles por tick
    private static final int DANO = 50;


     //Crea un proyectil en la posición (x, y) con la imagen dada.
    public Proyectil(double x, double y, Image imagen) {
        // Usa las constantes para el tamaño
        this.base = new ObjetoDeJuego(x, y, ANCHO, ALTO, imagen);
    }


     //Método de actualización (llamado por Tablero)
     //Mueve el proyectil hacia la derecha
    public void actualizar() {
        this.base.mover(VELOCIDAD, 0); 
    }

    public void dibujar(Entorno e) {
        this.base.dibujar(e);
    }

    // Getters 

    public int getDano() {
        return DANO;
    }
    
    public Punto getPosicion() {
        return this.base.getPosicion();
    }
    
    public double getX() {
        return this.base.getX();
    }
    
    public double getY() {
        return this.base.getY();
    }
    
    public double getAncho() {
        return this.base.getAncho();
    }
    
    public double getAlto() {
        return this.base.getAlto();
    }
}