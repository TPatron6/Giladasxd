package juego;

import entorno.Entorno;
import juego.entidades.Zombie;
import juego.entidades.Proyectil;
import juego.entidades.Regalo;
import juego.planta.RoseBlade;
import juego.planta.WallNut;

import java.util.Random;
import java.awt.Image;
import java.awt.Color;

public class Tablero {

    //limites y tamaños
    private static final int MAX_PLANTAS_POR_TIPO = 25;
    private static final int MAX_ZOMBIES = 15;
    private static final int MAX_PROYECTILES = 100;
    private static final int NUM_FILAS = 5;
    public static final double INICIO_Y_GRILLA = 100; //debajo de la UI
    public static final double ANCHO_CELDA = 100;
    public static final double ALTO_CELDA = 500.0 / NUM_FILAS; //alto de cada fila (100)
    public static final double LIMITE_IZQ = 0;
    public static final double LIMITE_DER = 800;
    public static final double LIMITE_SUP = INICIO_Y_GRILLA;
    public static final double LIMITE_INF = 600;

    //arrays para guardar todo
    private RoseBlade[] roseBlades;
    private WallNut[] wallNuts;
    private Zombie[] zombies;
    private Proyectil[] proyectiles;
    private Regalo[] regalos;

    //imagenes y variables del juego
    private Image imgFondoPasto, imgRoseBlade, imgWallNut, imgZombie, imgZombieFast, imgProyectil, imgRegalo;
    private int score;
    private boolean juegoTerminado;
    private Random random;
    private boolean juegoGanado;
    private double tiempoDeJuego;
    private int zombiesEliminados;
    private static final int ENEMIGOS_TOTALES_PARA_GANAR = 100;
    private double tiempoHastaProximoZombie;
    private static final double INTERVALO_ZOMBIE = 5.0;

    //constructor
    public Tablero(Image fondo, Image roseBlade, Image wallNut, Image zombie, Image zombieFast, Image proyectil, Image regalo) {
        //inicializa los arrays
        this.roseBlades = new RoseBlade[MAX_PLANTAS_POR_TIPO];
        this.wallNuts = new WallNut[MAX_PLANTAS_POR_TIPO];
        this.zombies = new Zombie[MAX_ZOMBIES];
        this.proyectiles = new Proyectil[MAX_PROYECTILES];
        this.regalos = new Regalo[NUM_FILAS];
        //guarda las imagenes
        this.imgFondoPasto = fondo; this.imgRoseBlade = roseBlade; this.imgWallNut = wallNut; this.imgZombie = zombie; this.imgZombieFast = zombieFast; this.imgProyectil = proyectil; this.imgRegalo = regalo;
        //inicializa variables de estado
        this.score = 0; this.juegoTerminado = false; this.random = new Random(); this.tiempoHastaProximoZombie = 0; this.juegoGanado = false; this.tiempoDeJuego = 0; this.zombiesEliminados = 0;
        //pone los regalos
        this.inicializarRegalosFijos();
    }

    //pone los 5 regalos a la izquierda
    private void inicializarRegalosFijos() {
        final double X_COLUMNA = 30;
        final double INICIO_Y = INICIO_Y_GRILLA + (ALTO_CELDA / 2);
        for (int i = 0; i < NUM_FILAS; i++) {
            double yPos = INICIO_Y + (i * ALTO_CELDA);
            this.regalos[i] = new Regalo(X_COLUMNA, yPos, this.imgRegalo);
        }
    }

    //METODOS DEL TICK

    //metodo principal llamado desde Juego.tick()
    public void actualizar() {
        if (this.juegoTerminado || this.juegoGanado) return; //si termino, no hace nada
        this.tiempoDeJuego += 1.0 / 60.0; //actualiza tiempo
        //llama a todos los metodos de logica y movimiento
        this.generarEnemigos();
        this.actualizarPlantas();
        this.actualizarProyectiles();
        this.actualizarZombies();
        this.gestionarColisiones();
        this.moverZombies();
        this.limpiarEntidades();
    }

