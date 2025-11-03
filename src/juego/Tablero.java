package juego;

import entorno.Entorno;
import juego.entidades.*; // Importa todo el paquete
import juego.planta.*; // Importa todo el paquete

import java.util.Random;
import java.awt.Image;
import java.awt.Color; // Necesario para Color.GRAY etc. en dibujar()

public class Tablero {

    // --- Constantes ---
    private static final int MAX_PLANTAS_POR_TIPO = 25;
    private static final int MAX_PLANTAS_EXPLOSIVAS = 10;
    private static final int MAX_EXPLOSIONES = 10;
    private static final int MAX_TUMBAS = 15;
    private static final int MAX_PROYECTILES_PLANTA = 100;
    private static final int MAX_PROYECTILES_ZOMBIE = 50;
    private static final int NUM_FILAS = 5;
    public static final double INICIO_Y_GRILLA = 100;
    public static final double ANCHO_CELDA = 100;
    public static final double ALTO_CELDA = 500.0 / NUM_FILAS;
    public static final double LIMITE_IZQ = 0;
    public static final double LIMITE_DER = 800;
    public static final double LIMITE_SUP = INICIO_Y_GRILLA;
    public static final double LIMITE_INF = 600;
    private static final double ANCHO_EXPLOSION = 150;
    private static final double ALTO_EXPLOSION = 150;
    private static final int DANO_EXPLOSION = 1000;
    private static final int MAX_ZOMBIES_COMUNES = 10;
    private static final int MAX_ZOMBIES_RAPIDOS = 5;
    private static final int MAX_ZOMBIES_LENTOS = 5;
    private static final int MAX_ZOMBIES_SHOOTERS = 3;
    private static final int MAX_ZOMBIES_JEFES = 2;
    private static final int MAX_ZOMBIES_TOTAL = MAX_ZOMBIES_COMUNES + MAX_ZOMBIES_RAPIDOS + MAX_ZOMBIES_LENTOS + MAX_ZOMBIES_SHOOTERS;
    private static final int ENEMIGOS_PARA_JEFE = 100;
    private static final double INTERVALO_ZOMBIE = 4.0;
    private static final double CHANCE_TUMBA = 0.25;

    // --- Arrays ---
    private RoseBlade[] roseBlades;
    private WallNut[] wallNuts;
    private PlantaExplosiva[] plantasExplosivas;
    private Tumba[] tumbas;
    private Zombie[] zombies;
    private ZombieFast[] zombiesFast;
    private ZombieSlow[] zombiesSlow;
    private ZombieShooter[] zombiesShooter;
    private ZombieBoss[] zombiesBoss;
    private Proyectil[] proyectiles;
    private ProyectilZombie[] proyectilesZombies;
    private Regalo[] regalos;
    private Explosion[] explosiones;

    // --- Imágenes y Estado ---
    private Image imgFondoPasto, imgRoseBlade, imgWallNut, imgPlantaExplosiva, imgTumba,
            imgZombie, imgZombieFast, imgZombieSlow, imgZombieShooter, imgZombieBoss,
            imgProyectil, imgProyectilZombie, imgRegalo, imgExplosion;
    private int score;
    private boolean juegoTerminado;
    private Random random;
    private boolean juegoGanado;
    private double tiempoDeJuego;
    private int zombiesEliminados;
    private boolean bossSpawned;
    private double tiempoHastaProximoZombie;
    
    private double tiempoTranscurrido = 0;
    private double factorAceleracion = 1.0; //comienza sin aceleracion
    private static final double MIN_INTERVALO = 1.5; //tiempo minimo entre zombies
    private static final double TIEMPO_ACELERACION = 40; //cada 40 segundos
    private static final double FACTOR_REDUCCION = 0.75;


    // Constructor
    public Tablero(Image fondo, Image roseBlade, Image wallNut, Image plantaExplosiva, Image tumba,
                   Image zombie, Image zombieFast, Image zombieSlow, Image zombieShooter, Image zombieBoss,
                   Image proyectil, Image proyectilZombie, Image regalo, Image explosion) {

        this.roseBlades = new RoseBlade[MAX_PLANTAS_POR_TIPO];
        this.wallNuts = new WallNut[MAX_PLANTAS_POR_TIPO];
        this.plantasExplosivas = new PlantaExplosiva[MAX_PLANTAS_EXPLOSIVAS];
        this.tumbas = new Tumba[MAX_TUMBAS];

        this.zombies = new Zombie[MAX_ZOMBIES_COMUNES];
        this.zombiesFast = new ZombieFast[MAX_ZOMBIES_RAPIDOS];
        this.zombiesSlow = new ZombieSlow[MAX_ZOMBIES_LENTOS];
        this.zombiesShooter = new ZombieShooter[MAX_ZOMBIES_SHOOTERS];
        this.zombiesBoss = new ZombieBoss[MAX_ZOMBIES_JEFES];

        this.proyectiles = new Proyectil[MAX_PROYECTILES_PLANTA];
        this.proyectilesZombies = new ProyectilZombie[MAX_PROYECTILES_ZOMBIE];
        this.explosiones = new Explosion[MAX_EXPLOSIONES];
        this.regalos = new Regalo[NUM_FILAS];

        this.imgFondoPasto = fondo;
        this.imgRoseBlade = roseBlade; this.imgWallNut = wallNut; this.imgPlantaExplosiva = plantaExplosiva; this.imgTumba = tumba;
        this.imgZombie = zombie; this.imgZombieFast = zombieFast; this.imgZombieSlow = zombieSlow; this.imgZombieShooter = zombieShooter; this.imgZombieBoss = zombieBoss;
        this.imgProyectil = proyectil; this.imgProyectilZombie = proyectilZombie; this.imgRegalo = regalo;
        this.imgExplosion = explosion;

        this.score = 0; this.juegoTerminado = false; this.random = new Random();
        this.tiempoHastaProximoZombie = 3.0;
        this.juegoGanado = false;
        this.tiempoDeJuego = 0; this.zombiesEliminados = 0; this.bossSpawned = false;

        this.inicializarRegalosFijos();
    }

