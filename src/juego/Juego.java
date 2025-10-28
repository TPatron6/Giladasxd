package juego;

import java.awt.Image;
import java.awt.Color;
import entorno.Entorno;
import entorno.InterfaceJuego;
import entorno.Herramientas;
import juego.planta.RoseBlade;
import juego.planta.WallNut;

public class Juego extends InterfaceJuego {

    private Entorno entorno;
    private Tablero tablero;

    //Imagenes
    private Image imgFondoPasto, imgRoseBlade, imgWallNut, gifZombie, gifZombieFast, gifProyectil, imgRegalo;

    //UI 
    private static final double CARTA1_X = 50; /*...*/ private static final double CARTA1_Y = 50; /*...*/ private static final double CARTA_UI_ANCHO = 40; /*...*/ private static final double CARTA_UI_ALTO = 40; /*...*/ private static final double COOLDOWN_ROSEBLADE = 5.0; /*...*/ private RoseBlade cartaRoseBlade;
    private static final double CARTA2_X = 120; /*...*/ private static final double CARTA2_Y = 50; /*...*/ private static final double CARTA2_UI_ANCHO = 45; /*...*/ private static final double CARTA2_UI_ALTO = 45; /*...*/ private static final double COOLDOWN_WALLNUT = 15.0; /*...*/ private WallNut cartaWallNut;

    private Object plantaSiendoArrastrada; // Puede ser RoseBlade o WallNut
    private String tipoPlantaArrastrada;
    private boolean mouseEstabaPresionado;

    private Object plantaSeleccionada; // Puede ser RoseBlade o WallNut


    Juego() {
        this.entorno = new Entorno(this, "La Invasión Grinch", 800, 600);
        this.imgFondoPasto = Herramientas.cargarImagen("Field.png"); this.imgRoseBlade = Herramientas.cargarImagen("RoseBlade.png"); this.imgWallNut = Herramientas.cargarImagen("WallNut.png"); this.gifZombie = Herramientas.cargarImagen("Zombie.gif"); this.gifZombieFast = Herramientas.cargarImagen("ZombieFast.gif"); this.gifProyectil = Herramientas.cargarImagen("Fireball.gif"); this.imgRegalo = Herramientas.cargarImagen("regalo.png");
        this.tablero = new Tablero(this.imgFondoPasto, this.imgRoseBlade, this.imgWallNut, this.gifZombie, this.gifZombieFast, this.gifProyectil, this.imgRegalo);
        this.cartaRoseBlade = new RoseBlade(CARTA1_X, CARTA1_Y, this.imgRoseBlade); this.cartaWallNut = new WallNut(CARTA2_X, CARTA2_Y, this.imgWallNut);
        this.plantaSiendoArrastrada = null; this.tipoPlantaArrastrada = null; this.mouseEstabaPresionado = false; this.plantaSeleccionada = null;
        this.entorno.iniciar();
    }

    public void tick() {
        this.cartaRoseBlade.actualizarCooldown(); this.cartaWallNut.actualizarCooldown();
        this.manejarInput();
        this.tablero.actualizar();
        this.tablero.dibujar(this.entorno);
        this.dibujarUI();
        this.mouseEstabaPresionado = this.entorno.estaPresionado(entorno.BOTON_IZQUIERDO);
    }

