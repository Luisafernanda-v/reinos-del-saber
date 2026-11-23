package com.reinos.modelo.jugador;

import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.partida.Partida;

/**
 * Forma en que la CPU elige qué casilla atacar.
 */
public interface EstrategiaCPU {

    /**
     * Elige la casilla que atacará un jugador.
     *
     * @param partida partida en curso
     * @param jugador jugador que ataca
     * @return una de las casillas que el jugador puede atacar
     * @throws IllegalStateException si el jugador no tiene casillas
     *                               atacables
     */
    Casilla elegirCasilla(Partida partida, Jugador jugador);
}
