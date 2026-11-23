package com.reinos.modelo.partida;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de {@link TamanoCiudad}.
 */
class TamanoCiudadTest {

    @Test
    void respetaLosLimitesEntreTamanos() {
        assertEquals(TamanoCiudad.ALDEA, TamanoCiudad.desde(1));
        assertEquals(TamanoCiudad.ALDEA, TamanoCiudad.desde(27));
        assertEquals(TamanoCiudad.PUEBLO, TamanoCiudad.desde(28));
        assertEquals(TamanoCiudad.PUEBLO, TamanoCiudad.desde(37));
        assertEquals(TamanoCiudad.CIUDAD, TamanoCiudad.desde(38));
        assertEquals(TamanoCiudad.CIUDAD, TamanoCiudad.desde(81));
    }

    @Test
    void cadaTamanoDaSusAcciones() {
        assertEquals(2, TamanoCiudad.ALDEA.getAcciones());
        assertEquals(3, TamanoCiudad.PUEBLO.getAcciones());
        assertEquals(4, TamanoCiudad.CIUDAD.getAcciones());
    }

    @Test
    void rechazaCasillasNegativas() {
        assertThrows(IllegalArgumentException.class, () -> TamanoCiudad.desde(-1));
    }
}
