package juego.entidades;

import java.awt.Image;
import java.awt.Color;
import juego.ObjetoDeJuego;
import entorno.Entorno;

/**
 * Representa una tumba dejada por un zombie.
 * Bloquea proyectiles y zombies. Tiene vida.
 */
public class Tumba {

    private ObjetoDeJuego base;
    private int vida;

    // --- Constantes ---
    public static final double ANCHO_TUMBA = 60;
    public static final double ALTO_TUMBA = 80;
    private static final int VIDA_INICIAL = 300; // Vida de la tumba

    public Tumba(double x, double y, Image imagen) {
        this.base = new ObjetoDeJuego(x, y, ANCHO_TUMBA, ALTO_TUMBA, imagen);
        this.vida = VIDA_INICIAL;
    }

    public void dibujar(Entorno e) {
        this.base.dibujar(e);

        // Barra de vida (opcional, si quieres verla)
        if (this.vida < VIDA_INICIAL) {
            double yBarra = this.base.getY() + (ALTO_TUMBA / 2) - 5;
            e.dibujarRectangulo(this.base.getX(), yBarra, ANCHO_TUMBA, 5, 0, Color.DARK_GRAY);
            double porcentajeVida = this.vida / (double) VIDA_INICIAL;
            double anchoVida = ANCHO_TUMBA * porcentajeVida;
            double xBarraVida = this.base.getX() - (ANCHO_TUMBA / 2) + (anchoVida / 2);
            e.dibujarRectangulo(xBarraVida, yBarra, anchoVida, 5, 0, Color.LIGHT_GRAY);
        }
    }

    public void recibirDano(int dano) {
        this.vida -= dano;
    }

    public boolean estaDestruida() {
        return this.vida <= 0;
    }

    // Getters necesarios para Tablero
    public double getX() { return this.base.getX(); }
    public double getY() { return this.base.getY(); }
    public double getAncho() { return this.base.getAncho(); }
    public double getAlto() { return this.base.getAlto(); }
    public juego.Punto getPosicion() { return this.base.getPosicion(); } // Asegúrate de importar Punto si es necesario
}