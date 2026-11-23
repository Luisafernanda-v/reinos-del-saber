package com.reinos.modelo.mapa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Conjunto de casillas de una partida.
 * <p>
 * Cada partida necesita su propio mapa, porque las conquistas cambian el
 * país de las casillas.
 * </p>
 */
public class Mapa {

    /**
     * Casillas del mapa indexadas por su coordenada, en el orden en que se
     * agregaron.
     */
    private final Map<Coordenada, Casilla> casillas;

    /**
     * Crea un mapa con las casillas dadas.
     *
     * @param casillas casillas del mapa indexadas por su coordenada
     */
    public Mapa(Map<Coordenada, Casilla> casillas) {
        this.casillas = new LinkedHashMap<>(casillas);
    }

    /**
     * Devuelve la casilla de una coordenada.
     *
     * @param coordenada posición buscada
     * @return la casilla, o {@code null} si el mapa no tiene casilla en esa
     *         posición
     */
    public Casilla getCasilla(Coordenada coordenada) {
        return casillas.get(coordenada);
    }

    /**
     * Devuelve todas las casillas del mapa.
     *
     * @return colección inmutable con las casillas, en el orden de carga
     */
    public Collection<Casilla> getCasillas() {
        return Collections.unmodifiableCollection(casillas.values());
    }

    /**
     * Devuelve las casillas del mapa que tocan una coordenada.
     *
     * @param coordenada posición de la casilla central
     * @return lista con las casillas vecinas que existen en el mapa
     */
    public List<Casilla> adyacentes(Coordenada coordenada) {
        List<Casilla> adyacentes = new ArrayList<>();
        for (Coordenada vecino : coordenada.vecinos()) {
            Casilla casilla = casillas.get(vecino);
            if (casilla != null) {
                adyacentes.add(casilla);
            }
        }
        return adyacentes;
    }

    /**
     * Devuelve las casillas que pertenecen actualmente a un país.
     *
     * @param pais país buscado
     * @return lista con las casillas del país, en el orden de carga
     */
    public List<Casilla> casillasDePais(Pais pais) {
        List<Casilla> resultado = new ArrayList<>();
        for (Casilla casilla : casillas.values()) {
            if (casilla.getPais() == pais) {
                resultado.add(casilla);
            }
        }
        return resultado;
    }

    /**
     * Busca la sede que pertenece actualmente a un país.
     *
     * @param pais país buscado
     * @return la sede del país, o {@code null} si el país no tiene sede
     */
    public Casilla getSede(Pais pais) {
        for (Casilla casilla : casillas.values()) {
            if (casilla.esSede() && casilla.getPais() == pais) {
                return casilla;
            }
        }
        return null;
    }

    /**
     * Indica si una casilla está en la frontera de un país.
     * <p>
     * Una casilla está en la frontera de un país cuando pertenece a otro país
     * y toca, por al menos uno de sus lados, una casilla del país dado. Son
     * las casillas que ese país puede atacar.
     * </p>
     *
     * @param coordenada posición de la casilla
     * @param pais       país desde el que se mira la frontera
     * @return {@code true} si la casilla existe, es de otro país y toca el
     *         país dado
     */
    public boolean esFrontera(Coordenada coordenada, Pais pais) {
        Casilla casilla = casillas.get(coordenada);
        if (casilla == null || casilla.getPais() == pais) {
            return false;
        }
        for (Casilla vecina : adyacentes(coordenada)) {
            if (vecina.getPais() == pais) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcula cuántos pasos separan dos casillas, moviéndose solo por
     * casillas del mapa.
     * <p>
     * Usa una búsqueda en anchura (BFS), así que rodea los huecos del mapa en
     * lugar de atravesarlos. Ignora a qué país pertenece cada casilla.
     * </p>
     *
     * @param desde casilla de origen
     * @param hasta casilla de destino
     * @return número de pasos, o {@code -1} si no hay camino entre ellas
     * @throws IllegalArgumentException si alguna coordenada no está en el mapa
     */
    public int distancia(Coordenada desde, Coordenada hasta) {
        if (!casillas.containsKey(desde) || !casillas.containsKey(hasta)) {
            throw new IllegalArgumentException("Las dos coordenadas deben estar en el mapa");
        }
        Map<Coordenada, Integer> pasos = new HashMap<>();
        Queue<Coordenada> pendientes = new ArrayDeque<>();
        pasos.put(desde, 0);
        pendientes.add(desde);
        while (!pendientes.isEmpty()) {
            Coordenada actual = pendientes.remove();
            if (actual.equals(hasta)) {
                return pasos.get(actual);
            }
            for (Casilla vecina : adyacentes(actual)) {
                Coordenada siguiente = vecina.getCoordenada();
                if (!pasos.containsKey(siguiente)) {
                    pasos.put(siguiente, pasos.get(actual) + 1);
                    pendientes.add(siguiente);
                }
            }
        }
        return -1;
    }
}
