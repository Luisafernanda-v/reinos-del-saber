package com.reinos.modelo.mapa;

import java.util.ArrayList;
import java.util.List;

/**
 * Posición de una casilla hexagonal en coordenadas axiales.
 * <p>
 * El mapa usa hexágonos con la punta hacia arriba. En coordenadas axiales,
 * cada casilla tiene exactamente 6 vecinos posibles, que se obtienen sumando
 * una de las 6 direcciones de {@link #DIRECCIONES}.
 * </p>
 *
 * @param q columna axial de la casilla
 * @param r fila de la casilla
 */
public record Coordenada(int q, int r) {

    /**
     * Desplazamientos de las 6 direcciones vecinas, como pares {@code {dq, dr}}.
     * <p>
     * El orden es: este, oeste, sureste, noroeste, noreste y suroeste.
     * </p>
     */
    private static final int[][] DIRECCIONES = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {-1, 1}
    };

    /**
     * Calcula las 6 coordenadas vecinas de esta coordenada.
     * <p>
     * No comprueba si los vecinos existen en el mapa; para eso se usa
     * {@link Mapa#adyacentes(Coordenada)}.
     * </p>
     *
     * @return lista con las 6 coordenadas vecinas
     */
    public List<Coordenada> vecinos() {
        List<Coordenada> vecinos = new ArrayList<>(DIRECCIONES.length);
        for (int[] direccion : DIRECCIONES) {
            vecinos.add(new Coordenada(q + direccion[0], r + direccion[1]));
        }
        return vecinos;
    }

    /**
     * Convierte una posición de la cuadrícula de texto a coordenadas axiales.
     * <p>
     * En {@code mapa.txt} las filas impares se desplazan media casilla a la
     * derecha, así que la conversión es
     * {@code q = columna - (fila - (fila & 1)) / 2} y {@code r = fila}.
     * </p>
     *
     * @param fila    fila de la cuadrícula, empezando en 0
     * @param columna columna de la cuadrícula, empezando en 0
     * @return la coordenada axial equivalente
     */
    public static Coordenada desdeFilaColumna(int fila, int columna) {
        return new Coordenada(columna - (fila - (fila & 1)) / 2, fila);
    }
}
