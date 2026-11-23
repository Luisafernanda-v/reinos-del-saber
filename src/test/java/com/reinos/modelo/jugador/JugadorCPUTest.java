package com.reinos.modelo.jugador;

import org.junit.jupiter.api.Test;

import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link JugadorCPU}.
 */
class JugadorCPUTest {

    /**
     * Crea un generador cuyo {@code nextDouble()} siempre devuelve el mismo
     * valor.
     *
     * @param valor valor devuelto
     * @return el generador controlado
     */
    private static RandomGenerator fijo(double valor) {
        return new RandomGenerator() {
            @Override
            public long nextLong() {
                return 0L;
            }

            @Override
            public double nextDouble() {
                return valor;
            }
        };
    }

    @Test
    void aciertaOFallaSegunSuProbabilidad() {
        JugadorCPU media = new JugadorCPU("CPU 1", DificultadCPU.MEDIA, new EstrategiaAvance());

        assertTrue(media.simularRespuesta(fijo(0.0)));
        assertTrue(media.simularRespuesta(fijo(0.69)));
        assertFalse(media.simularRespuesta(fijo(0.7)));
        assertFalse(media.simularRespuesta(fijo(0.99)));
    }

    @Test
    void cadaDificultadTieneSuLimite() {
        RandomGenerator sesenta = fijo(0.6);

        assertFalse(new JugadorCPU("CPU 1", DificultadCPU.FACIL, new EstrategiaAvance()).simularRespuesta(sesenta));
        assertTrue(new JugadorCPU("CPU 1", DificultadCPU.MEDIA, new EstrategiaAvance()).simularRespuesta(sesenta));
        assertTrue(new JugadorCPU("CPU 1", DificultadCPU.DIFICIL, new EstrategiaAvance()).simularRespuesta(sesenta));
        assertFalse(new JugadorCPU("CPU 1", DificultadCPU.DIFICIL, new EstrategiaAvance()).simularRespuesta(fijo(0.9)));
    }

    @Test
    void noEsHumano() {
        assertFalse(new JugadorCPU("CPU 1", DificultadCPU.FACIL, new EstrategiaAvance()).esHumano());
    }
}