    private void inicializarRegalosFijos() {
        final double X_COLUMNA = 50;
        final double INICIO_Y = INICIO_Y_GRILLA + (ALTO_CELDA / 2);
        for (int i = 0; i < NUM_FILAS; i++) {
            double yPos = INICIO_Y + (i * ALTO_CELDA);
            this.regalos[i] = new Regalo(X_COLUMNA, yPos, this.imgRegalo);
        }
    }

    // METODOS DEL TICK

    public void actualizar() {
        if (this.juegoTerminado || this.juegoGanado) return;
        this.tiempoDeJuego += 1.0 / 60.0;

        this.actualizarPlantas();
        this.actualizarZombies();
        this.actualizarExplosiones();
        this.actualizarProyectiles();
        this.generarEnemigos();
        this.resetearColisionesZombies();
        this.gestionarColisiones(); // Este método ahora setea las colisiones
        this.moverZombies(); // Mueve solo si no está colisionando
        this.limpiarEntidades();
    }

    private int contarZombies() {
        int contador = 0;
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) contador++; }
        for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null) contador++; }
        for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null) contador++; }
        for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null) contador++; }
        return contador;
    }

    private void spawnBoss() { if (this.zombiesBoss[0] == null) { this.zombiesBoss[0] = new ZombieBoss(900, this.imgZombieBoss); this.bossSpawned = true; } }

    private void generarEnemigos() {
        if (this.bossSpawned) { return; }
        
        if (this.zombiesEliminados >= ENEMIGOS_PARA_JEFE) { spawnBoss(); return; }
        
        this.tiempoTranscurrido += 1.0 / 60.0;

        // Cada 40 segundos reduce el tiempo de spawn un 25%
        if (this.tiempoTranscurrido >= TIEMPO_ACELERACION) {
            this.tiempoTranscurrido = 0; // reinicia el contador
            this.factorAceleracion *= FACTOR_REDUCCION;

            // Calcula el nuevo intervalo base, con mínimo de 1.5 segundos
            double nuevoIntervalo = INTERVALO_ZOMBIE * this.factorAceleracion;
            if (nuevoIntervalo < MIN_INTERVALO)
                this.factorAceleracion = MIN_INTERVALO / INTERVALO_ZOMBIE;
        }
        this.tiempoHastaProximoZombie -= 1.0 / 60.0;
        if (this.tiempoHastaProximoZombie <= 0 && this.contarZombies() < MAX_ZOMBIES_TOTAL) {
            final double INICIO_Y = INICIO_Y_GRILLA + (ALTO_CELDA / 2);
            double ySpawn = INICIO_Y + (this.random.nextInt(NUM_FILAS) * ALTO_CELDA);
            double xSpawn = 850;
            double chance = this.random.nextDouble();
            if (chance < 0.40) { double vel = (this.random.nextDouble() * 0.5) + 0.8; for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] == null) { this.zombies[i] = new Zombie(xSpawn, ySpawn, this.imgZombie, vel); break; } } }
            else if (chance < 0.60) { for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] == null) { this.zombiesFast[i] = new ZombieFast(xSpawn, ySpawn, this.imgZombieFast); break; } } }
            else if (chance < 0.80) { for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] == null) { this.zombiesSlow[i] = new ZombieSlow(xSpawn, ySpawn, this.imgZombieSlow); break; } } }
            else { for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] == null) { this.zombiesShooter[i] = new ZombieShooter(xSpawn, ySpawn, this.imgZombieShooter); break; } } }
            double intervaloActual = INTERVALO_ZOMBIE * this.factorAceleracion;
            if (intervaloActual < MIN_INTERVALO)
                intervaloActual = MIN_INTERVALO;

            this.tiempoHastaProximoZombie = intervaloActual * (0.8 + this.random.nextDouble() * 0.4);
        }
    }

    private void agregarProyectil(Proyectil p) { for (int i = 0; i < this.proyectiles.length; i++) { if (this.proyectiles[i] == null) { this.proyectiles[i] = p; return; } } }
    private void agregarProyectilZombie(ProyectilZombie pz) { for (int i = 0; i < this.proyectilesZombies.length; i++) { if (this.proyectilesZombies[i] == null) { this.proyectilesZombies[i] = pz; return; } } }

    private void actualizarPlantas() {
         for (int i = 0; i < this.roseBlades.length; i++) { if (this.roseBlades[i] != null) { RoseBlade rb = this.roseBlades[i]; rb.actualizar(this.zombies, this.zombiesFast, this.zombiesSlow, this.zombiesShooter, this.zombiesBoss); if (rb.puedeDisparar()) { Proyectil np = rb.disparar(this.imgProyectil); if (np != null) { this.agregarProyectil(np); } } } }
         for (int i = 0; i < this.wallNuts.length; i++) { if (this.wallNuts[i] != null) { this.wallNuts[i].actualizar(null); } }
         for (int i = 0; i < this.plantasExplosivas.length; i++) { if (this.plantasExplosivas[i] != null) { this.plantasExplosivas[i].actualizar(null); } }
    }

    private void actualizarProyectiles() { for (int i = 0; i < this.proyectiles.length; i++) { if (this.proyectiles[i] != null) this.proyectiles[i].actualizar(); } for (int i = 0; i < this.proyectilesZombies.length; i++) { if (this.proyectilesZombies[i] != null) this.proyectilesZombies[i].actualizar(); } }
    private void actualizarExplosiones() { for (int i = 0; i < this.explosiones.length; i++) { if (this.explosiones[i] != null) { this.explosiones[i].actualizar(); } } }

    private void actualizarZombies() {
         for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) { this.zombies[i].actualizar(null, null); } }
         for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null) { this.zombiesFast[i].actualizar(null, null); } }
         for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null) { this.zombiesSlow[i].actualizar(null, null); } }
         for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null) { ZombieShooter zs = this.zombiesShooter[i]; zs.actualizar(this.roseBlades, this.wallNuts); if (zs.puedeDisparar()) { ProyectilZombie pz = zs.disparar(this.imgProyectilZombie); if (pz != null) { this.agregarProyectilZombie(pz); } } } }
         for (int i = 0; i < this.zombiesBoss.length; i++) { if (this.zombiesBoss[i] != null) { this.zombiesBoss[i].actualizar(null, null); } }
    }

    private void resetearColisionesZombies() {
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) this.zombies[i].setEstaColisionando(false); }
        for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null) this.zombiesFast[i].setEstaColisionando(false); }
        for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null) this.zombiesSlow[i].setEstaColisionando(false); }
        for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null) this.zombiesShooter[i].setEstaColisionando(false); }
        for (int i = 0; i < this.zombiesBoss.length; i++) { if (this.zombiesBoss[i] != null) this.zombiesBoss[i].setEstaColisionando(false); }
    }

    private void moverZombies() {
         for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) { this.zombies[i].intentarMover(); } }
         for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null) { this.zombiesFast[i].intentarMover(); } }
         for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null) { this.zombiesSlow[i].intentarMover(); } }
         for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null) { this.zombiesShooter[i].intentarMover(); } }
         for (int i = 0; i < this.zombiesBoss.length; i++) { if (this.zombiesBoss[i] != null) { this.zombiesBoss[i].intentarMover(); } }
    }


    private void gestionarColisiones() {
        // 1. Bala de PLANTA vs (Tumbas Y Zombies)
        for (int i = 0; i < this.proyectiles.length; i++) {
            if (this.proyectiles[i] == null) continue; Proyectil p = this.proyectiles[i]; boolean balaChoco = false;
            for (int j = 0; j < this.tumbas.length; j++) { if (this.tumbas[j] != null && colisionEntidades(p.getPosicion(), p.getAncho(), p.getAlto(), this.tumbas[j].getPosicion(), this.tumbas[j].getAncho(), this.tumbas[j].getAlto())) { this.tumbas[j].recibirDano(p.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectiles[i] = null; continue; }
            for (int j = 0; j < this.zombies.length; j++) { if (this.zombies[j] != null && colisionEntidades(p.getPosicion(), p.getAncho(), p.getAlto(), this.zombies[j].getPosicion(), this.zombies[j].getAncho(), this.zombies[j].getAlto())) { this.zombies[j].recibirDano(p.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectiles[i] = null; this.score += 10; continue; }
            for (int j = 0; j < this.zombiesFast.length; j++) { if (this.zombiesFast[j] != null && colisionEntidades(p.getPosicion(), p.getAncho(), p.getAlto(), this.zombiesFast[j].getPosicion(), this.zombiesFast[j].getAncho(), this.zombiesFast[j].getAlto())) { this.zombiesFast[j].recibirDano(p.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectiles[i] = null; this.score += 10; continue; }
            for (int j = 0; j < this.zombiesSlow.length; j++) { if (this.zombiesSlow[j] != null && colisionEntidades(p.getPosicion(), p.getAncho(), p.getAlto(), this.zombiesSlow[j].getPosicion(), this.zombiesSlow[j].getAncho(), this.zombiesSlow[j].getAlto())) { this.zombiesSlow[j].recibirDano(p.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectiles[i] = null; this.score += 10; continue; }
            for (int j = 0; j < this.zombiesShooter.length; j++) { if (this.zombiesShooter[j] != null && colisionEntidades(p.getPosicion(), p.getAncho(), p.getAlto(), this.zombiesShooter[j].getPosicion(), this.zombiesShooter[j].getAncho(), this.zombiesShooter[j].getAlto())) { this.zombiesShooter[j].recibirDano(p.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectiles[i] = null; this.score += 10; continue; }
            for (int j = 0; j < this.zombiesBoss.length; j++) { if (this.zombiesBoss[j] != null && colisionEntidades(p.getPosicion(), p.getAncho(), p.getAlto(), this.zombiesBoss[j].getPosicion(), this.zombiesBoss[j].getAncho(), this.zombiesBoss[j].getAlto())) { this.zombiesBoss[j].recibirDano(p.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectiles[i] = null; this.score += 50; continue; }
        }

        // 2. Zombies vs Regalo
        for (int j = 0; j < this.regalos.length; j++) {
             Regalo r = this.regalos[j]; if (r == null) continue;
             for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null && colisionEntidades(this.zombies[i].getPosicion(), this.zombies[i].getAncho(), this.zombies[i].getAlto(), r.getPosicion(), r.getAncho(), r.getAlto())) { this.juegoTerminado = true; return; } }
             for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null && colisionEntidades(this.zombiesFast[i].getPosicion(), this.zombiesFast[i].getAncho(), this.zombiesFast[i].getAlto(), r.getPosicion(), r.getAncho(), r.getAlto())) { this.juegoTerminado = true; return; } }
             for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null && colisionEntidades(this.zombiesSlow[i].getPosicion(), this.zombiesSlow[i].getAncho(), this.zombiesSlow[i].getAlto(), r.getPosicion(), r.getAncho(), r.getAlto())) { this.juegoTerminado = true; return; } }
             for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null && colisionEntidades(this.zombiesShooter[i].getPosicion(), this.zombiesShooter[i].getAncho(), this.zombiesShooter[i].getAlto(), r.getPosicion(), r.getAncho(), r.getAlto())) { this.juegoTerminado = true; return; } }
             for (int i = 0; i < this.zombiesBoss.length; i++) { if (this.zombiesBoss[i] != null && colisionEntidades(this.zombiesBoss[i].getPosicion(), this.zombiesBoss[i].getAncho(), this.zombiesBoss[i].getAlto(), r.getPosicion(), r.getAncho(), r.getAlto())) { this.juegoTerminado = true; return; } }
        }

        // 3. Colisión y Ataque Melee Zombies vs Obstáculos
        gestionarColisionYAtaqueZombieObstaculo();

        // 4. Bala de ZOMBIE vs (Tumbas y Plantas)
        for (int i = 0; i < this.proyectilesZombies.length; i++) {
            if (this.proyectilesZombies[i] == null) continue; ProyectilZombie pz = this.proyectilesZombies[i]; boolean balaChoco = false;
            for (int j = 0; j < this.tumbas.length; j++) { if (this.tumbas[j] != null && colisionEntidades(pz.getPosicion(), pz.getAncho(), pz.getAlto(), this.tumbas[j].getPosicion(), this.tumbas[j].getAncho(), this.tumbas[j].getAlto())) { balaChoco = true; break; } } if (balaChoco) { this.proyectilesZombies[i] = null; continue; }
            for (int j = 0; j < this.roseBlades.length; j++) { if (this.roseBlades[j] != null && colisionEntidades(pz.getPosicion(), pz.getAncho(), pz.getAlto(), this.roseBlades[j].getPosicion(), this.roseBlades[j].getAncho(), this.roseBlades[j].getAlto())) { this.roseBlades[j].recibirDano(pz.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectilesZombies[i] = null; continue; }
            for (int j = 0; j < this.wallNuts.length; j++) { if (this.wallNuts[j] != null && colisionEntidades(pz.getPosicion(), pz.getAncho(), pz.getAlto(), this.wallNuts[j].getPosicion(), this.wallNuts[j].getAncho(), this.wallNuts[j].getAlto())) { this.wallNuts[j].recibirDano(pz.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectilesZombies[i] = null; continue; }
            for (int j = 0; j < this.plantasExplosivas.length; j++) { if (this.plantasExplosivas[j] != null && colisionEntidades(pz.getPosicion(), pz.getAncho(), pz.getAlto(), this.plantasExplosivas[j].getPosicion(), this.plantasExplosivas[j].getAncho(), this.plantasExplosivas[j].getAlto())) { this.plantasExplosivas[j].recibirDano(pz.getDano()); balaChoco = true; break; } } if (balaChoco) { this.proyectilesZombies[i] = null; continue; }
        }
    }

    // --- MÉTODOS DE AYUDA (HELPER) ---

    private boolean colisionEntidades(Punto p1, double a1, double h1, Punto p2, double a2, double h2) { return ObjetoDeJuego.colisionan(p1, a1, h1, p2, a2, h2); }

    /**
     * Gestiona la colisión y el ataque melee de TODOS los zombies contra TODOS los obstáculos.
     */
    private void gestionarColisionYAtaqueZombieObstaculo() {

        // --- 1. ZOMBIES NORMALES vs OBSTACULOS ---
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) chequearColisionYAtaqueZombieNormal(this.zombies[i]); }
        for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null) chequearColisionYAtaqueZombieNormal(this.zombiesFast[i]); }
        for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null) chequearColisionYAtaqueZombieNormal(this.zombiesSlow[i]); }
        for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null) chequearColisionYAtaqueZombieNormal(this.zombiesShooter[i]); }

        // --- 2. JEFE vs OBSTACULOS ---
        for (int i = 0; i < this.zombiesBoss.length; i++) {
            if (this.zombiesBoss[i] == null) continue;
            ZombieBoss boss = this.zombiesBoss[i];
            boolean jefeChoco = false;
            boolean jefeAtaco = false;

            for (int j = 0; j < this.roseBlades.length; j++) { if (this.roseBlades[j] != null && colisionEntidades(boss.getPosicion(), boss.getAncho(), boss.getAlto(), this.roseBlades[j].getPosicion(), this.roseBlades[j].getAncho(), this.roseBlades[j].getAlto())) { jefeChoco = true; if (boss.puedeAtacar()) { this.roseBlades[j].recibirDano(boss.getAtaque()); jefeAtaco = true; } } }
            for (int j = 0; j < this.wallNuts.length; j++) { if (this.wallNuts[j] != null && colisionEntidades(boss.getPosicion(), boss.getAncho(), boss.getAlto(), this.wallNuts[j].getPosicion(), this.wallNuts[j].getAncho(), this.wallNuts[j].getAlto())) { jefeChoco = true; if (boss.puedeAtacar()) { this.wallNuts[j].recibirDano(boss.getAtaque()); jefeAtaco = true; } } }
            for (int j = 0; j < this.plantasExplosivas.length; j++) { if (this.plantasExplosivas[j] != null && colisionEntidades(boss.getPosicion(), boss.getAncho(), boss.getAlto(), this.plantasExplosivas[j].getPosicion(), this.plantasExplosivas[j].getAncho(), this.plantasExplosivas[j].getAlto())) { jefeChoco = true; this.plantasExplosivas[j].recibirDano(1); jefeAtaco = true; } }
            for (int j = 0; j < this.tumbas.length; j++) { if (this.tumbas[j] != null && colisionEntidades(boss.getPosicion(), boss.getAncho(), boss.getAlto(), this.tumbas[j].getPosicion(), this.tumbas[j].getAncho(), this.tumbas[j].getAlto())) { jefeChoco = true; if (boss.puedeAtacar()) { this.tumbas[j].recibirDano(boss.getAtaque()); jefeAtaco = true; } } }

            boss.setEstaColisionando(jefeChoco);
            if (jefeAtaco) { boss.reiniciarCooldownAtaque(); }
        }
    }

     /**
      * Chequea colisión y si la encuentra, setea flag Y ataca.
      */
     private void chequearColisionYAtaqueZombieNormal(Object z) {
        double zX=0, zY=0, zAncho=0, zAlto=0;
        boolean puedeAtacar = false;
        int ataque = 0;

        if (z instanceof Zombie) { Zombie zc = (Zombie)z; zX=zc.getX(); zY=zc.getY(); zAncho=zc.getAncho(); zAlto=zc.getAlto(); puedeAtacar=zc.puedeAtacar(); ataque=zc.getAtaque(); }
        else if (z instanceof ZombieFast) { ZombieFast zf = (ZombieFast)z; zX=zf.getX(); zY=zf.getY(); zAncho=zf.getAncho(); zAlto=zf.getAlto(); puedeAtacar=zf.puedeAtacar(); ataque=zf.getAtaque(); }
        else if (z instanceof ZombieSlow) { ZombieSlow zs = (ZombieSlow)z; zX=zs.getX(); zY=zs.getY(); zAncho=zs.getAncho(); zAlto=zs.getAlto(); puedeAtacar=zs.puedeAtacar(); ataque=zs.getAtaque(); }
        else if (z instanceof ZombieShooter) { ZombieShooter zsh = (ZombieShooter)z; zX=zsh.getX(); zY=zsh.getY(); zAncho=zsh.getAncho(); zAlto=zsh.getAlto(); puedeAtacar=zsh.puedeAtacar(); ataque=zsh.getAtaque(); }
        else return;

        Punto zPos = new Punto(zX, zY);

        for (int i = 0; i < this.roseBlades.length; i++) {
            if (this.roseBlades[i] != null && colisionEntidades(zPos, zAncho, zAlto, this.roseBlades[i].getPosicion(), this.roseBlades[i].getAncho(), this.roseBlades[i].getAlto())) {
                setColisionandoZombie(z, true); if (puedeAtacar) { this.roseBlades[i].recibirDano(ataque); reiniciarCooldownZombie(z); } return;
            }
        }
        for (int i = 0; i < this.wallNuts.length; i++) {
            if (this.wallNuts[i] != null && colisionEntidades(zPos, zAncho, zAlto, this.wallNuts[i].getPosicion(), this.wallNuts[i].getAncho(), this.wallNuts[i].getAlto())) {
                setColisionandoZombie(z, true); if (puedeAtacar) { this.wallNuts[i].recibirDano(ataque); reiniciarCooldownZombie(z); } return;
            }
        }
        for (int i = 0; i < this.plantasExplosivas.length; i++) {
            if (this.plantasExplosivas[i] != null && colisionEntidades(zPos, zAncho, zAlto, this.plantasExplosivas[i].getPosicion(), this.plantasExplosivas[i].getAncho(), this.plantasExplosivas[i].getAlto())) {
                setColisionandoZombie(z, true); this.plantasExplosivas[i].recibirDano(1); if (puedeAtacar) { reiniciarCooldownZombie(z); } return;
            }
        }
        for (int i = 0; i < this.tumbas.length; i++) {
            if (this.tumbas[i] != null && colisionEntidades(zPos, zAncho, zAlto, this.tumbas[i].getPosicion(), this.tumbas[i].getAncho(), this.tumbas[i].getAlto())) {
                setColisionandoZombie(z, true); if (puedeAtacar) { this.tumbas[i].recibirDano(ataque); reiniciarCooldownZombie(z); } return;
            }
        }
        // Si no colisionó con nada, el flag ya fue reseteado al inicio del tick
     }

     private void reiniciarCooldownZombie(Object z) { if (z instanceof Zombie) ((Zombie) z).reiniciarCooldownAtaque(); else if (z instanceof ZombieFast) ((ZombieFast) z).reiniciarCooldownAtaque(); else if (z instanceof ZombieSlow) ((ZombieSlow) z).reiniciarCooldownAtaque(); else if (z instanceof ZombieShooter) ((ZombieShooter) z).reiniciarCooldownAtaque(); else if (z instanceof ZombieBoss) ((ZombieBoss) z).reiniciarCooldownAtaque(); }
     private void setColisionandoZombie(Object z, boolean valor) { if (z instanceof Zombie) ((Zombie) z).setEstaColisionando(valor); else if (z instanceof ZombieFast) ((ZombieFast) z).setEstaColisionando(valor); else if (z instanceof ZombieSlow) ((ZombieSlow) z).setEstaColisionando(valor); else if (z instanceof ZombieShooter) ((ZombieShooter) z).setEstaColisionando(valor); else if (z instanceof ZombieBoss) ((ZombieBoss) z).setEstaColisionando(valor); }

     private void aplicarDanoExplosivo(double x, double y) {
        Punto pExplosion = new Punto(x, y);
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null && colisionEntidades(pExplosion, ANCHO_EXPLOSION, ALTO_EXPLOSION, this.zombies[i].getPosicion(), this.zombies[i].getAncho(), this.zombies[i].getAlto())) { this.zombies[i].recibirDano(DANO_EXPLOSION); } }
        for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null && colisionEntidades(pExplosion, ANCHO_EXPLOSION, ALTO_EXPLOSION, this.zombiesFast[i].getPosicion(), this.zombiesFast[i].getAncho(), this.zombiesFast[i].getAlto())) { this.zombiesFast[i].recibirDano(DANO_EXPLOSION); } }
        for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null && colisionEntidades(pExplosion, ANCHO_EXPLOSION, ALTO_EXPLOSION, this.zombiesSlow[i].getPosicion(), this.zombiesSlow[i].getAncho(), this.zombiesSlow[i].getAlto())) { this.zombiesSlow[i].recibirDano(DANO_EXPLOSION); } }
        for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null && colisionEntidades(pExplosion, ANCHO_EXPLOSION, ALTO_EXPLOSION, this.zombiesShooter[i].getPosicion(), this.zombiesShooter[i].getAncho(), this.zombiesShooter[i].getAlto())) { this.zombiesShooter[i].recibirDano(DANO_EXPLOSION); } }
        for (int i = 0; i < this.zombiesBoss.length; i++) { if (this.zombiesBoss[i] != null && colisionEntidades(pExplosion, ANCHO_EXPLOSION, ALTO_EXPLOSION, this.zombiesBoss[i].getPosicion(), this.zombiesBoss[i].getAncho(), this.zombiesBoss[i].getAlto())) { this.zombiesBoss[i].recibirDano(DANO_EXPLOSION); } }
    }
    private void agregarExplosion(double x, double y) { for (int i = 0; i < this.explosiones.length; i++) { if (this.explosiones[i] == null) { this.explosiones[i] = new Explosion(x, y, this.imgExplosion); return; } } }
    private void agregarTumba(double x, double y) {
        if (getPlantaEnCoordenadas(x, y) != null || getTumbaEnCoordenadas(x, y) != null) { return; }
        double xCelda = Math.floor(x / ANCHO_CELDA) * ANCHO_CELDA + (ANCHO_CELDA / 2);
        double yCelda = Math.floor((y - INICIO_Y_GRILLA) / ALTO_CELDA) * ALTO_CELDA + INICIO_Y_GRILLA + (ALTO_CELDA / 2);
        if (xCelda < ANCHO_CELDA * 1.5) { xCelda = ANCHO_CELDA * 1.5; }
        for (int i = 0; i < this.tumbas.length; i++) { if (this.tumbas[i] == null) { this.tumbas[i] = new Tumba(xCelda, yCelda, this.imgTumba); return; } }
    }

    // Limpiar Entidades
    private void limpiarEntidades() {
        // 1 Zombies muertos
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null && this.zombies[i].estaMuerto()) { if (random.nextDouble() < CHANCE_TUMBA) { agregarTumba(this.zombies[i].getX(), this.zombies[i].getY()); } this.zombies[i] = null; this.zombiesEliminados++; } }
        for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null && this.zombiesFast[i].estaMuerto()) { if (random.nextDouble() < CHANCE_TUMBA) { agregarTumba(this.zombiesFast[i].getX(), this.zombiesFast[i].getY()); } this.zombiesFast[i] = null; this.zombiesEliminados++; } }
        for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null && this.zombiesSlow[i].estaMuerto()) { if (random.nextDouble() < CHANCE_TUMBA) { agregarTumba(this.zombiesSlow[i].getX(), this.zombiesSlow[i].getY()); } this.zombiesSlow[i] = null; this.zombiesEliminados++; } }
        for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null && this.zombiesShooter[i].estaMuerto()) { if (random.nextDouble() < CHANCE_TUMBA) { agregarTumba(this.zombiesShooter[i].getX(), this.zombiesShooter[i].getY()); } this.zombiesShooter[i] = null; this.zombiesEliminados++; } }
        for (int i = 0; i < this.zombiesBoss.length; i++) { if (this.zombiesBoss[i] != null && this.zombiesBoss[i].estaMuerto()) { this.zombiesBoss[i] = null; this.zombiesEliminados++; this.score += 1000; } }

        // 2 balas fuera
        for (int i = 0; i < this.proyectiles.length; i++) { if (this.proyectiles[i] != null) { double x = this.proyectiles[i].getX(); if (x > 850 || x < -50) this.proyectiles[i] = null; } }
        for (int i = 0; i < this.proyectilesZombies.length; i++) { if (this.proyectilesZombies[i] != null) { double x = this.proyectilesZombies[i].getX(); if (x > 850 || x < -50) this.proyectilesZombies[i] = null; } }

        // 3 plantas muertas
        for (int i = 0; i < this.roseBlades.length; i++) { if (this.roseBlades[i] != null && this.roseBlades[i].estaMuerta()) { this.roseBlades[i] = null; } }
        for (int i = 0; i < this.wallNuts.length; i++) { if (this.wallNuts[i] != null && this.wallNuts[i].estaMuerta()) { this.wallNuts[i] = null; } }

        // 4 Limpieza de plantas explosivas
        for (int i = 0; i < this.plantasExplosivas.length; i++) { if (this.plantasExplosivas[i] != null && this.plantasExplosivas[i].estaMuerta()) { aplicarDanoExplosivo(this.plantasExplosivas[i].getX(), this.plantasExplosivas[i].getY()); agregarExplosion(this.plantasExplosivas[i].getX(), this.plantasExplosivas[i].getY()); this.plantasExplosivas[i] = null; } }

        // 5 Limpieza de animaciones de explosión
        for (int i = 0; i < this.explosiones.length; i++) { if (this.explosiones[i] != null && this.explosiones[i].haTerminado()) { this.explosiones[i] = null; } }

        // 6 Limpieza de Tumbas
        for (int i = 0; i < this.tumbas.length; i++) { if (this.tumbas[i] != null && this.tumbas[i].estaDestruida()) { this.tumbas[i] = null; } }

        // 7 chequea si ganaste
        if (!this.juegoGanado && this.bossSpawned) { boolean bossVivo = false; for (int i = 0; i < this.zombiesBoss.length; i++) { if (this.zombiesBoss[i] != null) { bossVivo = true; break; } } if (!bossVivo) { this.juegoGanado = true; } }
    }

    // Dibujar
    public void dibujar(Entorno e) {
        // Fondo
        if (this.imgFondoPasto != null) { int ta = imgFondoPasto.getWidth(null); int th = imgFondoPasto.getHeight(null); if (ta > 0 && th > 0) { for (int y = 0; y < e.alto(); y += th) { for (int x = 0; x < e.ancho(); x += ta) { e.dibujarImagen(imgFondoPasto, x + ta / 2.0, y + th / 2.0, 0); } } } else { e.dibujarRectangulo(e.ancho()/2, e.alto()/2, e.ancho(), e.alto(), 0, Color.GRAY); } }
        else { e.dibujarRectangulo(e.ancho()/2, e.alto()/2, e.ancho(), e.alto(), 0, Color.DARK_GRAY); }

        // Entidades
        for (int i = 0; i < this.roseBlades.length; i++) { if (this.roseBlades[i] != null) this.roseBlades[i].dibujar(e); }
        for (int i = 0; i < this.wallNuts.length; i++) { if (this.wallNuts[i] != null) this.wallNuts[i].dibujar(e); }
        for (int i = 0; i < this.plantasExplosivas.length; i++) { if (this.plantasExplosivas[i] != null) this.plantasExplosivas[i].dibujar(e); }
        for (int i = 0; i < this.tumbas.length; i++) { if (this.tumbas[i] != null) this.tumbas[i].dibujar(e); }
        for (int i = 0; i < this.zombies.length; i++) { if (this.zombies[i] != null) this.zombies[i].dibujar(e); }
        for (int i = 0; i < this.zombiesFast.length; i++) { if (this.zombiesFast[i] != null) this.zombiesFast[i].dibujar(e); }
        for (int i = 0; i < this.zombiesSlow.length; i++) { if (this.zombiesSlow[i] != null) this.zombiesSlow[i].dibujar(e); }
        for (int i = 0; i < this.zombiesShooter.length; i++) { if (this.zombiesShooter[i] != null) this.zombiesShooter[i].dibujar(e); }
        for (int i = 0; i < this.zombiesBoss.length; i++) { if (this.zombiesBoss[i] != null) this.zombiesBoss[i].dibujar(e); }
        for (int i = 0; i < this.proyectiles.length; i++) { if (this.proyectiles[i] != null) this.proyectiles[i].dibujar(e); }
        for (int i = 0; i < this.proyectilesZombies.length; i++) { if (this.proyectilesZombies[i] != null) this.proyectilesZombies[i].dibujar(e); }
        for (int i = 0; i < this.regalos.length; i++) { if (this.regalos[i] != null) this.regalos[i].dibujar(e); }
        for (int i = 0; i < this.explosiones.length; i++) { if (this.explosiones[i] != null) this.explosiones[i].dibujar(e); }
    }

    // --- MÉTODOS DE INTERACCIÓN ---

    public Object getObjetoEnCoordenadas(double x, double y) { Object planta = getPlantaEnCoordenadas(x, y); if (planta != null) return planta; Tumba tumba = getTumbaEnCoordenadas(x, y); if (tumba != null) return tumba; return null; }
    public Object getPlantaEnCoordenadas(double x, double y) { for(int i = 0; i < this.roseBlades.length; i++) { if (this.roseBlades[i] != null && colisionPuntoRect(x, y, this.roseBlades[i].getX(), this.roseBlades[i].getY(), this.roseBlades[i].getAncho(), this.roseBlades[i].getAlto())) return this.roseBlades[i]; } for(int i = 0; i < this.wallNuts.length; i++) { if (this.wallNuts[i] != null && colisionPuntoRect(x, y, this.wallNuts[i].getX(), this.wallNuts[i].getY(), this.wallNuts[i].getAncho(), this.wallNuts[i].getAlto())) return this.wallNuts[i]; } for(int i = 0; i < this.plantasExplosivas.length; i++) { if (this.plantasExplosivas[i] != null && colisionPuntoRect(x, y, this.plantasExplosivas[i].getX(), this.plantasExplosivas[i].getY(), this.plantasExplosivas[i].getAncho(), this.plantasExplosivas[i].getAlto())) return this.plantasExplosivas[i]; } return null; }
    public Tumba getTumbaEnCoordenadas(double x, double y) { for(int i = 0; i < this.tumbas.length; i++) { if (this.tumbas[i] != null && colisionPuntoRect(x, y, this.tumbas[i].getX(), this.tumbas[i].getY(), this.tumbas[i].getAncho(), this.tumbas[i].getAlto())) return this.tumbas[i]; } return null; }
    private boolean colisionPuntoRect(double px, double py, double rx, double ry, double rancho, double ralto) { return px >= rx - rancho / 2 && px <= rx + rancho / 2 && py >= ry - ralto / 2 && py <= ry + ralto / 2; }
    public boolean estaOcupada(double x, double y) { return getObjetoEnCoordenadas(x, y) != null; }

    // CORREGIDO: Lógica para bloquear movimiento a la primera columna
    public boolean estaOcupadaPorOtroObjeto(double x, double y, Object objetoAExcluir) {
        // 1. Check si es la primera columna (la de los regalos)
        if (x < ANCHO_CELDA) {
            return true; // Bloquea movimiento a la primera columna
        }
        // 2. Check si hay otra planta o tumba (lógica anterior)
        Object objetoEnCelda = getObjetoEnCoordenadas(x, y);
        if (objetoEnCelda != null && objetoEnCelda != objetoAExcluir) {
            return true; // Ocupada por otra planta/tumba
        }
        // 3. Si no es la primera columna Y no hay otra planta/tumba, está libre
        return false;
    }

    public boolean agregarRoseBlade(double x, double y, Image imagen) { if (estaOcupada(x, y)) return false; for (int i = 0; i < this.roseBlades.length; i++) { if (this.roseBlades[i] == null) { this.roseBlades[i] = new RoseBlade(x, y, this.imgRoseBlade); return true; } } return false; }
    public boolean agregarWallNut(double x, double y, Image imagen) { if (estaOcupada(x, y)) return false; for (int i = 0; i < this.wallNuts.length; i++) { if (this.wallNuts[i] == null) { this.wallNuts[i] = new WallNut(x, y, this.imgWallNut); return true; } } return false; }
    public boolean agregarPlantaExplosiva(double x, double y, Image imagen) { if (estaOcupada(x, y)) return false; for (int i = 0; i < this.plantasExplosivas.length; i++) { if (this.plantasExplosivas[i] == null) { this.plantasExplosivas[i] = new PlantaExplosiva(x, y, this.imgPlantaExplosiva); return true; } } return false; }

    // GETTERS
    public int getZombiesEliminados() { return this.zombiesEliminados; }
    public int getEnemigosRestantes() { if (this.bossSpawned) { int bossCount = 0; for (int i=0; i < zombiesBoss.length; i++) { if(zombiesBoss[i]!=null) bossCount++; } return bossCount; } return Math.max(0, ENEMIGOS_PARA_JEFE - this.zombiesEliminados); }
    public double getTiempoDeJuego() { return this.tiempoDeJuego; }
    public boolean isJuegoTerminado() { return this.juegoTerminado; }
    public boolean isJuegoGanado() { return this.juegoGanado; }
    public int getScore() { return this.score; }
}