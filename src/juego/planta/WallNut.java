package juego.planta;

import java.awt.Image;
import juego.entidades.Zombie;
import juego.ObjetoDeJuego; 
import juego.Punto;        
import entorno.Entorno;    


public class WallNut {

    //Composición
    private ObjetoDeJuego base;

    // Propiedades
    private int vida;
    private double tiempoCargaRestante; //Para el prototipo de la UI

    // --- Constantes ---
    public static final double ANCHO_WALLNUT = 65; // 'public static'
    public static final double ALTO_WALLNUT = 65;
    private static final int VIDA_WALLNUT = 1000;


    public WallNut(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO_WALLNUT, ALTO_WALLNUT, imagen);
        this.vida = VIDA_WALLNUT;
        this.tiempoCargaRestante = 0;
        // System.out.println("-> Creando WallNut (Composición) con ANCHO=" + ANCHO_WALLNUT);
    }

    //Metodos

    // WallNut no necesita interactuar activamente con zombies en su update
    public void actualizar(Zombie[] zombies) {
        this.actualizarCooldown(); // Solo actualiza cooldown de carta
    }

    public void dibujar(Entorno e) { this.base.dibujar(e); }
    public void recibirDano(int dano) { this.vida -= dano; }
    public boolean estaMuerta() { return this.vida <= 0; }
    public int getVida() { return this.vida; }

    //Metodos delegados
    public double getX() { return this.base.getX(); }
    public double getY() { return this.base.getY(); }
    public double getAncho() { return this.base.getAncho(); }
    public double getAlto() { return this.base.getAlto(); }
    public Punto getPosicion() { return this.base.getPosicion(); }
    //Metodos necesarios que antes venian de la interfaz
    public void setPosicion(double x, double y) { this.base.setPosicion(x, y); }
    public void mover(double dx, double dy) { this.base.mover(dx, dy); }

    //Metodos cooldown carta 
    public double getTiempoCargaRestante() { return this.tiempoCargaRestante; }
    public void setTiempoCarga(double tiempo) { this.tiempoCargaRestante = tiempo; }
    public void actualizarCooldown() {
        if (this.tiempoCargaRestante > 0) { this.tiempoCargaRestante -= 1.0 / 60.0; if (this.tiempoCargaRestante < 0) this.tiempoCargaRestante = 0; }
    }
}