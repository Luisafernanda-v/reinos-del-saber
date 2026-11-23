package com.reinos.modelo.partida;

import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.mapa.Coordenada;
import com.reinos.modelo.mapa.Pais;
import com.reinos.modelo.mapa.Terreno;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link SerieRetos}.
 */
class SerieRetosTest {

    /**
     * Cuenta cuántos aciertos necesita una serie para completarse.
     *
     * @param casilla casilla atacada
     * @return aciertos registrados hasta completar la serie
     */
    private static int aciertosHastaCompletar(Casilla casilla) {
        SerieRetos serie = new SerieRetos(casilla);
        int aciertos = 0;
        while (!serie.estaCompleta()) {
            serie.registrarAcierto();
            aciertos++;
        }
        return aciertos;
    }

    @Test
    void exigeTantosAciertosComoDefensa() {
        Coordenada origen = new Coordenada(0, 0);

        assertEquals(1, aciertosHastaCompletar(new Casilla(origen, Pais.NUMEROS, Terreno.TIERRA, false)));
        assertEquals(2, aciertosHastaCompletar(new Casilla(origen, Pais.NUMEROS, Terreno.AGUA, false)));
        assertEquals(4, aciertosHastaCompletar(new Casilla(origen, Pais.NUMEROS, Terreno.TIERRA, true)));
    }

    @Test
    void noAdmiteAciertosDespuesDeCompletarse() {
        SerieRetos serie = new SerieRetos(new Casilla(new Coordenada(0, 0), Pais.NUMEROS, Terreno.AGUA, false));

        serie.registrarAcierto();
        assertFalse(serie.estaCompleta());
        serie.registrarAcierto();
        assertTrue(serie.estaCompleta());
        assertThrows(IllegalStateException.class, serie::registrarAcierto);
    }
}
