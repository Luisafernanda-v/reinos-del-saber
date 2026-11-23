package com.reinos.modelo.mapa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lee un mapa escrito como cuadrícula de texto.
 * <p>
 * Cada línea no vacía es una fila, y cada símbolo separado por espacios es
 * una columna. Las filas impares se desplazan media casilla a la derecha.
 * Símbolos: n, i, p son tierra de Números, Idiomas y Palabras; N, I, P son
 * agua de cada país; 1, 2, 3 son las sedes; y el punto es un hueco sin
 * casilla.
 * </p>
 */
public final class CargadorMapa {

    /**
     * Evita que se creen instancias, porque la clase solo tiene métodos
     * estáticos.
     */
    private CargadorMapa() {
    }

    /**
     * Lee un mapa desde un flujo de texto en UTF-8.
     *
     * @param entrada flujo con la cuadrícula del mapa
     * @return el mapa leído
     * @throws IllegalArgumentException si hay un símbolo desconocido; el
     *                                  mensaje indica la línea y la columna
     * @throws UncheckedIOException     si falla la lectura del flujo
     */
    public static Mapa cargar(InputStream entrada) {
        Map<Coordenada, Casilla> casillas = new LinkedHashMap<>();
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            int numeroLinea = 0;
            int fila = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                String[] simbolos = linea.trim().split("\\s+");
                for (int columna = 0; columna < simbolos.length; columna++) {
                    Casilla casilla = crearCasilla(simbolos[columna], fila, columna, numeroLinea);
                    if (casilla != null) {
                        casillas.put(casilla.getCoordenada(), casilla);
                    }
                }
                fila++;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el mapa", e);
        }
        return new Mapa(casillas);
    }

    /**
     * Crea la casilla que corresponde a un símbolo de la cuadrícula.
     *
     * @param simbolo     símbolo leído
     * @param fila        fila de la cuadrícula, empezando en 0
     * @param columna     columna de la cuadrícula, empezando en 0
     * @param numeroLinea línea del archivo, para los mensajes de error
     * @return la casilla creada, o {@code null} si el símbolo es {@code .}
     * @throws IllegalArgumentException si el símbolo es desconocido
     */
    private static Casilla crearCasilla(String simbolo, int fila, int columna, int numeroLinea) {
        Coordenada coordenada = Coordenada.desdeFilaColumna(fila, columna);
        switch (simbolo) {
            case ".":
                return null;
            case "n":
                return new Casilla(coordenada, Pais.NUMEROS, Terreno.TIERRA, false);
            case "i":
                return new Casilla(coordenada, Pais.IDIOMAS, Terreno.TIERRA, false);
            case "p":
                return new Casilla(coordenada, Pais.PALABRAS, Terreno.TIERRA, false);
            case "N":
                return new Casilla(coordenada, Pais.NUMEROS, Terreno.AGUA, false);
            case "I":
                return new Casilla(coordenada, Pais.IDIOMAS, Terreno.AGUA, false);
            case "P":
                return new Casilla(coordenada, Pais.PALABRAS, Terreno.AGUA, false);
            case "1":
                return new Casilla(coordenada, Pais.NUMEROS, Terreno.TIERRA, true);
            case "2":
                return new Casilla(coordenada, Pais.IDIOMAS, Terreno.TIERRA, true);
            case "3":
                return new Casilla(coordenada, Pais.PALABRAS, Terreno.TIERRA, true);
            default:
                throw new IllegalArgumentException("Línea " + numeroLinea
                        + ": símbolo desconocido " + simbolo);
        }
    }
}
