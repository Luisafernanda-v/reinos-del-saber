package com.reinos.modelo.jugador;

import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.mapa.Pais;

/**
 * Participante de una partida, humano o CPU.
 * <p>
 * El jugador se crea solo con su nombre. Su país y su sede se asignan por
 * sorteo cuando empieza la partida.
 * </p>
 */
public abstract class Jugador {

    /**
     * Nombre con el que se muestra el jugador.
     */
    private final String nombre;

    /**
     * País que representa el jugador, o {@code null} antes del sorteo.
     */
    private Pais pais;

    /**
     * Sede del país del jugador, o {@code null} antes del sorteo.
     */
    private Casilla sede;

    /**
     * Crea un jugador sin país.
     *
     * @param nombre nombre del jugador
     * @throws IllegalArgumentException si el nombre es {@code null} o está
     *                                  vacío
     */
    protected Jugador(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    /**
     * Asigna el país y la sede sorteados al iniciar la partida.
     * <p>
     * Solo se puede llamar una vez, así que el país de un jugador no cambia
     * durante la partida.
     * </p>
     *
     * @param pais país que representa el jugador
     * @param sede sede de ese país
     * @throws IllegalArgumentException si la casilla no es la sede del país
     * @throws IllegalStateException    si el jugador ya tiene país
     */
    public void asignarPais(Pais pais, Casilla sede) {
        if (this.pais != null) {
            throw new IllegalStateException(nombre + " ya tiene país asignado");
        }
        if (!sede.esSede() || sede.getPais() != pais) {
            throw new IllegalArgumentException(sede + " no es la sede de " + pais);
        }
        this.pais = pais;
        this.sede = sede;
    }

    /**
     * Indica si el jugador es una persona.
     *
     * @return {@code true} si es humano, {@code false} si es CPU
     */
    public abstract boolean esHumano();

    /**
     * Devuelve el nombre del jugador.
     *
     * @return el nombre, sin espacios al inicio ni al final
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el país del jugador.
     *
     * @return el país, o {@code null} si la partida no ha empezado
     */
    public Pais getPais() {
        return pais;
    }

    /**
     * Devuelve la sede del jugador.
     *
     * @return la sede, o {@code null} si la partida no ha empezado
     */
    public Casilla getSede() {
        return sede;
    }

    /**
     * Describe al jugador con su nombre.
     *
     * @return el nombre del jugador
     */
    @Override
    public String toString() {
        return nombre;
    }
}
