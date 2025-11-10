package juego.planta;

import java.awt.Image;
import java.awt.Color; // <-- AÑADIDO
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

    //Constantes
    public static final double ANCHO_WALLNUT = 65; // 
    public static final double ALTO_WALLNUT = 65;
    private static final int VIDA_WALLNUT = 1000; // Vida inicial


    public WallNut(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO_WALLNUT, ALTO_WALLNUT, imagen);
        this.vida = VIDA_WALLNUT;
        this.tiempoCargaRestante = 0;
    }



    // WallNut no necesita interactuar activamente con zombies
    public void actualizar(Zombie[] zombies) {
        this.actualizarCooldown(); // Solo actualiza cooldown de carta
    }

    //MÉTODO DIBUJAR
    public void dibujar(Entorno e) { 
        this.base.dibujar(e); 
        
        // Barra de vida (solo si está dañada)
        if (this.vida < VIDA_WALLNUT){ 
            // Fondo rojo
        	e.dibujarRectangulo(this.base.getX(), this.base.getY() + (ALTO_WALLNUT / 2) - 5,
                    			 ANCHO_WALLNUT, 5, 0, Color.RED);
            // Vida verde
        	double porcentajeVida = this.vida / (double)VIDA_WALLNUT;
        	double anchoVida = ANCHO_WALLNUT * porcentajeVida;
        	double xBarraVida = this.base.getX() - (ANCHO_WALLNUT / 2) + (anchoVida / 2);
        	
        	e.dibujarRectangulo(xBarraVida, this.base.getY() + (ALTO_WALLNUT / 2) - 5, anchoVida, 5, 0, Color.GREEN);
        }
    }
    
    public void recibirDano(int dano) { this.vida -= dano; }
    public boolean estaMuerta() { return this.vida <= 0; }
    public int getVida() { return this.vida; }

    //Getters (no puros?)
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