package com.reinos.modelo.jugador;

import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.partida.Partida;

import java.util.List;

/**
 * Estrategia que avanza hacia la sede rival más próxima.
 * <p>
 * Entre las casillas que puede atacar, elige la de menor distancia a una
 * sede rival. Una sede rival atacable tiene distancia 0, así que siempre se
 * elige primero. Si hay empate, prefiere la de menor Defensa, y luego la de
 * menor q y menor r.
 * </p>
 */
public class EstrategiaAvance implements EstrategiaCPU {

    /**
     * Crea la estrategia. No guarda ningún dato, así que no necesita
     * parámetros.
     */
    public EstrategiaAvance() {
    }

    /**
     * Elige la casilla atacable que más acerca al jugador a una sede rival.
     *
     * @param partida partida en curso
     * @param jugador jugador que ataca
     * @return la casilla elegida
     * @throws IllegalStateException si el jugador no tiene casillas
     *                               atacables
     */
    @Override
    public Casilla elegirCasilla(Partida partida, Jugador jugador) {
        List<Casilla> atacables = partida.casillasAtacablesPor(jugador);
        if (atacables.isEmpty()) {
            throw new IllegalStateException(jugador.getNombre() + " no tiene casillas para atacar");
        }
        Casilla mejor = atacables.get(0);
        int mejorDistancia = distanciaASedeRival(partida, jugador, mejor);
        for (int i = 1; i < atacables.size(); i++) {
            Casilla casilla = atacables.get(i);
            int distancia = distanciaASedeRival(partida, jugador, casilla);
            if (esMejor(casilla, distancia, mejor, mejorDistancia)) {
                mejor = casilla;
                mejorDistancia = distancia;
            }
        }
        return mejor;
    }

    /**
     * Compara dos casillas candidatas según la prioridad de la estrategia:
     * primero la distancia, luego la Defensa, luego q y por último r.
     *
     * @param casilla        casilla nueva
     * @param distancia      distancia de la casilla nueva a una sede rival
     * @param mejor          mejor casilla encontrada hasta ahora
     * @param mejorDistancia distancia de la mejor casilla a una sede rival
     * @return {@code true} si la casilla nueva es mejor que la actual
     */
    private boolean esMejor(Casilla casilla, int distancia, Casilla mejor, int mejorDistancia) {
        if (distancia != mejorDistancia) {
            return distancia < mejorDistancia;
        }
        if (casilla.getDefensa() != mejor.getDefensa()) {
            return casilla.getDefensa() < mejor.getDefensa();
        }
        if (casilla.getCoordenada().q() != mejor.getCoordenada().q()) {
            return casilla.getCoordenada().q() < mejor.getCoordenada().q();
        }
        return casilla.getCoordenada().r() < mejor.getCoordenada().r();
    }

    /**
     * Calcula la distancia de una casilla a la sede rival más cercana,
     * usando la distancia del mapa (BFS).
     *
     * @param partida partida en curso
     * @param jugador jugador que ataca
     * @param casilla casilla candidata
     * @return la menor distancia a una sede rival
     */
    private int distanciaASedeRival(Partida partida, Jugador jugador, Casilla casilla) {
        int menor = Integer.MAX_VALUE;
        for (Jugador otro : partida.getJugadores()) {
            if (otro != jugador) {
                int distancia = partida.getMapa().distancia(casilla.getCoordenada(),
                        otro.getSede().getCoordenada());
                if (distancia >= 0 && distancia < menor) {
                    menor = distancia;
                }
            }
        }
        return menor;
    }
}
