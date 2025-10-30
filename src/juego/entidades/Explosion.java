package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import java.awt.Image;

public class Explosion {
    
    private ObjetoDeJuego base;
    private double tiempoRestante;

    public static final double ANCHO_EXPLOSION = 150; 
    public static final double ALTO_EXPLOSION = 150;
    public static final double DURACION_ANIMACION = 0.5; 
    
    public Explosion(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO_EXPLOSION, ALTO_EXPLOSION, imagen);
        this.tiempoRestante = DURACION_ANIMACION;
    }

    public void actualizar() {
        if (this.tiempoRestante > 0) {
            this.tiempoRestante -= 1.0 / 60.0; 
        }
    }

    public void dibujar(Entorno e) {
        this.base.dibujar(e);
    }
    
    public boolean haTerminado() {
        return this.tiempoRestante <= 0;
    }

    public double getX() { return this.base.getX(); }
    public double getY() { return this.base.getY(); }
}