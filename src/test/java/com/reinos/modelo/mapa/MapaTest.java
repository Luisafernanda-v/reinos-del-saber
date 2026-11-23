package com.reinos.modelo.mapa;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link Mapa}.
 */
class MapaTest {

    /**
     * Crea un mapa de tierra de Números con las coordenadas dadas.
     *
     * @param coordenadas posiciones de las casillas
     * @return el mapa creado
     */
    private static Mapa mapaCon(List<Coordenada> coordenadas) {
        Map<Coordenada, Casilla> casillas = new LinkedHashMap<>();
        for (Coordenada coordenada : coordenadas) {
            casillas.put(coordenada, new Casilla(coordenada, Pais.NUMEROS, Terreno.TIERRA, false));
        }
        return new Mapa(casillas);
    }

    @Test
    void todasLasCasillasFormanUnSoloContinente() {
        Mapa mapa = CargadorMapaTest.mapaReal();
        Coordenada origen = mapa.getCasillas().iterator().next().getCoordenada();
        for (Casilla casilla : mapa.getCasillas()) {
            assertTrue(mapa.distancia(origen, casilla.getCoordenada()) >= 0,
                    casilla + " no se alcanza desde " + origen);
        }
    }

    @Test
    void devuelveAdyacentesCorrectos() {
        Mapa mapa = CargadorMapaTest.mapaReal();
        Coordenada interior = Coordenada.desdeFilaColumna(6, 5);
        Coordenada esquina = Coordenada.desdeFilaColumna(0, 2);

        assertEquals(6, mapa.adyacentes(interior).size());

        Set<Coordenada> vecinosEsquina = new HashSet<>();
        mapa.adyacentes(esquina).forEach(c -> vecinosEsquina.add(c.getCoordenada()));
        assertEquals(Set.of(
                Coordenada.desdeFilaColumna(0, 3),
                Coordenada.desdeFilaColumna(1, 1),
                Coordenada.desdeFilaColumna(1, 2)), vecinosEsquina);
    }

    @Test
    void esFronteraEsCorrecta() {
        Mapa mapa = CargadorMapaTest.mapaReal();
        Coordenada idiomasJuntoANumeros = Coordenada.desdeFilaColumna(0, 6);
        Coordenada numerosJuntoAIdiomas = Coordenada.desdeFilaColumna(0, 5);
        Coordenada idiomasLejos = Coordenada.desdeFilaColumna(3, 11);

        assertTrue(mapa.esFrontera(idiomasJuntoANumeros, Pais.NUMEROS));
        assertTrue(mapa.esFrontera(numerosJuntoAIdiomas, Pais.IDIOMAS));
        assertFalse(mapa.esFrontera(idiomasLejos, Pais.NUMEROS));
        assertFalse(mapa.esFrontera(numerosJuntoAIdiomas, Pais.NUMEROS));
        assertFalse(mapa.esFrontera(new Coordenada(100, 100), Pais.NUMEROS));
    }

    @Test
    void distanciaRodeaLosHuecosDelMapa() {
        Coordenada centro = new Coordenada(0, 0);
        Coordenada este = new Coordenada(1, 0);
        Coordenada oeste = new Coordenada(-1, 0);
        Mapa anilloConCentro = mapaCon(List.of(centro, este, oeste,
                new Coordenada(0, 1), new Coordenada(0, -1),
                new Coordenada(1, -1), new Coordenada(-1, 1)));
        Mapa anilloSinCentro = mapaCon(List.of(este, oeste,
                new Coordenada(0, 1), new Coordenada(0, -1),
                new Coordenada(1, -1), new Coordenada(-1, 1)));

        assertEquals(2, anilloConCentro.distancia(este, oeste));
        assertEquals(3, anilloSinCentro.distancia(este, oeste));
    }

    @Test
    void distanciaEsMenosUnoSinCamino() {
        Coordenada a = new Coordenada(0, 0);
        Coordenada b = new Coordenada(5, 0);

        assertEquals(-1, mapaCon(List.of(a, b)).distancia(a, b));
    }
}
