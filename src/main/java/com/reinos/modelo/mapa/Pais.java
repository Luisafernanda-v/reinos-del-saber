package com.reinos.modelo.mapa;

/**
 * Países del mapa.
 * <p>
 * Cada país define el tema de las preguntas que recibe quien ataca una de
 * sus casillas.
 * </p>
 */
public enum Pais {

    /**
     * País de los números.
     * <p>
     * Sus casillas se conquistan con operaciones matemáticas.
     * </p>
     */
    NUMEROS,

    /**
     * País de los idiomas.
     * <p>
     * Sus casillas se conquistan con vocabulario en inglés.
     * </p>
     */
    IDIOMAS,

    /**
     * País de las palabras.
     * <p>
     * Sus casillas se conquistan con sinónimos y antónimos.
     * </p>
     */
    PALABRAS
}
