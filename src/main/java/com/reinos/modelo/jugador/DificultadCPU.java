package com.reinos.modelo.jugador;

/**
 * Dificultades de la CPU.
 * <p>
 * La dificultad solo cambia la probabilidad de que la CPU acierte cada
 * reto; la forma de elegir casillas es la misma en las tres.
 * </p>
 */
public enum DificultadCPU {

    /**
     * Dificultad fácil.
     * <p>
     * La CPU acierta la mitad de las veces.
     * </p>
     */
    FACIL(0.5),

    /**
     * Dificultad media.
     * <p>
     * La CPU acierta el 70 % de las veces.
     * </p>
     */
    MEDIA(0.7),

    /**
     * Dificultad difícil.
     * <p>
     * La CPU acierta el 90 % de las veces.
     * </p>
     */
    DIFICIL(0.9);

    /**
     * Probabilidad de acertar cada reto, entre 0 y 1.
     */
    private final double probabilidadAcierto;

    /**
     * Crea una dificultad con su probabilidad de acierto.
     *
     * @param probabilidadAcierto probabilidad de acertar, entre 0 y 1
     */
    DificultadCPU(double probabilidadAcierto) {
        this.probabilidadAcierto = probabilidadAcierto;
    }

    /**
     * Devuelve la probabilidad de que la CPU acierte un reto.
     *
     * @return probabilidad entre 0 y 1
     */
    public double getProbabilidadAcierto() {
        return probabilidadAcierto;
    }
}
