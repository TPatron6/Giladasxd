package juego;

import java.awt.Image;
import java.awt.Color;
import entorno.Entorno;
import entorno.InterfaceJuego;
import entorno.Herramientas;
import juego.planta.RoseBlade;
import juego.planta.WallNut;
import juego.planta.PlantaExplosiva;

public class Juego extends InterfaceJuego {
	private enum EstadoJuego {
	    MENU,
	    JUGANDO,
	    CREDITOS
	}

	private EstadoJuego estadoActual = EstadoJuego.MENU;

    private Entorno entorno;
    private Tablero tablero;

    // Imagenes
    private Image imgFondoPasto, imgRoseBlade, imgWallNut, imgPlantaExplosiva, imgTumba,
            gifZombie, gifZombieFast, gifZombieSlow, gifZombieShooter, gifZombieBoss,
            gifProyectil, gifProyectilZombie, imgRegalo, imgUI, gifExplosion;

    // UI
    private static final double CARTA1_X = 50;
    private static final double CARTA2_X = 120;
    private static final double CARTA3_X = 190;
    private static final double CARTA_Y = 50;
    private static final double CARTA_UI_ANCHO = 45;
    private static final double CARTA_UI_ALTO = 45;
    
    private static final double COOLDOWN_ROSEBLADE = 5.0;
    private static final double COOLDOWN_WALLNUT = 15.0;
    private static final double COOLDOWN_EXPLOSIVA = 20.0;
    
    //Botones del menu
    private static final double BOTON_JUEGO_X = 400;
    private static final double BOTON_JUEGO_Y = 300;
    private static final double BOTON_CREDITOS_X = 400;
    private static final double BOTON_CREDITOS_Y = 400;
    private static final double BOTON_ANCHO = 220;
    private static final double BOTON_ALTO = 60;
    
    private RoseBlade cartaRoseBlade;
    private WallNut cartaWallNut;
    private PlantaExplosiva cartaPlantaExplosiva;

    private Object plantaSiendoArrastrada;
    private String tipoPlantaArrastrada;
    private boolean mouseEstabaPresionado;
    private Object plantaSeleccionada;

    private static final double BOTON_REINICIO_X = 400;
    private static final double BOTON_REINICIO_Y = 350;
    private static final double BOTON_REINICIO_ANCHO = 220;
    private static final double BOTON_REINICIO_ALTO = 50;
    private static final double BOTON_MENU_X = 400;
    private static final double BOTON_MENU_Y = 420;
    private static final double BOTON_MENU_ANCHO = 220;
    private static final double BOTON_MENU_ALTO = 50;


    Juego() {
        this.entorno = new Entorno(this, "La Invasión Grinch", 800, 600);

        // Carga de imágenes
        this.imgFondoPasto = Herramientas.cargarImagen("Field.png");
        this.imgRoseBlade = Herramientas.cargarImagen("RoseBlade.png");
        this.imgWallNut = Herramientas.cargarImagen("WallNut.png");
        this.imgPlantaExplosiva = Herramientas.cargarImagen("planta-explosiva.png"); // Corregido a jpg
        this.imgTumba = Herramientas.cargarImagen("tumba.png"); // <-- NUEVO (Asegúrate que exista)
        this.gifZombie = Herramientas.cargarImagen("Zombie.gif");
        this.gifZombieFast = Herramientas.cargarImagen("Fastazul.gif");
        this.gifZombieSlow = Herramientas.cargarImagen("Slowrojo.gif");
        this.gifZombieShooter = Herramientas.cargarImagen("violetashooter.gif");
        this.gifZombieBoss = Herramientas.cargarImagen("zombie-gigante.gif");
        this.gifProyectil = Herramientas.cargarImagen("FireBall.gif"); // Corregido a B mayúscula
        this.gifProyectilZombie = Herramientas.cargarImagen("ZombieFireball.gif");
        this.imgRegalo = Herramientas.cargarImagen("regalo.png");
        this.imgUI = Herramientas.cargarImagen("UI.png");
        this.gifExplosion = Herramientas.cargarImagen("explosion.gif");

        this.inicializarJuego();
        this.entorno.iniciar();
    }

