package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import java.awt.Image;
import java.awt.Color; 

public class Regalo {
    
    private ObjetoDeJuego base; 
    
    private static final double ANCHO = 40; 
    private static final double ALTO = 40;
    
    public Regalo(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO, ALTO, imagen);
    }

    
     //Dibuja el regalo.
    public void dibujar(Entorno e) {
        // Delega el dibujo al ObjetoDeJuego 'base', 
        // que ya tiene la imagen y sabe cómo dibujarse
        if (this.base != null) {
            this.base.dibujar(e); 
        }
    }
    
    //Getters

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