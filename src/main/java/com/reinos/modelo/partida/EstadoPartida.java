package com.reinos.modelo.partida;

/**
 * Estados del flujo de una partida.
 * <p>
 * Cada método de {@link Partida} que modifica la partida solo se acepta en
 * un estado concreto.
 * </p>
 */
public enum EstadoPartida {

    /**
     * Turno de un humano sin ataque en curso.
     * <p>
     * La partida espera que el jugador elija una casilla con
     * {@link Partida#atacar(com.reinos.modelo.mapa.Casilla)}.
     * </p>
     */
    ELIGIENDO_CASILLA,

    /**
     * Serie de retos en curso.
     * <p>
     * La partida espera una respuesta con {@link Partida#responder(int)} o
     * el aviso de {@link Partida#tiempoAgotado()}.
     * </p>
     */
    RESPONDIENDO_RETO,

    /**
     * Turno de la CPU.
     * <p>
     * La partida espera que se llame a {@link Partida#ejecutarAccionCPU()}.
     * </p>
     */
    TURNO_CPU,

    /**
     * Partida terminada porque se conquistó una sede.
     * <p>
     * Es el estado final y no acepta más acciones.
     * </p>
     */
    FINALIZADA
}
