package juego.planta;

import java.awt.Image;
import juego.entidades.Zombie;
import juego.ObjetoDeJuego; 
import juego.Punto;        
import entorno.Entorno;    

public class PlantaExplosiva {

    //Composición
    private ObjetoDeJuego base;

    // Propiedades
    private int vida;
    private double tiempoCargaRestante; //Para la UI

    // Constantes 
    public static final double ANCHO_EXPLOSIVA = 70; 
    public static final double ALTO_EXPLOSIVA = 70;
    // Le damos vida baja para que muera con cualquier toque
    private static final int VIDA_INICIAL = 10; 


    public PlantaExplosiva(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO_EXPLOSIVA, ALTO_EXPLOSIVA, imagen);
        this.vida = VIDA_INICIAL;
        this.tiempoCargaRestante = 0;
    }

    //Metodos

    // Esta planta no hace nada, solo espera
    public void actualizar(Zombie[] zombies) {
        this.actualizarCooldown(); // Solo actualiza cooldown de carta
    }

    public void dibujar(Entorno e) { 
        this.base.dibujar(e); 
        
        // No necesita barra de vida, ya que explota al primer toque
    }
    
    
     //Al recibir CUALQUIER daño, se considera muerta para explotar.

    public void recibirDano(int dano) { 
        if (dano > 0) {
            this.vida = 0; 
        }
    }
    
    public boolean estaMuerta() { return this.vida <= 0; }
    public int getVida() { return this.vida; }

    //Getters (no puro?)
    public double getX() { return this.base.getX(); }
    public double getY() { return this.base.getY(); }
    public double getAncho() { return this.base.getAncho(); }
    public double getAlto() { return this.base.getAlto(); }
    public Punto getPosicion() { return this.base.getPosicion(); }
    public void setPosicion(double x, double y) { this.base.setPosicion(x, y); }
    public void mover(double dx, double dy) { this.base.mover(dx, dy); }

    //Metodos cooldown carta 
    public double getTiempoCargaRestante() { return this.tiempoCargaRestante; }
    public void setTiempoCarga(double tiempo) { this.tiempoCargaRestante = tiempo; }
    public void actualizarCooldown() {
        if (this.tiempoCargaRestante > 0) { this.tiempoCargaRestante -= 1.0 / 60.0; if (this.tiempoCargaRestante < 0) this.tiempoCargaRestante = 0; }
    }
}