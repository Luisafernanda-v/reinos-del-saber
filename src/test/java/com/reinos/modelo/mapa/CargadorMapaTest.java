package com.reinos.modelo.mapa;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link CargadorMapa} con el {@code mapa.txt} real.
 */
class CargadorMapaTest {

    /**
     * Carga el mapa real del juego.
     *
     * @return el mapa de {@code mapa.txt}
     */
    static Mapa mapaReal() {
        InputStream entrada = CargadorMapaTest.class.getResourceAsStream("/mapa.txt");
        assertNotNull(entrada, "Falta mapa.txt");
        return CargadorMapa.cargar(entrada);
    }

    @Test
    void cargaOchentaYUnaCasillas() {
        assertEquals(81, mapaReal().getCasillas().size());
    }

    @Test
    void cadaPaisTieneVeintisieteCasillasYUnaSede() {
        Mapa mapa = mapaReal();
        for (Pais pais : Pais.values()) {
            assertEquals(27, mapa.casillasDePais(pais).size(), pais.name());
            assertNotNull(mapa.getSede(pais), pais.name());
        }
    }

    @Test
    void tieneDoceCasillasDeAguaYTresSedes() {
        Mapa mapa = mapaReal();
        long agua = mapa.getCasillas().stream().filter(c -> c.getTerreno() == Terreno.AGUA).count();
        long sedes = mapa.getCasillas().stream().filter(Casilla::esSede).count();

        assertEquals(12, agua);
        assertEquals(3, sedes);
    }

    @Test
    void cadaSedeEstaACincoAtaquesOMasDeUnEnemigo() {
        Mapa mapa = mapaReal();
        for (Pais pais : Pais.values()) {
            Casilla sede = mapa.getSede(pais);
            for (Casilla casilla : mapa.getCasillas()) {
                if (casilla.getPais() != pais) {
                    int distancia = mapa.distancia(casilla.getCoordenada(), sede.getCoordenada());
                    assertTrue(distancia >= 5, "La sede de " + pais + " está a " + distancia
                            + " de " + casilla);
                }
            }
        }
    }

    @Test
    void rechazaSimbolosDesconocidos() {
        InputStream entrada = new ByteArrayInputStream(
                "n n\nn x\n".getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CargadorMapa.cargar(entrada));
        assertTrue(error.getMessage().contains("Línea 2"), error.getMessage());
    }
}
