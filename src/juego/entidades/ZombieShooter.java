package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import java.awt.Image;
import java.awt.Color;
import juego.planta.RoseBlade;
import juego.planta.WallNut;

public class ZombieShooter {

    private ObjetoDeJuego base;
    private int vida;
    private double velocidad;
    private int ataque;
    private boolean estaColisionandoConPlanta;
    private double tiempoHastaAtaque;
    private static final double COOLDOWN_ATAQUE = 1.5;

    private static final double ANCHO = 100;
    private static final double ALTO = 100;
    private static final int VIDA_INICIAL = 120;
    private static final double VELOCIDAD_INICIAL = 0.8;
    private static final int ATAQUE_INICIAL = 25;

    private double tiempoHastaDisparo;
    private boolean estaParadoParaDisparar; // PRIVADO
    private double tiempoParado;
    private boolean veUnaPlanta;

    private static final double COOLDOWN_DISPARO = 4.0;
    private static final double TIEMPO_DE_PARADA = 1.0;
    private static final double RANGO_ATAQUE = 500;

    public ZombieShooter(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO, ALTO, imagen);
        this.vida = VIDA_INICIAL;
        this.velocidad = VELOCIDAD_INICIAL;
        this.ataque = ATAQUE_INICIAL;
        this.estaColisionandoConPlanta = false;
        this.tiempoHastaAtaque = 0;

        this.tiempoHastaDisparo = COOLDOWN_DISPARO;
        this.estaParadoParaDisparar = false;
        this.tiempoParado = 0;
        this.veUnaPlanta = false;
    }

    public void actualizar(RoseBlade[] roseBlades, WallNut[] wallNuts) {
        // Cooldown ataque melee
        if (this.tiempoHastaAtaque > 0) { this.tiempoHastaAtaque -= 1.0 / 60.0; }

        // Buscar planta
        this.veUnaPlanta = false;
        for (int i = 0; i < roseBlades.length; i++) { if (roseBlades[i] != null && estaEnRangoDisparo(roseBlades[i].getX(), roseBlades[i].getY())) { this.veUnaPlanta = true; break; } }
        if (!this.veUnaPlanta) { for (int i = 0; i < wallNuts.length; i++) { if (wallNuts[i] != null && estaEnRangoDisparo(wallNuts[i].getX(), wallNuts[i].getY())) { this.veUnaPlanta = true; break; } } }

        // Cooldown disparo
        if (this.tiempoHastaDisparo > 0) { this.tiempoHastaDisparo -= 1.0 / 60.0; }

        // Lógica de estado (parar / disparar)
        if (this.veUnaPlanta && !this.estaParadoParaDisparar && this.tiempoHastaDisparo <= 0) {
            this.estaParadoParaDisparar = true;
            this.tiempoParado = TIEMPO_DE_PARADA;
        }
        if (this.estaParadoParaDisparar) {
            this.tiempoParado -= 1.0 / 60.0;
            if (!this.veUnaPlanta) { // Cancela si deja de ver
                this.estaParadoParaDisparar = false;
            }
        }
    }

    // Helper para buscar planta a disparar
    private boolean estaEnRangoDisparo(double pX, double pY) {
        boolean mismaFila = Math.abs(pY - this.getY()) < 10;
        boolean estaDetras = pX < this.getX(); // Planta está a la izquierda
        boolean enRango = this.getX() - pX < RANGO_ATAQUE; // Dentro del rango de visión
        return mismaFila && estaDetras && enRango;
    }

    public void intentarMover() {
        if (!this.estaColisionandoConPlanta && !this.estaParadoParaDisparar) { // No se mueve si está colisionando O parado
             this.base.mover(-this.velocidad, 0);
        }
    }

    public boolean puedeAtacar() { return this.tiempoHastaAtaque <= 0; }
    public void reiniciarCooldownAtaque() { this.tiempoHastaAtaque = COOLDOWN_ATAQUE; }
    public void setEstaColisionando(boolean colisionando) { this.estaColisionandoConPlanta = colisionando; }

    public boolean puedeDisparar() { return this.estaParadoParaDisparar && this.tiempoParado <= 0; }
    public ProyectilZombie disparar(Image imgProyectil) {
        this.estaParadoParaDisparar = false;
        this.tiempoHastaDisparo = COOLDOWN_DISPARO;
        return new ProyectilZombie(this.getX() - this.getAncho() / 2, this.getY(), imgProyectil);
    }

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
    public boolean estaColisionandoConPlanta() {
        return this.estaColisionandoConPlanta;
    }
    public boolean estaParadoParaDisparar() {
        return this.estaParadoParaDisparar;
    }
}