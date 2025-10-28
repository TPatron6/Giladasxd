package juego;

public class Punto {
    private double x;
    private double y;

    public Punto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //Getters

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    //Setters

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    //metodos de Utilidad
    public void mover(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public double distanciaA(Punto otro) {
        double diffX = this.x - otro.x;
        double diffY = this.y - otro.y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
}