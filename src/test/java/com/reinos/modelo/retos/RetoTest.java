package com.reinos.modelo.retos;

import com.reinos.modelo.mapa.Pais;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link Reto}.
 */
class RetoTest {

    private static final List<String> OPCIONES = List.of("12", "9", "16", "20");

    private static Reto reto(List<String> opciones, int indiceCorrecto) {
        return new Reto(Pais.NUMEROS, Nivel.FACIL, "¿Cuánto es 7 + 5?", opciones, indiceCorrecto);
    }

    @Test
    void exigeDatosCompletos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reto(null, Nivel.FACIL, "¿?", OPCIONES, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Reto(Pais.NUMEROS, null, "¿?", OPCIONES, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Reto(Pais.NUMEROS, Nivel.FACIL, " ", OPCIONES, 0));
        assertThrows(IllegalArgumentException.class, () -> reto(Arrays.asList("12", null, "16", "20"), 0));
        assertThrows(IllegalArgumentException.class, () -> reto(List.of("12", "", "16", "20"), 0));
    }

    @Test
    void exigeCuatroOpcionesUnicas() {
        assertThrows(IllegalArgumentException.class, () -> reto(List.of("12", "9", "16"), 0));
        assertThrows(IllegalArgumentException.class, () -> reto(List.of("12", "9", "16", "20", "1"), 0));
        assertThrows(IllegalArgumentException.class, () -> reto(List.of("dog", "cat", "Dog", "fish"), 0));
    }

    @Test
    void exigeUnIndiceValido() {
        assertThrows(IllegalArgumentException.class, () -> reto(OPCIONES, -1));
        assertThrows(IllegalArgumentException.class, () -> reto(OPCIONES, 4));
    }

    @Test
    void identificaLaRespuestaCorrecta() {
        Reto reto = reto(OPCIONES, 0);

        assertTrue(reto.esCorrecta(0));
        assertFalse(reto.esCorrecta(1));
        assertEquals("12", reto.getRespuestaCorrecta());
        assertThrows(IllegalArgumentException.class, () -> reto.esCorrecta(4));
    }

    @Test
    void barajadoConservaLaRespuestaCorrecta() {
        Reto original = reto(OPCIONES, 0);
        Random random = new Random(1);
        boolean cambioDePosicion = false;
        for (int i = 0; i < 20; i++) {
            Reto barajado = original.barajado(random);
            assertEquals("12", barajado.getRespuestaCorrecta());
            assertEquals(Set.copyOf(OPCIONES), Set.copyOf(barajado.opciones()));
            cambioDePosicion |= barajado.indiceCorrecto() != 0;
        }
        assertTrue(cambioDePosicion);
    }
}
