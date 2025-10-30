package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import java.awt.Image;
import java.awt.Color;
import juego.planta.RoseBlade;
import juego.planta.WallNut;

public class ZombieFast {

    private ObjetoDeJuego base;
    private int vida;
    private double velocidad;
    private int ataque;
    private boolean estaColisionandoConPlanta; // PRIVADO
    private double tiempoHastaAtaque;
    private static final double COOLDOWN_ATAQUE = 1.5;

    private static final double ANCHO = 100;
    private static final double ALTO = 100;
    private static final int VIDA_INICIAL = 100;
    private static final double VELOCIDAD_INICIAL = 2.0;
    private static final int ATAQUE_INICIAL = 20;

    public ZombieFast(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO, ALTO, imagen);
        this.vida = VIDA_INICIAL;
        this.velocidad = VELOCIDAD_INICIAL;
        this.ataque = ATAQUE_INICIAL;
        this.estaColisionandoConPlanta = false;
        this.tiempoHastaAtaque = 0;
    }

    public void actualizar(RoseBlade[] roseBlades, WallNut[] wallNuts) {
        if (this.tiempoHastaAtaque > 0) {
            this.tiempoHastaAtaque -= 1.0 / 60.0;
        }
    }

    public void intentarMover() {
        if (!this.estaColisionandoConPlanta) {
             this.base.mover(-this.velocidad, 0);
        }
    }

    public boolean puedeAtacar() { return this.tiempoHastaAtaque <= 0; }
    public void reiniciarCooldownAtaque() { this.tiempoHastaAtaque = COOLDOWN_ATAQUE; }
    public void setEstaColisionando(boolean colisionando) { this.estaColisionandoConPlanta = colisionando; }

    public void dibujar(Entorno e) {
        this.base.dibujar(e);
        if (this.vida < VIDA_INICIAL){
        	e.dibujarRectangulo(this.base.getX(), this.base.getY() - (ALTO / 2) + 5, ANCHO, 5, 0, Color.RED);
        	double porcentajeVida = this.vida / (double)VIDA_INICIAL;
        	double anchoVida = ANCHO * porcentajeVida;
        	double xBarraVida = this.base.getX() - (ANCHO / 2) + (anchoVida / 2);
        	e.dibujarRectangulo(xBarraVida, this.base.getY() - (ALTO / 2) + 5, anchoVida, 5, 0, Color.GREEN);
        }
    }

    public int getVida() { return this.vida; }
    public void recibirDano(int dano) { this.vida -= dano; }
    public boolean estaMuerto() { return this.vida <= 0; }
    public Punto getPosicion() { return this.base.getPosicion(); }
    public double getX() { return this.base.getX(); }
    public double getY() { return this.base.getY(); }
    public double getAncho() { return this.base.getAncho(); }
    public double getAlto() { return this.base.getAlto(); }
    public int getAtaque() { return this.ataque; }
    public double getVelocidad() { return this.velocidad; }

    // --- NUEVO GETTER PÚBLICO ---
    public boolean estaColisionandoConPlanta() {
        return this.estaColisionandoConPlanta;
    }
    // ----------------------------
}