    private void inicializarJuego() {
        // Constructor de Tablero
        this.tablero = new Tablero(
            this.imgFondoPasto, this.imgRoseBlade, this.imgWallNut, this.imgPlantaExplosiva, this.imgTumba,
            this.gifZombie, this.gifZombieFast, this.gifZombieSlow, this.gifZombieShooter, this.gifZombieBoss,
            this.gifProyectil, this.gifProyectilZombie, this.imgRegalo, this.gifExplosion
        );

        // Cartas de UI
        this.cartaRoseBlade = new RoseBlade(CARTA1_X, CARTA_Y, this.imgRoseBlade);
        this.cartaWallNut = new WallNut(CARTA2_X, CARTA_Y, this.imgWallNut);
        this.cartaPlantaExplosiva = new PlantaExplosiva(CARTA3_X, CARTA_Y, this.imgPlantaExplosiva);

        this.plantaSiendoArrastrada = null;
        this.tipoPlantaArrastrada = null;
        this.mouseEstabaPresionado = false;
        this.plantaSeleccionada = null;
    }

    public void tick() {
    	//Manejo del Menu
    	switch (estadoActual) {
        case MENU:
            dibujarMenu();
            manejarInputMenu();
            break;
        case CREDITOS:
            dibujarCreditos();
            manejarInputCreditos();
            break;
        case JUGANDO:
            ejecutarJuego();
            break;
    	}
    }
    
    //Logica del tick movida
    private void ejecutarJuego() {
    	this.cartaRoseBlade.actualizarCooldown();
        this.cartaWallNut.actualizarCooldown();
        this.cartaPlantaExplosiva.actualizarCooldown();

        this.manejarInput();
        this.tablero.actualizar();
        this.tablero.dibujar(this.entorno);
        this.dibujarUI();
        this.mouseEstabaPresionado = this.entorno.estaPresionado(entorno.BOTON_IZQUIERDO);
    }
    
    //Menu agregado
    private void dibujarMenu() {
        entorno.dibujarRectangulo(400, 300, 800, 600, 0, Color.BLACK);
        entorno.cambiarFont("Impact", 40, Color.WHITE, 0);
        entorno.escribirTexto("LA INVASION GRINCH", 210, 150);

        entorno.dibujarRectangulo(BOTON_JUEGO_X, BOTON_JUEGO_Y, BOTON_ANCHO, BOTON_ALTO, 0, new Color(0, 100, 0, 200));
        entorno.cambiarFont("Impact", 28, Color.YELLOW, 0);
        entorno.escribirTexto("NUEVA PARTIDA", BOTON_JUEGO_X - 100, BOTON_JUEGO_Y + 8);

        entorno.dibujarRectangulo(BOTON_CREDITOS_X, BOTON_CREDITOS_Y, BOTON_ANCHO, BOTON_ALTO, 0, new Color(50, 50, 50, 200));
        entorno.cambiarFont("Impact", 26, Color.CYAN, 0);
        entorno.escribirTexto("CREDITOS", BOTON_CREDITOS_X - 60, BOTON_CREDITOS_Y + 8);
    }
    
    //Menu agregado
    private void manejarInputMenu() {
        boolean clic = entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO);
        double mouseX = entorno.mouseX();
        double mouseY = entorno.mouseY();

