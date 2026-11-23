package com.reinos.modelo.mapa;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link Coordenada}.
 */
class CoordenadaTest {

    @Test
    void tieneSeisVecinosDistintos() {
        Coordenada centro = new Coordenada(2, 3);
        List<Coordenada> vecinos = centro.vecinos();

        assertEquals(6, vecinos.size());
        assertEquals(6, new HashSet<>(vecinos).size());
        assertFalse(vecinos.contains(centro));
    }

    @Test
    void losVecinosSonReciprocos() {
        Coordenada centro = new Coordenada(0, 0);
        for (Coordenada vecino : centro.vecinos()) {
            assertTrue(vecino.vecinos().contains(centro));
        }
    }

    @Test
    void convierteFilasParesEImpares() {
        assertEquals(new Coordenada(3, 0), Coordenada.desdeFilaColumna(0, 3));
        assertEquals(new Coordenada(3, 1), Coordenada.desdeFilaColumna(1, 3));
        assertEquals(new Coordenada(2, 2), Coordenada.desdeFilaColumna(2, 3));
        assertEquals(new Coordenada(2, 3), Coordenada.desdeFilaColumna(3, 3));
    }
}