    //cuenta cuantos zombies hay ahora
    private int contarZombies() {
        int contador = 0;
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) contador++; }
        return contador;
    }

    //genera zombies nuevos si toca
    private void generarEnemigos() {
        this.tiempoHastaProximoZombie -= 1.0 / 60.0;
        if (this.tiempoHastaProximoZombie <= 0 && this.contarZombies() < MAX_ZOMBIES) {
            final double INICIO_Y = INICIO_Y_GRILLA + (ALTO_CELDA / 2);
            double ySpawn = INICIO_Y + (this.random.nextInt(NUM_FILAS) * ALTO_CELDA);
            Image zombieElegido = this.imgZombie; double velocidadBase = 1.0;
            if (this.random.nextDouble() < 0.20) { zombieElegido = this.imgZombieFast; velocidadBase = 1.5; } //chance de zombie rapido
            //busca lugar y lo crea
            for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] == null) { this.zombies[i] = new Zombie(850, ySpawn, zombieElegido, velocidadBase); break; } }
            this.tiempoHastaProximoZombie = INTERVALO_ZOMBIE; //reinicia timer
        }
    }

    //guarda una bala en el array
    private void agregarProyectil(Proyectil p) {
         for (int i = 0; i < this.proyectiles.length; i++) { if (this.proyectiles[i] == null) { this.proyectiles[i] = p; return; } }
    }

    //actualiza todas las plantas
    private void actualizarPlantas() {
         //actualiza rosas (y dispara si puede)
         for (int i = 0; i < this.roseBlades.length; i++) {
             if (this.roseBlades[i] != null) {
                 RoseBlade rb = this.roseBlades[i];
                 rb.actualizar(this.zombies);
                 if (rb.puedeDisparar()) { Proyectil np = rb.disparar(this.imgProyectil); if (np != null) { this.agregarProyectil(np); } }
             }
         }
         //actualiza nueces
         for (int i = 0; i < this.wallNuts.length; i++) {
              if (this.wallNuts[i] != null) { this.wallNuts[i].actualizar(this.zombies); }
         }
    }

    //mueve las balas
    private void actualizarProyectiles() {
         for (int i = 0; i < this.proyectiles.length; i++) { if (this.proyectiles[i] != null) this.proyectiles[i].actualizar(); }
    }

    //actualiza estado interno de zombies (ej cooldown ataque)
    private void actualizarZombies() {
         for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) { this.zombies[i].actualizar(); } }
    }

    //mueve los zombies que no esten chocando
    private void moverZombies() {
         for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) { this.zombies[i].intentarMover(); } }
    }

    //chequea todos los choques
    private void gestionarColisiones() {
        //0 resetea flags de colision en zombies
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) this.zombies[i].setEstaColisionando(false); }

        //1 bala vs zombie
        for (int i = 0; i < this.proyectiles.length; i++) {
            if (this.proyectiles[i] == null) continue;
            Proyectil p = this.proyectiles[i];
            for (int j = 0; j < this.zombies.length; j++) {
                if (this.zombies[j] == null) continue;
                Zombie z = this.zombies[j];
                if (ObjetoDeJuego.colisionan(p.getPosicion(), p.getAncho(), p.getAlto(), z.getPosicion(), z.getAncho(), z.getAlto())) {
                    z.recibirDano(p.getDano()); //zombie recibe daño
                    this.proyectiles[i] = null; //bala desaparece
                    this.score += 10; //suma puntos
                    break; //bala ya choco
                }
            }
        }

        //2 zombie vs regalo (game over)
        for (int i = 0; i < this.zombies.length; i++) {
            if (this.zombies[i] == null) continue;
            Zombie z = this.zombies[i];
            for (int j = 0; j < this.regalos.length; j++) {
                Regalo r = this.regalos[j];
                if (r == null) continue; //chequeo por las dudas
                if (ObjetoDeJuego.colisionan(z.getPosicion(), z.getAncho(), z.getAlto(), r.getPosicion(), r.getAncho(), r.getAlto())) {
                    System.out.println("--- COLISIÓN FATAL ---"); //log
                    System.out.println("Zombie["+i+"] @ ("+z.getX()+","+z.getY()+") vs Regalo["+j+"] @ ("+r.getX()+","+r.getY()+")");
                    this.juegoTerminado = true; //perdiste
                    return;
                }
            }
        }

        //3 zombie vs planta
        for (int i = 0; i < this.zombies.length; i++) {
            if (this.zombies[i] == null) continue;
            Zombie z = this.zombies[i];
            boolean colisionDetectadaEsteZombie = false; //para que no choque con dos a la vez

            //contra rosas
            for (int j = 0; j < this.roseBlades.length; j++) {
                if (this.roseBlades[j] == null) continue;
                RoseBlade rb = this.roseBlades[j];
                if (ObjetoDeJuego.colisionan(z.getPosicion(), z.getAncho(), z.getAlto(), rb.getPosicion(), rb.getAncho(), rb.getAlto())) {
                    z.setEstaColisionando(true); //zombie frena
                    rb.recibirDano(rb.getVida()); //rosa muere
                    colisionDetectadaEsteZombie = true;
                    break; //listo con este zombie
                }
            }
            if (colisionDetectadaEsteZombie) continue; //si ya choco, pasa al siguiente zombie

            //contra nueces
            for (int j = 0; j < this.wallNuts.length; j++) {
                if (this.wallNuts[j] == null) continue;
                WallNut wn = this.wallNuts[j];
                if (ObjetoDeJuego.colisionan(z.getPosicion(), z.getAncho(), z.getAlto(), wn.getPosicion(), wn.getAncho(), wn.getAlto())) {
                    z.setEstaColisionando(true); //zombie frena
                    if (z.puedeAtacar()) { //si el zombie puede atacar
                        wn.recibirDano(z.getAtaque()); //nuez recibe daño
                        z.reiniciarCooldownAtaque(); //zombie reinicia timer de ataque
                    }
                    break; //listo con este zombie
                }
            }
        }
    }

    //pone en null lo que este muerto o fuera de pantalla
    private void limpiarEntidades() {
        //1 zombies muertos
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null && this.zombies[i].estaMuerto()) { this.zombies[i] = null; this.zombiesEliminados++; } }
        //2 balas fuera
        for (int i = 0; i < this.proyectiles.length; i++) { if (this.proyectiles[i] != null) { double x = this.proyectiles[i].getX(); if (x > 850 || x < -50) this.proyectiles[i] = null; } }
        //3 plantas muertas
        for (int i = 0; i < this.roseBlades.length; i++) { if (this.roseBlades[i] != null && this.roseBlades[i].estaMuerta()) { this.roseBlades[i] = null; } }
        for (int i = 0; i < this.wallNuts.length; i++) { if (this.wallNuts[i] != null && this.wallNuts[i].estaMuerta()) { this.wallNuts[i] = null; } }
        //chequea si ganaste
        if (!this.juegoGanado && this.zombiesEliminados >= ENEMIGOS_TOTALES_PARA_GANAR) { this.juegoGanado = true; }
    }

    //METODO PARA DIBUJAR
    public void dibujar(Entorno e) {
        //dibuja fondo
        if (this.imgFondoPasto != null) { int ta=imgFondoPasto.getWidth(null),th=imgFondoPasto.getHeight(null); if(ta>0&&th>0){for(int y=0;y<e.alto();y+=th){for(int x=0;x<e.ancho();x+=ta){e.dibujarImagen(imgFondoPasto,x+ta/2.0,y+th/2.0,0);}}} else {e.colorFondo(Color.GREEN);}} else {e.colorFondo(Color.GRAY);}
        //dibuja entidades (solo si no son null)
        for (int i = 0; i < this.roseBlades.length; i++) { if (this.roseBlades[i] != null) this.roseBlades[i].dibujar(e); }
        for (int i = 0; i < this.wallNuts.length; i++) { if (this.wallNuts[i] != null) this.wallNuts[i].dibujar(e); }
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) this.zombies[i].dibujar(e); }
        for (int i = 0; i < this.proyectiles.length; i++) { if (this.proyectiles[i] != null) this.proyectiles[i].dibujar(e); }
        for (int i = 0; i < this.regalos.length; i++) { if (this.regalos[i] != null) this.regalos[i].dibujar(e); }
    }

    //METODOS DE INTERACCION (usados por Juego.java)

    //devuelve la planta (rosa o nuez) que este en x,y o null si no hay nada
    public Object getPlantaEnCoordenadas(double x, double y) {
        //busca en rosas
        for(int i = 0; i < this.roseBlades.length; i++) { if (this.roseBlades[i] != null) { RoseBlade p = this.roseBlades[i]; boolean dx = x >= p.getX() - p.getAncho()/2 && x <= p.getX() + p.getAncho()/2; boolean dy = y >= p.getY() - p.getAlto()/2 && y <= p.getY() + p.getAlto()/2; if (dx && dy) return p; } }
        //busca en nueces
        for(int i = 0; i < this.wallNuts.length; i++) { if (this.wallNuts[i] != null) { WallNut p = this.wallNuts[i]; boolean dx = x >= p.getX() - p.getAncho()/2 && x <= p.getX() + p.getAncho()/2; boolean dy = y >= p.getY() - p.getAlto()/2 && y <= p.getY() + p.getAlto()/2; if (dx && dy) return p; } }
        return null;
    }

    //dice si la celda x,y esta ocupada
    public boolean estaOcupada(double x, double y) {
        return getPlantaEnCoordenadas(x, y) != null;
    }

    //dice si la celda x,y esta ocupada por OTRA planta (no la que se mueve)
    public boolean estaOcupadaPorOtraPlanta(double x, double y, Object plantaAExcluir) {
        Object plantaEnCelda = getPlantaEnCoordenadas(x, y);
        return plantaEnCelda != null && plantaEnCelda != plantaAExcluir;
    }

    //intenta poner una rosa en x,y
    public boolean agregarRoseBlade(double x, double y, Image imagen) {
        if (estaOcupada(x, y)) return false; //si esta ocupada, no hace nada
        //busca lugar vacio
        for (int i = 0; i < this.roseBlades.length; i++) {
            if (this.roseBlades[i] == null) {
                this.roseBlades[i] = new RoseBlade(x, y, this.imgRoseBlade); //la crea
                return true; //avisa que pudo
            }
        }
        return false; //no pudo (array lleno)
    }

    //intenta poner una nuez en x,y
    public boolean agregarWallNut(double x, double y, Image imagen) {
        if (estaOcupada(x, y)) return false; //si esta ocupada, no hace nada
        //busca lugar vacio
        for (int i = 0; i < this.wallNuts.length; i++) {
            if (this.wallNuts[i] == null) {
                this.wallNuts[i] = new WallNut(x, y, this.imgWallNut); //la crea
                return true; //avisa que pudo
            }
        }
        return false; //no pudo (array lleno)
    }

    //GETTERS (para que Juego.java muestre la info)
    public int getZombiesEliminados() { return this.zombiesEliminados; }
    public int getEnemigosRestantes() { return Math.max(0, ENEMIGOS_TOTALES_PARA_GANAR - this.zombiesEliminados); }
    public double getTiempoDeJuego() { return this.tiempoDeJuego; }
    public boolean isJuegoTerminado() { return this.juegoTerminado; }
    public boolean isJuegoGanado() { return this.juegoGanado; }
    public int getScore() { return this.score; }

}