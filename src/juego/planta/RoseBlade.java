package juego.planta;

import java.awt.Image;
import juego.entidades.Proyectil;
import juego.entidades.Zombie;
import juego.ObjetoDeJuego; 
import juego.Punto;        
import entorno.Entorno;    

public class RoseBlade {

    // Composicion
    private ObjetoDeJuego base;

    // Propiedades
    private int vida;
    private double tiempoCargaRestante; //Para el prototipo de la UI

    //Constantes y propiedades de ataque 
    public static final double ANCHO_ROSEBLADE = 65; 
    public static final double ALTO_ROSEBLADE = 65;
    private static final int VIDA_ROSEBLADE = 100;
    private static final double COOLDOWN_DISPARO = 2.0;
    private static final double RANGO_ATAQUE = 800;
    private double tiempoHastaDisparo;
    private boolean veUnZombie;

    public RoseBlade(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO_ROSEBLADE, ALTO_ROSEBLADE, imagen);
        this.vida = VIDA_ROSEBLADE;
        this.tiempoCargaRestante = 0;
        this.tiempoHastaDisparo = COOLDOWN_DISPARO;
        this.veUnZombie = false;
        // System.out.println("-> Creando RoseBlade (Composición) con ANCHO=" + ANCHO_ROSEBLADE);
    }

    //Metodos

    // La lógica principal de actualización
    public void actualizar(Zombie[] zombies) {
        this.actualizarCooldown(); // Actualiza cooldown de carta
        if (this.tiempoHastaDisparo > 0) { this.tiempoHastaDisparo -= 1.0 / 60.0; } // Actualiza cooldown disparo
        // Buscar un objetivo
        this.veUnZombie = false;
        for (int i = 0; i < zombies.length; i++) {
            if (zombies[i] != null) {
                boolean mismaFila = Math.abs(zombies[i].getY() - this.getY()) < 10;
                boolean estaDelante = zombies[i].getX() > this.getX();
                boolean enRango = zombies[i].getX() < (this.getX() + RANGO_ATAQUE);
                if (mismaFila && estaDelante && enRango) { this.veUnZombie = true; break; }
            }
        }
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
    
    //Metodos posicion y mover
    public void setPosicion(double x, double y) { this.base.setPosicion(x, y); }
    public void mover(double dx, double dy) { this.base.mover(dx, dy); }

    //Metodos cooldown carta
    public double getTiempoCargaRestante() { return this.tiempoCargaRestante; }
    public void setTiempoCarga(double tiempo) { this.tiempoCargaRestante = tiempo; }
    public void actualizarCooldown() {
        if (this.tiempoCargaRestante > 0) { this.tiempoCargaRestante -= 1.0 / 60.0; if (this.tiempoCargaRestante < 0) this.tiempoCargaRestante = 0; }
    }

    //Metodos especificos de RoseBlade
    public boolean puedeDisparar() { return this.tiempoHastaDisparo <= 0 && this.veUnZombie; }
    public Proyectil disparar(Image imgProyectil) {
        if (puedeDisparar()) { this.tiempoHastaDisparo = COOLDOWN_DISPARO; return new Proyectil(this.getX() + this.getAncho() / 2, this.getY(), imgProyectil); }
        return null;
    }
}