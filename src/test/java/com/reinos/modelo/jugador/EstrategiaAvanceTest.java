package com.reinos.modelo.jugador;

import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.mapa.Coordenada;
import com.reinos.modelo.mapa.Mapa;
import com.reinos.modelo.mapa.Pais;
import com.reinos.modelo.partida.Partida;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static com.reinos.modelo.partida.PartidasDePrueba.iniciada;
import static com.reinos.modelo.partida.PartidasDePrueba.jugadorDe;
import static com.reinos.modelo.partida.PartidasDePrueba.mapa;
import static com.reinos.modelo.partida.PartidasDePrueba.mapaReal;
import static com.reinos.modelo.partida.PartidasDePrueba.tresHumanos;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link EstrategiaAvance}.
 * <p>
 * Los mapas pequeños se leen igual que {@code mapa.txt}; en todos, Números
 * empieza solo con su sede en la esquina superior izquierda.
 * </p>
 */
class EstrategiaAvanceTest {

    private final EstrategiaAvance estrategia = new EstrategiaAvance();

    /**
     * Elige la casilla que atacaría Números en un mapa dado.
     *
     * @param textoMapa filas del mapa
     * @return la casilla elegida
     */
    private Casilla eleccionDeNumeros(String textoMapa) {
        Partida partida = iniciada(tresHumanos(), mapa(textoMapa), new Random(1));
        return estrategia.elegirCasilla(partida, jugadorDe(partida, Pais.NUMEROS));
    }

    @Test
    void prefiereUnaSedeAlcanzable() {
        // Números puede atacar la sede de Idiomas (Defensa 4) o una tierra de
        // Palabras (Defensa 1); la sede tiene distancia 0.
        Casilla elegida = eleccionDeNumeros("1 2 i\np 3\n");

        assertTrue(elegida.esSede());
        assertEquals(Pais.IDIOMAS, elegida.getPais());
    }

    @Test
    void conIgualDistanciaPrefiereMenorDefensa() {
        // Tierra (1, 0) y agua (0, 1) están a 1 paso de la sede de Idiomas.
        Casilla elegida = eleccionDeNumeros("1 i p 3\nI 2\n");

        assertEquals(new Coordenada(1, 0), elegida.getCoordenada());
    }

    @Test
    void conIgualDistanciaYDefensaPrefiereMenorQ() {
        // Las dos casillas son tierra y están a 1 paso de la sede de Idiomas.
        Casilla elegida = eleccionDeNumeros("1 i p 3\ni 2\n");

        assertEquals(new Coordenada(0, 1), elegida.getCoordenada());
    }

    @Test
    void avanzaHaciaLaSedeRivalMasProxima() {
        Partida partida = iniciada(tresHumanos(), mapaReal(), new Random(1));
        Mapa mapa = partida.getMapa();
        for (Jugador jugador : partida.getJugadores()) {
            Casilla elegida = estrategia.elegirCasilla(partida, jugador);
            int distanciaElegida = distanciaASedeRival(partida, jugador, elegida);
            for (Casilla otra : partida.casillasAtacablesPor(jugador)) {
                assertTrue(distanciaElegida <= distanciaASedeRival(partida, jugador, otra),
                        jugador + " eligió " + elegida + " pero " + otra + " está más cerca");
            }
            assertTrue(distanciaElegida < mapa.distancia(jugador.getSede().getCoordenada(),
                    sedeRivalMasProxima(partida, jugador).getCoordenada()));
        }
    }

    @Test
    void noEligeCasillasInvalidas() {
        Partida partida = iniciada(tresHumanos(), mapaReal(), new Random(9));
        for (Jugador jugador : partida.getJugadores()) {
            Casilla elegida = estrategia.elegirCasilla(partida, jugador);

            assertNotEquals(jugador.getPais(), elegida.getPais());
            assertTrue(partida.casillasAtacablesPor(jugador).contains(elegida));
        }
    }

    private static int distanciaASedeRival(Partida partida, Jugador jugador, Casilla casilla) {
        return partida.getJugadores().stream()
                .filter(otro -> otro != jugador)
                .mapToInt(otro -> partida.getMapa().distancia(casilla.getCoordenada(),
                        otro.getSede().getCoordenada()))
                .min()
                .orElseThrow();
    }

    private static Casilla sedeRivalMasProxima(Partida partida, Jugador jugador) {
        Coordenada sede = jugador.getSede().getCoordenada();
        return partida.getJugadores().stream()
                .filter(otro -> otro != jugador)
                .map(Jugador::getSede)
                .min((a, b) -> Integer.compare(
                        partida.getMapa().distancia(sede, a.getCoordenada()),
                        partida.getMapa().distancia(sede, b.getCoordenada())))
                .orElseThrow();
    }
}