    //casting
    private void manejarInput() {
        boolean clicPresionadoAhora = this.entorno.estaPresionado(entorno.BOTON_IZQUIERDO);
        boolean clicRecienPresionado = clicPresionadoAhora && !this.mouseEstabaPresionado;
        boolean clicRecienSoltado = !clicPresionadoAhora && this.mouseEstabaPresionado;
        double mouseX = this.entorno.mouseX();
        double mouseY = this.entorno.mouseY();

        //Click logica
        if (clicRecienPresionado) {
            if (mouseY < Tablero.INICIO_Y_GRILLA) { // Clic en UI
                this.plantaSeleccionada = null;
                boolean clickEnCarta1 = mouseX > CARTA1_X - CARTA_UI_ANCHO / 2 && mouseX < CARTA1_X + CARTA_UI_ANCHO / 2 && mouseY > CARTA1_Y - CARTA_UI_ALTO / 2 && mouseY < CARTA1_Y + CARTA_UI_ALTO / 2;
                boolean carta1Lista = this.cartaRoseBlade.getTiempoCargaRestante() <= 0;
                boolean clickEnCarta2 = mouseX > CARTA2_X - CARTA2_UI_ANCHO / 2 && mouseX < CARTA2_X + CARTA2_UI_ANCHO / 2 && mouseY > CARTA2_Y - CARTA2_UI_ALTO / 2 && mouseY < CARTA2_Y + CARTA2_UI_ALTO / 2;
                boolean carta2Lista = this.cartaWallNut.getTiempoCargaRestante() <= 0;

                //Asignacion a 'plantaSiendoArrastrada' (tipo Object)
                if (clickEnCarta1 && carta1Lista) { this.plantaSiendoArrastrada = new RoseBlade(mouseX, mouseY, this.imgRoseBlade); this.tipoPlantaArrastrada = "RoseBlade"; }
                else if (clickEnCarta2 && carta2Lista) { this.plantaSiendoArrastrada = new WallNut(mouseX, mouseY, this.imgWallNut); this.tipoPlantaArrastrada = "WallNut"; }

            } else { // Clic en Tablero
                if (this.plantaSiendoArrastrada == null) {
                    // getPlantaEnCoordenadas devuelve Object
                    this.plantaSeleccionada = this.tablero.getPlantaEnCoordenadas(mouseX, mouseY);
                }
            }
        }
        //Durante Arrastre
        if (clicPresionadoAhora && this.plantaSiendoArrastrada != null) {
            //Necesitamos hacer casting para llamar a setPosicion
            if (this.plantaSiendoArrastrada instanceof RoseBlade) {
                ((RoseBlade)this.plantaSiendoArrastrada).setPosicion(mouseX, mouseY);
            } else if (this.plantaSiendoArrastrada instanceof WallNut) {
                 ((WallNut)this.plantaSiendoArrastrada).setPosicion(mouseX, mouseY);
            }
        }
        //Fin arrastre
        if (clicRecienSoltado) {
            if (this.plantaSiendoArrastrada != null && this.tipoPlantaArrastrada != null) {
                if (mouseY > Tablero.INICIO_Y_GRILLA) {
                    double xCelda = Math.floor(mouseX / Tablero.ANCHO_CELDA) * Tablero.ANCHO_CELDA + (Tablero.ANCHO_CELDA / 2);
                    double yCelda = Math.floor((mouseY - Tablero.INICIO_Y_GRILLA) / Tablero.ALTO_CELDA) * Tablero.ALTO_CELDA + Tablero.INICIO_Y_GRILLA + (Tablero.ALTO_CELDA / 2);
                    boolean plantadaConExito = false;
                    if (this.tipoPlantaArrastrada.equals("RoseBlade")) { plantadaConExito = this.tablero.agregarRoseBlade(xCelda, yCelda, this.imgRoseBlade); if (plantadaConExito) this.cartaRoseBlade.setTiempoCarga(COOLDOWN_ROSEBLADE); }
                    else if (this.tipoPlantaArrastrada.equals("WallNut")) { plantadaConExito = this.tablero.agregarWallNut(xCelda, yCelda, this.imgWallNut); if (plantadaConExito) this.cartaWallNut.setTiempoCarga(COOLDOWN_WALLNUT); }
                }
                this.plantaSiendoArrastrada = null; this.tipoPlantaArrastrada = null;
            }
        }
        //Movimiento teclado
        if (this.plantaSeleccionada != null && this.plantaSiendoArrastrada == null) {
            double dx = 0; double dy = 0; boolean teclaMovimiento = false;
            // Detección de teclas 
            if (this.entorno.sePresiono(this.entorno.TECLA_DERECHA)||this.entorno.sePresiono('d')){dx=Tablero.ANCHO_CELDA;teclaMovimiento=true;} if (this.entorno.sePresiono(this.entorno.TECLA_IZQUIERDA)||this.entorno.sePresiono('a')){dx=-Tablero.ANCHO_CELDA;teclaMovimiento=true;} if (this.entorno.sePresiono(this.entorno.TECLA_ABAJO)||this.entorno.sePresiono('s')){dy=Tablero.ALTO_CELDA;teclaMovimiento=true;} if (this.entorno.sePresiono(this.entorno.TECLA_ARRIBA)||this.entorno.sePresiono('w')){dy=-Tablero.ALTO_CELDA;teclaMovimiento=true;}

            if (teclaMovimiento) {
                // Necesitamos hacer casting para getX/getY
                double currentX=0, currentY=0;
                if(this.plantaSeleccionada instanceof RoseBlade){ currentX=((RoseBlade)this.plantaSeleccionada).getX(); currentY=((RoseBlade)this.plantaSeleccionada).getY(); }
                else if(this.plantaSeleccionada instanceof WallNut){ currentX=((WallNut)this.plantaSeleccionada).getX(); currentY=((WallNut)this.plantaSeleccionada).getY(); }

                double targetX = currentX + dx;
                double targetY = currentY + dy;
                // Validación de límites (igual)
                boolean dentroLimites = targetX >= Tablero.LIMITE_IZQ + Tablero.ANCHO_CELDA/2 && targetX <= Tablero.LIMITE_DER - Tablero.ANCHO_CELDA/2 && targetY >= Tablero.LIMITE_SUP + Tablero.ALTO_CELDA/2 && targetY <= Tablero.LIMITE_INF - Tablero.ALTO_CELDA/2;

                // estaOcupadaPorOtraPlanta recibe Object
                if (dentroLimites && !this.tablero.estaOcupadaPorOtraPlanta(targetX, targetY, this.plantaSeleccionada)) {
                    // Hacer casting para llamar a mover
                     if(this.plantaSeleccionada instanceof RoseBlade){ ((RoseBlade)this.plantaSeleccionada).mover(dx, dy); }
                     else if(this.plantaSeleccionada instanceof WallNut){ ((WallNut)this.plantaSeleccionada).mover(dx, dy); }
                }
            }
        }
    }

