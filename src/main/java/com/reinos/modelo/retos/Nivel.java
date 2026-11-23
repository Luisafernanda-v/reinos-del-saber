package com.reinos.modelo.retos;

/**
 * Niveles de dificultad de las preguntas.
 * <p>
 * Cada persona elige su nivel al iniciar la partida, y el nivel define el
 * tiempo que tiene para responder cada pregunta.
 * </p>
 */
public enum Nivel {

    /**
     * Nivel fácil.
     * <p>
     * Da 30 segundos por pregunta.
     * </p>
     */
    FACIL(30),

    /**
     * Nivel medio.
     * <p>
     * Da 20 segundos por pregunta.
     * </p>
     */
    MEDIO(20),

    /**
     * Nivel difícil.
     * <p>
     * Da 15 segundos por pregunta.
     * </p>
     */
    DIFICIL(15);

    /**
     * Segundos disponibles para responder cada pregunta de este nivel.
     */
    private final int segundos;

    /**
     * Crea un nivel con su tiempo por pregunta.
     *
     * @param segundos segundos disponibles para responder
     */
    Nivel(int segundos) {
        this.segundos = segundos;
    }

    /**
     * Devuelve el tiempo disponible para responder cada pregunta.
     *
     * @return segundos por pregunta
     */
    public int getSegundos() {
        return segundos;
    }
}
