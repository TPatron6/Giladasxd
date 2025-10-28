package juego;

import entorno.Entorno;
import java.awt.Image;

public class ObjetoDeJuego {

    private Punto posicion;
    private double ancho; //para colisiones
    private double alto;  //para colisiones
    private Image imagen;
    private double escala; //usado para dibujar

    public ObjetoDeJuego(double x, double y, double ancho, double alto, Image imagen) {
        this.posicion = new Punto(x, y);
        this.ancho = ancho; 
        this.alto = alto;
        this.imagen = imagen;

       
        //Calcular escala basado en el ancho deseado y el ancho original de la imagen
        if (imagen != null) {
            int imgWidth = imagen.getWidth(null); //Ancho real del archivo PNG/GIF
            if (imgWidth > 0) {
                //escala = (tamaño que quiero) / (tamaño original)
                this.escala = ancho / imgWidth;
            } else {
                this.escala = 1.0; //Evitar división por cero
            }
        } else {
            this.escala = 1.0; //Sin imagen, escala 1
        }
        // System.out.println("  ObjetoDeJuego: Escala calculada=" + this.escala + " para objeto en ("+ x + "," + y + ")"); // Log opcional
    }


     //Dibuja el objeto usando la escala calculada
    public void dibujar(Entorno e) {
        if (this.imagen != null) {
            e.dibujarImagen(this.imagen, this.posicion.getX(), this.posicion.getY(), 0, this.escala);
        }
    }



    public void mover(double dx, double dy) { this.posicion.mover(dx, dy); }
    public Punto getPosicion() { return this.posicion; }
    public double getX() { return this.posicion.getX(); }
    public double getY() { return this.posicion.getY(); }
    public double getAncho() { return this.ancho; } 
    public double getAlto() { return this.alto; }   
    public Image getImagen() { return this.imagen; }
    public void setPosicion(double x, double y) { this.posicion.setX(x); this.posicion.setY(y); }
    public void setImagen(Image nuevaImagen) { this.imagen = nuevaImagen; /* Recalcular escala */ if (this.imagen != null && this.imagen.getWidth(null) > 0) { this.escala = this.ancho / this.imagen.getWidth(null); } else { this.escala = 1.0; } }
    public static boolean colisionan(Punto p1, double a1, double h1, Punto p2, double a2, double h2) { boolean nc = p1.getX()-a1/2>=p2.getX()+a2/2 || p1.getX()+a1/2<=p2.getX()-a2/2 || p1.getY()-h1/2>=p2.getY()+h2/2 || p1.getY()+h1/2<=p2.getY()-h2/2; return !nc; }

}