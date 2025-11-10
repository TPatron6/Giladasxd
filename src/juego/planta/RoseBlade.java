package juego.planta;

import java.awt.Image;
import java.awt.Color; 
import juego.entidades.Proyectil;
import juego.entidades.Zombie; 
import juego.entidades.ZombieFast;
import juego.entidades.ZombieSlow;
import juego.entidades.ZombieShooter;
import juego.entidades.ZombieBoss;
import juego.ObjetoDeJuego; 
import juego.Punto;        
import entorno.Entorno;    

public class RoseBlade {

    // Composicion
    private ObjetoDeJuego base;

    // Propiedades
    private int vida;
    private double tiempoCargaRestante; 

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
    }


    //MÉTODO ACTUALIZAR 
    public void actualizar(Zombie[] zombies, ZombieFast[] zombiesFast, ZombieSlow[] zombiesSlow, ZombieShooter[] zombiesShooter, ZombieBoss[] zombiesBoss) {
        this.actualizarCooldown(); 
        if (this.tiempoHastaDisparo > 0) { this.tiempoHastaDisparo -= 1.0 / 60.0; } 
        
        // Buscar un objetivo
        this.veUnZombie = false;
        
        // Buscar en Zombies comunes
        for (int i = 0; i < zombies.length; i++) {
            if (zombies[i] != null) {
                if (estaEnRango(zombies[i].getX(), zombies[i].getY())) { this.veUnZombie = true; break; }
            }
        }
        
        // Buscar en Zombies rápidos
        if (!this.veUnZombie) {
            for (int i = 0; i < zombiesFast.length; i++) {
                if (zombiesFast[i] != null) {
                    if (estaEnRango(zombiesFast[i].getX(), zombiesFast[i].getY())) { this.veUnZombie = true; break; }
                }
            }
        }
        
        // Buscar en Zombies lentos
        if (!this.veUnZombie) {
            for (int i = 0; i < zombiesSlow.length; i++) {
                if (zombiesSlow[i] != null) {
                    if (estaEnRango(zombiesSlow[i].getX(), zombiesSlow[i].getY())) { this.veUnZombie = true; break; }
                }
            }
        }

        // Buscar en Zombies shooters
        if (!this.veUnZombie) {
            for (int i = 0; i < zombiesShooter.length; i++) {
                if (zombiesShooter[i] != null) {
                    if (estaEnRango(zombiesShooter[i].getX(), zombiesShooter[i].getY())) { this.veUnZombie = true; break; }
                }
            }
        }
        
        // Buscar en Zombies jefes (LÓGICA ESPECIAL)
        if (!this.veUnZombie) {
            for (int i = 0; i < zombiesBoss.length; i++) {
                if (zombiesBoss[i] != null) {
                    
                    double bossX = zombiesBoss[i].getX();
                    
                    // El jefe ignora el chequeo de "mismaFila".
                    // Todas las plantas lo detectan si está en rango horizontal.
                    boolean estaDelante = bossX > this.getX();
                    boolean enRangoHorizontal = bossX < (this.getX() + RANGO_ATAQUE);
                    
                    if (estaDelante && enRangoHorizontal) { 
                        this.veUnZombie = true; 
                        break; 
                    }
                }
            }
        }
    }
    
    // Método de ayuda para zombies normales
    private boolean estaEnRango(double zX, double zY) {
        boolean mismaFila = Math.abs(zY - this.getY()) < 10; 
        boolean estaDelante = zX > this.getX();
        boolean enRango = zX < (this.getX() + RANGO_ATAQUE);
        return mismaFila && estaDelante && enRango;
    }

    public void dibujar(Entorno e) { 
        this.base.dibujar(e); 
        
        if (this.vida < VIDA_ROSEBLADE){ 
        	e.dibujarRectangulo(this.base.getX(), this.base.getY() + (ALTO_ROSEBLADE / 2) - 5,
                    			 ANCHO_ROSEBLADE, 5, 0, Color.RED);
        	double porcentajeVida = this.vida / (double)VIDA_ROSEBLADE;
        	double anchoVida = ANCHO_ROSEBLADE * porcentajeVida;
        	double xBarraVida = this.base.getX() - (ANCHO_ROSEBLADE / 2) + (anchoVida / 2);
        	e.dibujarRectangulo(xBarraVida, this.base.getY() + (ALTO_ROSEBLADE / 2) - 5, anchoVida, 5, 0, Color.GREEN);
        }
    }
    
    public void recibirDano(int dano) { this.vida -= dano; }
    public boolean estaMuerta() { return this.vida <= 0; }
    public int getVida() { return this.vida; }
    public double getX() { return this.base.getX(); }
    public double getY() { return this.base.getY(); }
    public double getAncho() { return this.base.getAncho(); }
    public double getAlto() { return this.base.getAlto(); }
    public Punto getPosicion() { return this.base.getPosicion(); }
    public void setPosicion(double x, double y) { this.base.setPosicion(x, y); }
    public void mover(double dx, double dy) { this.base.mover(dx, dy); }
    public double getTiempoCargaRestante() { return this.tiempoCargaRestante; }
    public void setTiempoCarga(double tiempo) { this.tiempoCargaRestante = tiempo; }
    public void actualizarCooldown() {
        if (this.tiempoCargaRestante > 0) { this.tiempoCargaRestante -= 1.0 / 60.0; if (this.tiempoCargaRestante < 0) this.tiempoCargaRestante = 0; }
    }
    public boolean puedeDisparar() { return this.tiempoHastaDisparo <= 0 && this.veUnZombie; }
    public Proyectil disparar(Image imgProyectil) {
        if (puedeDisparar()) { this.tiempoHastaDisparo = COOLDOWN_DISPARO; return new Proyectil(this.getX() + this.getAncho() / 2, this.getY(), imgProyectil); }
        return null;
    }
}