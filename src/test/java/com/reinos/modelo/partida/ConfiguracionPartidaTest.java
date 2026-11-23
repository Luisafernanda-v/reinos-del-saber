package com.reinos.modelo.partida;

import com.reinos.modelo.jugador.DificultadCPU;
import com.reinos.modelo.jugador.Jugador;
import com.reinos.modelo.retos.Nivel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link ConfiguracionPartida}.
 */
class ConfiguracionPartidaTest {

    @Test
    void aceptaDeUnoATresHumanos() {
        for (int humanos = 1; humanos <= 3; humanos++) {
            ConfiguracionPartida configuracion = new ConfiguracionPartida();
            for (int i = 0; i < humanos; i++) {
                configuracion.agregarHumano("Jugador " + i, Nivel.MEDIO);
            }
            configuracion.setDificultadCPU(DificultadCPU.MEDIA);

            assertDoesNotThrow(configuracion::validar);
            List<Jugador> jugadores = configuracion.crearJugadores();
            assertEquals(3, jugadores.size());
            assertEquals(humanos, jugadores.stream().filter(Jugador::esHumano).count());
        }
    }

    @Test
    void nombraLosPuestosDeCpu() {
        List<Jugador> jugadores = PartidasDePrueba.unHumano(DificultadCPU.FACIL).crearJugadores();

        assertEquals(List.of("Ana", "CPU 1", "CPU 2"), jugadores.stream().map(Jugador::getNombre).toList());
    }

    @Test
    void tresHumanosNoNecesitanDificultad() {
        assertTrue(PartidasDePrueba.tresHumanos().esValida());
    }

    @Test
    void rechazaDatosIncompletos() {
        ConfiguracionPartida sinHumanos = new ConfiguracionPartida();
        sinHumanos.setDificultadCPU(DificultadCPU.FACIL);
        assertThrows(IllegalStateException.class, sinHumanos::validar);

        ConfiguracionPartida sinNombre = new ConfiguracionPartida();
        sinNombre.agregarHumano("  ", Nivel.FACIL);
        sinNombre.setDificultadCPU(DificultadCPU.FACIL);
        assertThrows(IllegalStateException.class, sinNombre::validar);

        ConfiguracionPartida sinNivel = new ConfiguracionPartida();
        sinNivel.agregarHumano("Ana", null);
        sinNivel.setDificultadCPU(DificultadCPU.FACIL);
        assertThrows(IllegalStateException.class, sinNivel::validar);

        ConfiguracionPartida sinDificultad = new ConfiguracionPartida();
        sinDificultad.agregarHumano("Ana", Nivel.FACIL);
        assertThrows(IllegalStateException.class, sinDificultad::validar);
    }

    @Test
    void rechazaMasDeTresHumanos() {
        ConfiguracionPartida configuracion = PartidasDePrueba.tresHumanos();
        configuracion.agregarHumano("Dani", Nivel.FACIL);

        assertFalse(configuracion.esValida());
    }

    @Test
    void rechazaNombresRepetidos() {
        ConfiguracionPartida configuracion = new ConfiguracionPartida();
        configuracion.agregarHumano("Ana", Nivel.FACIL);
        configuracion.agregarHumano(" ana ", Nivel.MEDIO);
        configuracion.setDificultadCPU(DificultadCPU.FACIL);

        assertThrows(IllegalStateException.class, configuracion::validar);
    }

    @Test
    void rechazaNombresReservadosDeCpu() {
        ConfiguracionPartida configuracion = new ConfiguracionPartida();
        configuracion.agregarHumano("cpu 2", Nivel.FACIL);
        configuracion.setDificultadCPU(DificultadCPU.FACIL);

        assertThrows(IllegalStateException.class, configuracion::validar);
    }
}
