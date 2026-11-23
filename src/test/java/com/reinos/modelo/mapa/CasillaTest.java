package com.reinos.modelo.mapa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de {@link Casilla}.
 */
class CasillaTest {

    private static final Coordenada ORIGEN = new Coordenada(0, 0);

    @Test
    void laDefensaDependeDelTerrenoODeLaSede() {
        assertEquals(1, new Casilla(ORIGEN, Pais.NUMEROS, Terreno.TIERRA, false).getDefensa());
        assertEquals(2, new Casilla(ORIGEN, Pais.NUMEROS, Terreno.AGUA, false).getDefensa());
        assertEquals(4, new Casilla(ORIGEN, Pais.NUMEROS, Terreno.TIERRA, true).getDefensa());
    }

    @Test
    void conquistarCambiaElPais() {
        Casilla casilla = new Casilla(ORIGEN, Pais.NUMEROS, Terreno.TIERRA, false);

        casilla.conquistar(Pais.IDIOMAS);

        assertEquals(Pais.IDIOMAS, casilla.getPais());
    }

    @Test
    void noSePuedeConquistarUnaCasillaPropia() {
        Casilla casilla = new Casilla(ORIGEN, Pais.NUMEROS, Terreno.TIERRA, false);

        assertThrows(IllegalArgumentException.class, () -> casilla.conquistar(Pais.NUMEROS));
    }
}
