package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import java.awt.Image;
import java.awt.Color;

public class Zombie {

    private ObjetoDeJuego base;
    private int vida;
    private double velocidad;
    private int ataque;
    private boolean estaColisionandoConPlanta;
    private double tiempoHastaAtaque;
    private static final double COOLDOWN_ATAQUE = 1.5;

    private static final double ANCHO = 65;
    private static final double ALTO = 65;  
    
    private static final int VIDA_INICIAL = 100;

    public Zombie(double x, double y, Image imagen, double velocidad) {
        // Pasa el nuevo tamaño al constructor de ObjetoDeJuego
        this.base = new ObjetoDeJuego(x, y, ANCHO, ALTO, imagen);

        // log ancho, borrar luego
        System.out.println("-> Creando Zombie con ANCHO=" + ANCHO);

        this.vida = VIDA_INICIAL;
        this.velocidad = velocidad;
        this.ataque = 25;
        this.estaColisionandoConPlanta = false;
        this.tiempoHastaAtaque = 0;
    }

    public void actualizar() {
        // El reset se hace en Tablero.gestionarColisiones
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
        // Barra de vida (creo que no funciona bien)
        if (this.vida > 0 && this.vida < VIDA_INICIAL) { // Solo mostrar si está dañado
             e.dibujarRectangulo(this.base.getX(), this.base.getY() - (ALTO / 2) - 5,
                                 ANCHO * (this.vida / (double)VIDA_INICIAL), 5, 0, Color.GREEN);
        }
    }

    //Getters y logica de vida
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
}