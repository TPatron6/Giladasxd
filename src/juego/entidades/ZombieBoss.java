package juego.entidades;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import java.awt.Image;
import java.awt.Color;
import juego.planta.RoseBlade;
import juego.planta.WallNut;

public class ZombieBoss {

    private ObjetoDeJuego base;
    private int vida;
    private double velocidad;
    private int ataque;
    private boolean estaColisionandoConPlanta; // PRIVADO
    private double tiempoHastaAtaque;
    private static final double COOLDOWN_ATAQUE = 1.0;

    private static final double ANCHO = 200;
    private static final double ALTO = 500;
    private static final int VIDA_INICIAL = 5000;
    private static final double VELOCIDAD_INICIAL = 0.4;
    private static final int ATAQUE_INICIAL = 75;
    private static final double Y_OFFSET_DIBUJO = -50;

    public ZombieBoss(double x, Image imagen) {
        double ySpawn = 100 + (ALTO / 2);
        this.base = new ObjetoDeJuego(x, ySpawn, ANCHO, ALTO, imagen);
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
        Image img = this.base.getImagen();
        if (img != null) {
            int imgHeight = img.getHeight(null);
            double escala = 1.0;
            if (imgHeight > 0) { escala = ALTO / (double)imgHeight; }
            double yCentroDibujo = this.base.getY() + Y_OFFSET_DIBUJO;
            e.dibujarImagen(img, this.base.getX(), yCentroDibujo, 0, escala);
        }

        if (this.vida < VIDA_INICIAL){
            double yBarra = (this.base.getY() + Y_OFFSET_DIBUJO) - (ALTO / 2) + 15;
            e.dibujarRectangulo(this.base.getX(), yBarra, ANCHO, 10, 0, Color.RED);
            double porcentajeVida = this.vida / (double)VIDA_INICIAL;
            double anchoVida = ANCHO * porcentajeVida;
            double xBarraVida = this.base.getX() - (ANCHO / 2) + (anchoVida / 2);
            e.dibujarRectangulo(xBarraVida, yBarra, anchoVida, 10, 0, Color.GREEN);
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