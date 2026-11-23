package com.reinos.modelo.mapa;

/**
 * Tipos de terreno de una casilla.
 * <p>
 * El terreno define la Defensa base de la casilla, es decir, cuántos
 * aciertos seguidos se necesitan para conquistarla.
 * </p>
 */
public enum Terreno {

    /**
     * Casilla de tierra.
     * <p>
     * Se conquista con 1 acierto.
     * </p>
     */
    TIERRA(1),

    /**
     * Casilla de agua.
     * <p>
     * Se conquista con 2 aciertos seguidos.
     * </p>
     */
    AGUA(2);

    /**
     * Aciertos seguidos necesarios para conquistar una casilla de este
     * terreno que no sea sede.
     */
    private final int defensaBase;

    /**
     * Crea un terreno con su Defensa base.
     *
     * @param defensaBase aciertos necesarios para conquistar la casilla
     */
    Terreno(int defensaBase) {
        this.defensaBase = defensaBase;
    }

    /**
     * Devuelve la Defensa base del terreno.
     *
     * @return aciertos seguidos necesarios para conquistar la casilla
     */
    public int getDefensaBase() {
        return defensaBase;
    }
}
