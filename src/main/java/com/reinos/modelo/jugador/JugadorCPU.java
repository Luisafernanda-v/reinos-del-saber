package com.reinos.modelo.jugador;

import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.partida.Partida;

import java.util.random.RandomGenerator;

/**
 * Jugador controlado por el computador.
 * <p>
 * La CPU no lee las preguntas: cada reto lo acierta o lo falla al azar,
 * según la probabilidad de su dificultad. La casilla que ataca la decide su
 * estrategia.
 * </p>
 */
public class JugadorCPU extends Jugador {

    /**
     * Dificultad que define la probabilidad de acierto.
     */
    private final DificultadCPU dificultad;

    /**
     * Estrategia con la que elige qué casilla atacar.
     */
    private final EstrategiaCPU estrategia;

    /**
     * Crea un jugador CPU.
     *
     * @param nombre     nombre del jugador, por ejemplo {@code CPU 1}
     * @param dificultad dificultad de la CPU
     * @param estrategia estrategia para elegir casillas
     * @throws IllegalArgumentException si el nombre está vacío
     */
    public JugadorCPU(String nombre, DificultadCPU dificultad, EstrategiaCPU estrategia) {
        super(nombre);
        this.dificultad = dificultad;
        this.estrategia = estrategia;
    }

    /**
     * Decide al azar si la CPU acierta un reto.
     *
     * @param random generador aleatorio de la partida
     * @return {@code true} si {@code random.nextDouble()} es menor que la
     *         probabilidad de acierto
     */
    public boolean simularRespuesta(RandomGenerator random) {
        return random.nextDouble() < dificultad.getProbabilidadAcierto();
    }

    /**
     * Elige la casilla que atacará la CPU, usando su estrategia.
     *
     * @param partida partida en curso
     * @return la casilla elegida
     * @throws IllegalStateException si no hay casillas atacables
     */
    public Casilla elegirCasilla(Partida partida) {
        return estrategia.elegirCasilla(partida, this);
    }

    /**
     * Devuelve la dificultad de la CPU.
     *
     * @return la dificultad
     */
    public DificultadCPU getDificultad() {
        return dificultad;
    }

    /**
     * Indica que este jugador no es una persona.
     *
     * @return siempre {@code false}
     */
    @Override
    public boolean esHumano() {
        return false;
    }
}
