package juego.planta;

import entorno.Entorno;
import juego.ObjetoDeJuego;
import juego.Punto;
import juego.entidades.Zombie; // Importa Zombie
import java.awt.Image;

public class Planta {
    
    public ObjetoDeJuego base; 
    public int vida;           
    private double tiempoCargaRestante; // Cooldown de la carta

    public Planta(double x, double y, double ancho, double alto, Image imagen, int vida) {
        this.base = new ObjetoDeJuego(x, y, ancho, alto, imagen);
        this.vida = vida;
        this.tiempoCargaRestante = 0;
    }

    // llama al Tablero en cada tick, implemente el cooldown de cartars y sobreescribe 
    // informacion de las cartas si es necesario por alguna condicion logica
    public void actualizar() {
        this.actualizarCooldown(); 
    }

    public void actualizar(Zombie[] zombies) {
        this.actualizar();
    }

    // Metodos de posicion y dibujo 
    public void dibujar(Entorno e) { this.base.dibujar(e); }
    public ObjetoDeJuego getBase() { return this.base; }
    public Punto getPosicion() { return this.base.getPosicion(); }
    public double getX() { return this.base.getX(); }
    public double getY() { return this.base.getY(); }
    public void mover(double dx, double dy) { this.base.mover(dx, dy); }
    public double getAncho() { return this.base.getAncho(); }
    public double getAlto() { return this.base.getAlto(); }
    
    // Logica de vida y recibir dano
    public int getVida() { return this.vida; }
    public void recibirDano(int dano) { this.vida -= dano; }
    public boolean estaMuerta() { return this.vida <= 0; }
    
    // Logica del cooldown de carta
    public double getTiempoCargaRestante() { return this.tiempoCargaRestante; }
    public void setTiempoCarga(double tiempo) { this.tiempoCargaRestante = tiempo; }
    public void actualizarCooldown() {
        if (this.tiempoCargaRestante > 0) {
            this.tiempoCargaRestante -= 1.0 / 60.0; 
            if (this.tiempoCargaRestante < 0) this.tiempoCargaRestante = 0;
        }
    }
}