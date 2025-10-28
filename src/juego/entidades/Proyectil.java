package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import java.awt.Image;

/**
 * Representa una bola de fuego disparada por una planta
 * Se mueve de izquierda a derecha
 */
public class Proyectil {
    
    private ObjetoDeJuego base;
    
    private static final double ANCHO = 20;
    private static final double ALTO = 20;
    private static final double VELOCIDAD = 5.0; // Píxeles por tick
    private static final int DANO = 50; // 2 disparos para matar un zombie (asumiendo 100 de vida)


     //Crea un proyectil en la posición (x, y) con la imagen dada.
    public Proyectil(double x, double y, Image imagen) {
        // Usa las constantes para el tamaño
        this.base = new ObjetoDeJuego(x, y, ANCHO, ALTO, imagen);
    }

    /**
     * Método de actualización (llamado por Tablero)
     * Mueve el proyectil hacia la derecha
     */
    public void actualizar() {
        this.base.mover(VELOCIDAD, 0); 
    }

    /**
     * Dibuja el proyectil en pantalla.
     */
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