        if (clic) {
            //Boton "Nueva partida"
            if (mouseX > BOTON_JUEGO_X - BOTON_ANCHO / 2 && mouseX < BOTON_JUEGO_X + BOTON_ANCHO / 2 &&
                mouseY > BOTON_JUEGO_Y - BOTON_ALTO / 2 && mouseY < BOTON_JUEGO_Y + BOTON_ALTO / 2) {
                inicializarJuego();
                estadoActual = EstadoJuego.JUGANDO;
            }

            //Boton "Creditos"
            if (mouseX > BOTON_CREDITOS_X - BOTON_ANCHO / 2 && mouseX < BOTON_CREDITOS_X + BOTON_ANCHO / 2 &&
                mouseY > BOTON_CREDITOS_Y - BOTON_ALTO / 2 && mouseY < BOTON_CREDITOS_Y + BOTON_ALTO / 2) {
                estadoActual = EstadoJuego.CREDITOS;
            }
        }
    }

    private void manejarInput() {
        boolean clicPresionadoAhora = this.entorno.estaPresionado(entorno.BOTON_IZQUIERDO);
        boolean clicRecienPresionado = clicPresionadoAhora && !this.mouseEstabaPresionado;
        boolean clicRecienSoltado = !clicPresionadoAhora && this.mouseEstabaPresionado;
        double mouseX = this.entorno.mouseX();
        double mouseY = this.entorno.mouseY();

        // Clic en boton de reinicio
        if ((this.tablero.isJuegoTerminado() || this.tablero.isJuegoGanado()) && clicRecienPresionado) {
            boolean clickEnBoton = mouseX > BOTON_REINICIO_X - BOTON_REINICIO_ANCHO / 2 &&
                                   mouseX < BOTON_REINICIO_X + BOTON_REINICIO_ANCHO / 2 &&
                                   mouseY > BOTON_REINICIO_Y - BOTON_REINICIO_ALTO / 2 &&
                                   mouseY < BOTON_REINICIO_Y + BOTON_REINICIO_ALTO / 2;
            if (clickEnBoton) {
                inicializarJuego();
                return;
            }
            //Click en el boton menu principal
            boolean clickEnBotonMenu =  mouseX > BOTON_MENU_X - BOTON_MENU_ANCHO / 2 &&
                    					mouseX < BOTON_MENU_X + BOTON_MENU_ANCHO / 2 &&
                    					mouseY > BOTON_MENU_Y - BOTON_MENU_ALTO / 2 &&
                    					mouseY < BOTON_MENU_Y + BOTON_MENU_ALTO / 2;
            if (clickEnBotonMenu) {
            	this.estadoActual = EstadoJuego.MENU;
                return;
            }
        }

        if (!this.tablero.isJuegoTerminado() && !this.tablero.isJuegoGanado()) {

            // Click logica
            if (clicRecienPresionado) {
                if (mouseY < Tablero.INICIO_Y_GRILLA) { // Clic en UI
                    this.plantaSeleccionada = null;
                    
                    boolean clickEnCarta1 = mouseX > CARTA1_X - CARTA_UI_ANCHO / 2 && mouseX < CARTA1_X + CARTA_UI_ANCHO / 2 && mouseY > CARTA_Y - CARTA_UI_ALTO / 2 && mouseY < CARTA_Y + CARTA_UI_ALTO / 2;
        			boolean carta1Lista = this.cartaRoseBlade.getTiempoCargaRestante() <= 0;
        			
                    boolean clickEnCarta2 = mouseX > CARTA2_X - CARTA_UI_ANCHO / 2 && mouseX < CARTA2_X + CARTA_UI_ANCHO / 2 && mouseY > CARTA_Y - CARTA_UI_ALTO / 2 && mouseY < CARTA_Y + CARTA_UI_ALTO / 2;
        			boolean carta2Lista = this.cartaWallNut.getTiempoCargaRestante() <= 0;
        			
                    boolean clickEnCarta3 = mouseX > CARTA3_X - CARTA_UI_ANCHO / 2 && mouseX < CARTA3_X + CARTA_UI_ANCHO / 2 && mouseY > CARTA_Y - CARTA_UI_ALTO / 2 && mouseY < CARTA_Y + CARTA_UI_ALTO / 2;
        			boolean carta3Lista = this.cartaPlantaExplosiva.getTiempoCargaRestante() <= 0;


                    if (clickEnCarta1 && carta1Lista) { this.plantaSiendoArrastrada = new RoseBlade(mouseX, mouseY, this.imgRoseBlade); this.tipoPlantaArrastrada = "RoseBlade"; }
                    else if (clickEnCarta2 && carta2Lista) { this.plantaSiendoArrastrada = new WallNut(mouseX, mouseY, this.imgWallNut); this.tipoPlantaArrastrada = "WallNut"; }
                    else if (clickEnCarta3 && carta3Lista) { this.plantaSiendoArrastrada = new PlantaExplosiva(mouseX, mouseY, this.imgPlantaExplosiva); this.tipoPlantaArrastrada = "PlantaExplosiva"; }

                } else { // Clic en Tablero
                    if (this.plantaSiendoArrastrada == null) {
                        this.plantaSeleccionada = this.tablero.getPlantaEnCoordenadas(mouseX, mouseY);
                    }
                }
            }
            // Durante Arrastre
            if (clicPresionadoAhora && this.plantaSiendoArrastrada != null) {
                if (this.plantaSiendoArrastrada instanceof RoseBlade) { ((RoseBlade)this.plantaSiendoArrastrada).setPosicion(mouseX, mouseY); }
        		else if (this.plantaSiendoArrastrada instanceof WallNut) { ((WallNut)this.plantaSiendoArrastrada).setPosicion(mouseX, mouseY); }
                else if (this.plantaSiendoArrastrada instanceof PlantaExplosiva) { ((PlantaExplosiva)this.plantaSiendoArrastrada).setPosicion(mouseX, mouseY); }
            }
            // Fin arrastre
            if (clicRecienSoltado) {
                 if (this.plantaSiendoArrastrada != null && this.tipoPlantaArrastrada != null) {
                    if (mouseY > Tablero.INICIO_Y_GRILLA) {
                        double xCelda = Math.floor(mouseX / Tablero.ANCHO_CELDA) * Tablero.ANCHO_CELDA + (Tablero.ANCHO_CELDA / 2);
        				double yCelda = Math.floor((mouseY - Tablero.INICIO_Y_GRILLA) / Tablero.ALTO_CELDA) * Tablero.ALTO_CELDA + Tablero.INICIO_Y_GRILLA + (Tablero.ALTO_CELDA / 2);
                        boolean plantadaConExito = false;

                        if (!this.tablero.estaOcupada(xCelda, yCelda)) {
                            if (this.tipoPlantaArrastrada.equals("RoseBlade")) { plantadaConExito = this.tablero.agregarRoseBlade(xCelda, yCelda, this.imgRoseBlade); if (plantadaConExito) this.cartaRoseBlade.setTiempoCarga(COOLDOWN_ROSEBLADE); }
                            else if (this.tipoPlantaArrastrada.equals("WallNut")) { plantadaConExito = this.tablero.agregarWallNut(xCelda, yCelda, this.imgWallNut); if (plantadaConExito) this.cartaWallNut.setTiempoCarga(COOLDOWN_WALLNUT); }
                            else if (this.tipoPlantaArrastrada.equals("PlantaExplosiva")) { plantadaConExito = this.tablero.agregarPlantaExplosiva(xCelda, yCelda, this.imgPlantaExplosiva); if (plantadaConExito) this.cartaPlantaExplosiva.setTiempoCarga(COOLDOWN_EXPLOSIVA); }
                        }
                    }
                    this.plantaSiendoArrastrada = null; this.tipoPlantaArrastrada = null;
                }
            }
            // Movimiento teclado
            if (this.plantaSeleccionada != null && this.plantaSiendoArrastrada == null) {
                double dx = 0; double dy = 0; boolean teclaMovimiento = false;
                
                if (this.entorno.sePresiono(this.entorno.TECLA_DERECHA)||this.entorno.sePresiono('d')){dx=Tablero.ANCHO_CELDA;teclaMovimiento=true;} 
                if (this.entorno.sePresiono(this.entorno.TECLA_IZQUIERDA)||this.entorno.sePresiono('a')){dx=-Tablero.ANCHO_CELDA;teclaMovimiento=true;} 
                if (this.entorno.sePresiono(this.entorno.TECLA_ABAJO)||this.entorno.sePresiono('s')){dy=Tablero.ALTO_CELDA;teclaMovimiento=true;} 
                if (this.entorno.sePresiono(this.entorno.TECLA_ARRIBA)||this.entorno.sePresiono('w')){dy=-Tablero.ALTO_CELDA;teclaMovimiento=true;}

                if (teclaMovimiento) {
                    double currentX=0, currentY=0;
                    if(this.plantaSeleccionada instanceof RoseBlade){ currentX=((RoseBlade)this.plantaSeleccionada).getX(); currentY=((RoseBlade)this.plantaSeleccionada).getY(); }
                    else if(this.plantaSeleccionada instanceof WallNut){ currentX=((WallNut)this.plantaSeleccionada).getX(); currentY=((WallNut)this.plantaSeleccionada).getY(); }
                    else if(this.plantaSeleccionada instanceof PlantaExplosiva){ currentX=((PlantaExplosiva)this.plantaSeleccionada).getX(); currentY=((PlantaExplosiva)this.plantaSeleccionada).getY(); }

                    double targetX = currentX + dx;
                    double targetY = currentY + dy;
                    boolean dentroLimites = targetX >= Tablero.LIMITE_IZQ + Tablero.ANCHO_CELDA/2 && targetX <= Tablero.LIMITE_DER - Tablero.ANCHO_CELDA/2 && targetY >= Tablero.LIMITE_SUP + Tablero.ALTO_CELDA/2 && targetY <= Tablero.LIMITE_INF - Tablero.ALTO_CELDA/2;
                    
                    // Usa la nueva 'estaOcupadaPorOtroObjeto'
                    if (dentroLimites && !this.tablero.estaOcupadaPorOtroObjeto(targetX, targetY, this.plantaSeleccionada)) {
                        if(this.plantaSeleccionada instanceof RoseBlade){ ((RoseBlade)this.plantaSeleccionada).mover(dx, dy); }
                        else if(this.plantaSeleccionada instanceof WallNut){ ((WallNut)this.plantaSeleccionada).mover(dx, dy); }
                        else if(this.plantaSeleccionada instanceof PlantaExplosiva){ ((PlantaExplosiva)this.plantaSeleccionada).mover(dx, dy); }
                    }
                }
            }
        }
    }


    private void dibujarUI() {
        // Fondo UI
        this.entorno.dibujarImagen(this.imgUI, 400, 25, 0, 1.0);
        this.entorno.dibujarImagen(this.imgUI, 400, 75, Math.PI, 1.0);
        // Carta 1
        this.cartaRoseBlade.dibujar(this.entorno); double cd1=this.cartaRoseBlade.getTiempoCargaRestante(); if(cd1>0){this.entorno.dibujarRectangulo(CARTA1_X,CARTA_Y,CARTA_UI_ANCHO,CARTA_UI_ALTO,0,new Color(0,0,0,150)); this.entorno.cambiarFont(null,16,Color.WHITE,1); this.entorno.escribirTexto(String.format("%.1f",cd1),CARTA1_X-(CARTA_UI_ANCHO/4),CARTA_Y+5);}
        // Carta 2
        this.cartaWallNut.dibujar(this.entorno); double cd2=this.cartaWallNut.getTiempoCargaRestante(); if(cd2>0){this.entorno.dibujarRectangulo(CARTA2_X,CARTA_Y,CARTA_UI_ANCHO,CARTA_UI_ALTO,0,new Color(0,0,0,150)); this.entorno.cambiarFont(null,16,Color.WHITE,1); this.entorno.escribirTexto(String.format("%.1f",cd2),CARTA2_X-(CARTA_UI_ANCHO/4),CARTA_Y+5);}
        // Carta 3 (NUEVO)
        this.cartaPlantaExplosiva.dibujar(this.entorno); double cd3=this.cartaPlantaExplosiva.getTiempoCargaRestante(); if(cd3>0){this.entorno.dibujarRectangulo(CARTA3_X,CARTA_Y,CARTA_UI_ANCHO,CARTA_UI_ALTO,0,new Color(0,0,0,150)); this.entorno.cambiarFont(null,16,Color.WHITE,1); this.entorno.escribirTexto(String.format("%.1f",cd3),CARTA3_X-(CARTA_UI_ANCHO/4),CARTA_Y+5);}

        // Info Juego
        int el=this.tablero.getZombiesEliminados(), re=this.tablero.getEnemigosRestantes(); double tj=this.tablero.getTiempoDeJuego(); int sc=this.tablero.getScore(); String tf=String.format("%02d:%02d",(int)(tj/60),(int)(tj%60));
        // Ajuste de posición del texto
        this.entorno.cambiarFont("Times New Roman",16,Color.WHITE,1); this.entorno.escribirTexto("Eliminados: "+el,260,40); this.entorno.escribirTexto("Restantes: "+re,260,70); this.entorno.escribirTexto("Tiempo: "+tf,420,40);
        this.entorno.cambiarFont("Times New Roman",18,Color.ORANGE,1); this.entorno.escribirTexto("SCORE: "+sc,680,50);

        // Planta Arrastrada
        if(this.plantaSiendoArrastrada!=null){
            if (this.plantaSiendoArrastrada instanceof RoseBlade) { ((RoseBlade)this.plantaSiendoArrastrada).dibujar(this.entorno); }
            else if (this.plantaSiendoArrastrada instanceof WallNut) { ((WallNut)this.plantaSiendoArrastrada).dibujar(this.entorno); }
            else if (this.plantaSiendoArrastrada instanceof PlantaExplosiva) { ((PlantaExplosiva)this.plantaSiendoArrastrada).dibujar(this.entorno); }
        }
        // Planta Seleccionada
        if(this.plantaSeleccionada!=null && this.plantaSiendoArrastrada==null){
            double selX=0, selY=0, selAncho=0;
            if(this.plantaSeleccionada instanceof RoseBlade){ selX=((RoseBlade)this.plantaSeleccionada).getX(); selY=((RoseBlade)this.plantaSeleccionada).getY(); selAncho=((RoseBlade)this.plantaSeleccionada).getAncho(); }
            else if(this.plantaSeleccionada instanceof WallNut){ selX=((WallNut)this.plantaSeleccionada).getX(); selY=((WallNut)this.plantaSeleccionada).getY(); selAncho=((WallNut)this.plantaSeleccionada).getAncho(); }
            else if(this.plantaSeleccionada instanceof PlantaExplosiva){ selX=((PlantaExplosiva)this.plantaSeleccionada).getX(); selY=((PlantaExplosiva)this.plantaSeleccionada).getY(); selAncho=((PlantaExplosiva)this.plantaSeleccionada).getAncho(); }
            this.entorno.dibujarCirculo(selX, selY, selAncho / 1.5, new Color(255, 255, 0, 100));
        }
        
        // Game Over / Ganaste
        if(this.tablero.isJuegoTerminado()){this.entorno.cambiarFont("Impact",30,Color.RED,0); this.entorno.escribirTexto("¡¡GAME OVER REGALO DESTRUIDO!!",this.entorno.ancho()/2-195,this.entorno.alto()/2); 
        this.entorno.dibujarRectangulo(BOTON_REINICIO_X, BOTON_REINICIO_Y, BOTON_REINICIO_ANCHO, BOTON_REINICIO_ALTO, 0, new Color(0, 0, 0, 180));
        this.entorno.cambiarFont("Impact", 22, Color.YELLOW, 1);
        this.entorno.escribirTexto("  JUGAR DE NUEVO", BOTON_REINICIO_X - 80, BOTON_REINICIO_Y + 8);
        this.entorno.dibujarRectangulo(BOTON_MENU_X, BOTON_MENU_Y, BOTON_MENU_ANCHO, BOTON_MENU_ALTO, 0, new Color(0, 0, 0, 180));
        this.entorno.cambiarFont("Impact", 22, Color.CYAN, 1);
        this.entorno.escribirTexto("MENU PRINCIPAL", BOTON_MENU_X - 95, BOTON_MENU_Y + 8);}
        if(this.tablero.isJuegoGanado()){this.entorno.cambiarFont("Impact",30,Color.GREEN,1); this.entorno.escribirTexto("¡FELICIDADES SALVASTE LA NAVIDAD!",this.entorno.ancho()/2-220,this.entorno.alto()/2);
        this.entorno.dibujarRectangulo(BOTON_REINICIO_X, BOTON_REINICIO_Y, BOTON_REINICIO_ANCHO, BOTON_REINICIO_ALTO, 0, new Color(0, 0, 0, 180));
        this.entorno.cambiarFont(null, 22, Color.YELLOW, 1);
        this.entorno.escribirTexto("JUGAR OTRA VEZ", BOTON_REINICIO_X - 100, BOTON_REINICIO_Y + 8);
        this.entorno.dibujarRectangulo(BOTON_MENU_X, BOTON_MENU_Y, BOTON_MENU_ANCHO, BOTON_MENU_ALTO, 0, new Color(0, 0, 0, 180));
        this.entorno.cambiarFont("Impact", 22, Color.CYAN, 1);
        this.entorno.escribirTexto("MENU PRINCIPAL", BOTON_MENU_X - 95, BOTON_MENU_Y + 8);}
    }
    
    //Manejo de creditos
    private void manejarInputCreditos() {
        boolean clic = entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO);
        double mouseX = entorno.mouseX();
        double mouseY = entorno.mouseY();

        if (clic && mouseX > 400 - 90 && mouseX < 400 + 90 &&
            mouseY > 500 - 25 && mouseY < 500 + 25) {
            estadoActual = EstadoJuego.MENU;
        }
    }
    
    //Creditos agregados
    private void dibujarCreditos() {
        entorno.dibujarRectangulo(400, 300, 800, 600, 0, Color.BLACK);
        entorno.cambiarFont("Impact", 36, Color.WHITE, 0);
        entorno.escribirTexto("CREDITOS", 320, 100);

        entorno.cambiarFont("Arial", 22, Color.LIGHT_GRAY, 0);
        entorno.escribirTexto("Programacion: ", 180, 200);
        entorno.escribirTexto("Diseño y Sonido: ", 220, 200);
        entorno.escribirTexto("Colaborador: ", 260, 200);
        entorno.escribirTexto("Inspirado en: Plants vs Zombies", 300, 200);
        entorno.escribirTexto("Gracias por jugar!", 340, 300);

        entorno.dibujarRectangulo(400, 500, 180, 50, 0, new Color(0, 0, 0, 150));
        entorno.cambiarFont("Impact", 22, Color.YELLOW, 0);
        entorno.escribirTexto("VOLVER", 340, 510);
    }

    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}