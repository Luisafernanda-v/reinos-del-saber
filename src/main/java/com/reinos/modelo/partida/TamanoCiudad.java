package com.reinos.modelo.partida;

/**
 * Tamaños que puede tener la sede de un jugador.
 * <p>
 * El tamaño depende de cuántas casillas tiene el jugador y define cuántas
 * acciones recibe al empezar su turno.
 * </p>
 */
public enum TamanoCiudad {

    /**
     * Aldea: hasta 27 casillas.
     * <p>
     * Da 2 acciones por turno.
     * </p>
     */
    ALDEA(2, 0),

    /**
     * Pueblo: de 28 a 37 casillas.
     * <p>
     * Da 3 acciones por turno.
     * </p>
     */
    PUEBLO(3, 28),

    /**
     * Ciudad: 38 casillas o más.
     * <p>
     * Da 4 acciones por turno.
     * </p>
     */
    CIUDAD(4, 38);

    /**
     * Acciones por turno que da este tamaño.
     */
    private final int acciones;

    /**
     * Menor número de casillas con el que se alcanza este tamaño.
     */
    private final int casillasMinimas;

    /**
     * Crea un tamaño de ciudad.
     *
     * @param acciones        acciones por turno
     * @param casillasMinimas casillas necesarias para alcanzar el tamaño
     */
    TamanoCiudad(int acciones, int casillasMinimas) {
        this.acciones = acciones;
        this.casillasMinimas = casillasMinimas;
    }

    /**
     * Calcula el tamaño que corresponde a un número de casillas.
     *
     * @param casillas casillas del jugador
     * @return el tamaño de la sede
     * @throws IllegalArgumentException si {@code casillas} es negativo
     */
    public static TamanoCiudad desde(int casillas) {
        if (casillas < 0) {
            throw new IllegalArgumentException("El número de casillas no puede ser negativo");
        }
        if (casillas >= CIUDAD.casillasMinimas) {
            return CIUDAD;
        }
        if (casillas >= PUEBLO.casillasMinimas) {
            return PUEBLO;
        }
        return ALDEA;
    }

    /**
     * Devuelve las acciones por turno de este tamaño.
     *
     * @return número de acciones
     */
    public int getAcciones() {
        return acciones;
    }

    /**
     * Devuelve las casillas necesarias para alcanzar este tamaño.
     * <p>
     * Sirve para mostrar el progreso de un jugador hacia el siguiente
     * tamaño.
     * </p>
     *
     * @return menor número de casillas de este tamaño
     */
    public int getCasillasMinimas() {
        return casillasMinimas;
    }
}
