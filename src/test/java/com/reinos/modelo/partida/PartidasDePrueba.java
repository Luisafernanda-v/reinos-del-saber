package com.reinos.modelo.partida;

import com.reinos.modelo.jugador.DificultadCPU;
import com.reinos.modelo.jugador.Jugador;
import com.reinos.modelo.mapa.CargadorMapa;
import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.mapa.Mapa;
import com.reinos.modelo.mapa.Pais;
import com.reinos.modelo.mapa.Terreno;
import com.reinos.modelo.retos.BancoRetos;
import com.reinos.modelo.retos.Nivel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * Utilidades compartidas por las pruebas que necesitan una partida.
 */
public final class PartidasDePrueba {

    /**
     * Generador que siempre devuelve 0: los sorteos son predecibles y la CPU
     * acierta todos los retos.
     */
    public static final RandomGenerator SIEMPRE_CERO = () -> 0L;

    private PartidasDePrueba() {
    }

    /**
     * Carga el mapa real del juego.
     *
     * @return un mapa nuevo de {@code mapa.txt}
     */
    public static Mapa mapaReal() {
        return CargadorMapa.cargar(recurso("/mapa.txt"));
    }

    /**
     * Carga un mapa escrito en el mismo formato de {@code mapa.txt}.
     *
     * @param texto filas del mapa
     * @return el mapa leído
     */
    public static Mapa mapa(String texto) {
        return CargadorMapa.cargar(new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Carga el banco con las preguntas reales.
     *
     * @return el banco cargado
     */
    public static BancoRetos bancoReal() {
        BancoRetos banco = new BancoRetos(new Random(1));
        banco.cargar(recurso("/retos/preguntas.csv"));
        return banco;
    }

    /**
     * Crea una configuración con 3 humanos de nivel fácil.
     *
     * @return la configuración
     */
    public static ConfiguracionPartida tresHumanos() {
        ConfiguracionPartida configuracion = new ConfiguracionPartida();
        configuracion.agregarHumano("Ana", Nivel.FACIL);
        configuracion.agregarHumano("Beto", Nivel.MEDIO);
        configuracion.agregarHumano("Caro", Nivel.DIFICIL);
        return configuracion;
    }

    /**
     * Crea una configuración con 1 humano y 2 puestos de CPU.
     *
     * @param dificultad dificultad de la CPU
     * @return la configuración
     */
    public static ConfiguracionPartida unHumano(DificultadCPU dificultad) {
        ConfiguracionPartida configuracion = new ConfiguracionPartida();
        configuracion.agregarHumano("Ana", Nivel.FACIL);
        configuracion.setDificultadCPU(dificultad);
        return configuracion;
    }

    /**
     * Crea e inicia una partida.
     *
     * @param configuracion jugadores
     * @param mapa          mapa de la partida
     * @param random        generador de la partida
     * @return la partida iniciada
     */
    public static Partida iniciada(ConfiguracionPartida configuracion, Mapa mapa, RandomGenerator random) {
        Partida partida = new Partida(configuracion, mapa, bancoReal(), random);
        partida.iniciar();
        return partida;
    }

    /**
     * Busca el jugador que representa un país.
     *
     * @param partida partida iniciada
     * @param pais    país buscado
     * @return el jugador de ese país
     */
    public static Jugador jugadorDe(Partida partida, Pais pais) {
        return partida.getJugadores().stream()
                .filter(jugador -> jugador.getPais() == pais)
                .findFirst()
                .orElseThrow();
    }

    /**
     * Busca una casilla atacable del terreno pedido que no sea sede.
     *
     * @param partida partida iniciada
     * @param terreno terreno buscado
     * @return la primera casilla que cumple las condiciones
     */
    public static Casilla atacableDe(Partida partida, Terreno terreno) {
        return partida.casillasAtacables().stream()
                .filter(casilla -> !casilla.esSede() && casilla.getTerreno() == terreno)
                .findFirst()
                .orElseThrow();
    }

    /**
     * Devuelve la posición de una opción incorrecta del reto actual.
     *
     * @param partida partida con un reto en curso
     * @return índice de una opción incorrecta
     */
    public static int incorrecta(Partida partida) {
        return (partida.getRetoActual().indiceCorrecto() + 1) % 4;
    }

    /**
     * Devuelve la posición de la opción correcta del reto actual.
     *
     * @param partida partida con un reto en curso
     * @return índice de la opción correcta
     */
    public static int correcta(Partida partida) {
        return partida.getRetoActual().indiceCorrecto();
    }

    private static InputStream recurso(String ruta) {
        return Objects.requireNonNull(PartidasDePrueba.class.getResourceAsStream(ruta), "Falta " + ruta);
    }
}
