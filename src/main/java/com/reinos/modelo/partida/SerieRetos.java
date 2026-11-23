package com.reinos.modelo.partida;

import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.retos.Reto;

/**
 * Ataque en curso contra una casilla.
 * <p>
 * Para conquistar la casilla hay que acertar seguidos tantos retos como su
 * Defensa. Un fallo termina la serie, pero eso lo decide {@link Partida}.
 * </p>
 */
public class SerieRetos {

    /**
     * Casilla atacada.
     */
    private final Casilla casilla;

    /**
     * Aciertos seguidos necesarios, igual a la Defensa de la casilla al
     * empezar el ataque.
     */
    private final int requeridos;

    /**
     * Aciertos conseguidos hasta ahora.
     */
    private int aciertos;

    /**
     * Reto que se está respondiendo, o {@code null} si no hay uno, como en
     * los ataques de la CPU.
     */
    private Reto retoActual;

    /**
     * Empieza una serie contra una casilla.
     *
     * @param casilla casilla atacada
     */
    public SerieRetos(Casilla casilla) {
        this.casilla = casilla;
        this.requeridos = casilla.getDefensa();
    }

    /**
     * Suma un acierto a la serie.
     * <p>
     * Solo lo llama {@link Partida}, para que la interfaz no pueda cambiar
     * el ataque sin pasar por las reglas.
     * </p>
     *
     * @throws IllegalStateException si la serie ya está completa
     */
    void registrarAcierto() {
        if (estaCompleta()) {
            throw new IllegalStateException("La serie ya está completa");
        }
        aciertos++;
    }

    /**
     * Indica si ya se consiguieron todos los aciertos necesarios.
     *
     * @return {@code true} si los aciertos alcanzan la Defensa de la casilla
     */
    public boolean estaCompleta() {
        return aciertos >= requeridos;
    }

    /**
     * Devuelve la casilla atacada.
     *
     * @return la casilla atacada
     */
    public Casilla getCasilla() {
        return casilla;
    }

    /**
     * Devuelve cuántos aciertos se necesitan.
     *
     * @return aciertos requeridos
     */
    public int getRequeridos() {
        return requeridos;
    }

    /**
     * Devuelve cuántos aciertos se han conseguido.
     *
     * @return aciertos conseguidos
     */
    public int getAciertos() {
        return aciertos;
    }

    /**
     * Devuelve el reto que se está respondiendo.
     *
     * @return el reto actual, o {@code null} si no hay uno
     */
    public Reto getRetoActual() {
        return retoActual;
    }

    /**
     * Cambia el reto que se está respondiendo.
     * <p>
     * Solo lo llama {@link Partida}, al pedir un reto nuevo al banco.
     * </p>
     *
     * @param retoActual reto nuevo
     */
    void setRetoActual(Reto retoActual) {
        this.retoActual = retoActual;
    }
}