    //Trabaja con Object y hace casting
    private void dibujarUI() {
        // Fondo UI 
        Color cFondo=new Color(101,67,33), cBorde=new Color(210,180,140); this.entorno.dibujarRectangulo(400,50,800,100,0,cFondo); this.entorno.dibujarRectangulo(400,100,800,2,0,cBorde);
        // Carta 1 
        this.cartaRoseBlade.dibujar(this.entorno); double cd1=this.cartaRoseBlade.getTiempoCargaRestante(); if(cd1>0){this.entorno.dibujarRectangulo(CARTA1_X,CARTA1_Y,CARTA_UI_ANCHO,CARTA_UI_ALTO,0,new Color(0,0,0,150)); this.entorno.cambiarFont(null,16,Color.WHITE,1); this.entorno.escribirTexto(String.format("%.1f",cd1),CARTA1_X-(CARTA_UI_ANCHO/4),CARTA1_Y+5);}
        // Carta 2 
        this.cartaWallNut.dibujar(this.entorno); double cd2=this.cartaWallNut.getTiempoCargaRestante(); if(cd2>0){this.entorno.dibujarRectangulo(CARTA2_X,CARTA2_Y,CARTA2_UI_ANCHO,CARTA2_UI_ALTO,0,new Color(0,0,0,150)); this.entorno.cambiarFont(null,16,Color.WHITE,1); this.entorno.escribirTexto(String.format("%.1f",cd2),CARTA2_X-(CARTA2_UI_ANCHO/4),CARTA2_Y+5);}
        // Info Juego
        int el=this.tablero.getZombiesEliminados(), re=this.tablero.getEnemigosRestantes(); double tj=this.tablero.getTiempoDeJuego(); int sc=this.tablero.getScore(); String tf=String.format("%02d:%02d",(int)(tj/60),(int)(tj%60));
        this.entorno.cambiarFont(null,16,Color.WHITE,0); this.entorno.escribirTexto("Eliminados: "+el,200,40); this.entorno.escribirTexto("Restantes: "+re,200,70); this.entorno.escribirTexto("Tiempo: "+tf,350,40);
        this.entorno.cambiarFont(null,18,Color.ORANGE,1); this.entorno.escribirTexto("SCORE: "+sc,680,50);

        // Planta Arrastrada (Necesita casting para dibujar)
        if(this.plantaSiendoArrastrada!=null){
            if (this.plantaSiendoArrastrada instanceof RoseBlade) { ((RoseBlade)this.plantaSiendoArrastrada).dibujar(this.entorno); }
            else if (this.plantaSiendoArrastrada instanceof WallNut) { ((WallNut)this.plantaSiendoArrastrada).dibujar(this.entorno); }
        }
        // Planta Seleccionada (Necesita casting para getX/getY/getAncho)
        if(this.plantaSeleccionada!=null && this.plantaSiendoArrastrada==null){
            double selX=0, selY=0, selAncho=0;
            if(this.plantaSeleccionada instanceof RoseBlade){ selX=((RoseBlade)this.plantaSeleccionada).getX(); selY=((RoseBlade)this.plantaSeleccionada).getY(); selAncho=((RoseBlade)this.plantaSeleccionada).getAncho(); }
            else if(this.plantaSeleccionada instanceof WallNut){ selX=((WallNut)this.plantaSeleccionada).getX(); selY=((WallNut)this.plantaSeleccionada).getY(); selAncho=((WallNut)this.plantaSeleccionada).getAncho(); }
            this.entorno.dibujarCirculo(selX, selY, selAncho / 1.5, new Color(255, 255, 0, 100));
        }
        // Game Over / Ganaste
        if(this.tablero.isJuegoTerminado()){this.entorno.cambiarFont(null,30,Color.RED,1); this.entorno.escribirTexto("GAME OVER. REGALO DESTRUIDO.",this.entorno.ancho()/2-150,this.entorno.alto()/2);}
        if(this.tablero.isJuegoGanado()){this.entorno.cambiarFont(null,30,Color.GREEN,1); this.entorno.escribirTexto("¡GANASTE! PUEBLO SALVADO.",this.entorno.ancho()/2-150,this.entorno.alto()/2);}
    }

    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}