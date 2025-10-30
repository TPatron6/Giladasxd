package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import java.awt.Image;

/**
 * Representa un proyectil disparado por un Zombie
 * Se mueve de derecha a izquierda
 */
public class ProyectilZombie {
    
    private ObjetoDeJuego base;
    
    private static final double ANCHO = 40;
    private static final double ALTO = 40;
    private static final double VELOCIDAD = -4.0; // Píxeles por tick (NEGATIVA)
    private static final int DANO = 25; // Mismo daño que un ataque melee

    public ProyectilZombie(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO, ALTO, imagen);
    }

    public void actualizar() {
        this.base.mover(VELOCIDAD, 0); 
    }

    public void dibujar(Entorno e) {
        this.base.dibujar(e);
    }

    // Getters 
    public int getDano() { return DANO; }
    public Punto getPosicion() { return this.base.getPosicion(); }
    public double getX() { return this.base.getX(); }
    public double getY() { return this.base.getY(); }
    public double getAncho() { return this.base.getAncho(); }
    public double getAlto() { return this.base.getAlto(); }
}
