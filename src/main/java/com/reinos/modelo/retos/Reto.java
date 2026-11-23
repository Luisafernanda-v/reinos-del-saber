package com.reinos.modelo.retos;

import com.reinos.modelo.mapa.Pais;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Pregunta de opción múltiple con 4 opciones.
 * <p>
 * Las opciones se numeran desde 0. Al crear el reto se valida que los datos
 * estén completos, que haya exactamente 4 opciones sin repetir y que el
 * índice de la respuesta correcta sea válido.
 * </p>
 *
 * @param pais           país cuyo tema trata la pregunta
 * @param nivel          nivel de dificultad de la pregunta
 * @param enunciado      texto de la pregunta
 * @param opciones       las 4 opciones de respuesta
 * @param indiceCorrecto posición de la opción correcta, de 0 a 3
 */
public record Reto(Pais pais, Nivel nivel, String enunciado, List<String> opciones, int indiceCorrecto) {

    /**
     * Número de opciones que debe tener cada reto.
     */
    public static final int NUMERO_OPCIONES = 4;

    /**
     * Valida los datos del reto y guarda una copia inmutable de las opciones.
     *
     * @throws IllegalArgumentException si falta algún dato, no hay
     *                                  exactamente 4 opciones, alguna opción
     *                                  se repite o el índice correcto no es
     *                                  válido
     */
    public Reto {
        if (pais == null || nivel == null) {
            throw new IllegalArgumentException("El país y el nivel son obligatorios");
        }
        if (enunciado == null || enunciado.isBlank()) {
            throw new IllegalArgumentException("El enunciado es obligatorio");
        }
        if (opciones == null || opciones.size() != NUMERO_OPCIONES) {
            throw new IllegalArgumentException("El reto debe tener exactamente "
                    + NUMERO_OPCIONES + " opciones");
        }
        for (int i = 0; i < opciones.size(); i++) {
            if (opciones.get(i) == null || opciones.get(i).isBlank()) {
                throw new IllegalArgumentException("Las opciones no pueden estar vacías");
            }
            for (int j = 0; j < i; j++) {
                if (opciones.get(i).equalsIgnoreCase(opciones.get(j))) {
                    throw new IllegalArgumentException("La opción " + opciones.get(i) + " está repetida");
                }
            }
        }
        if (indiceCorrecto < 0 || indiceCorrecto >= NUMERO_OPCIONES) {
            throw new IllegalArgumentException("El índice correcto debe estar entre 0 y "
                    + (NUMERO_OPCIONES - 1));
        }
        opciones = new ArrayList<>(opciones);
    }

    /**
     * Indica si una opción es la respuesta correcta.
     *
     * @param indice posición de la opción elegida, de 0 a 3
     * @return {@code true} si la opción es la correcta
     * @throws IllegalArgumentException si el índice no está entre 0 y 3
     */
    public boolean esCorrecta(int indice) {
        if (indice < 0 || indice >= NUMERO_OPCIONES) {
            throw new IllegalArgumentException("La opción debe estar entre 0 y "
                    + (NUMERO_OPCIONES - 1));
        }
        return indice == indiceCorrecto;
    }

    /**
     * Devuelve el texto de la respuesta correcta.
     *
     * @return la opción correcta
     */
    public String getRespuestaCorrecta() {
        return opciones.get(indiceCorrecto);
    }

    /**
     * Crea una copia del reto con las opciones en otro orden.
     * <p>
     * La respuesta correcta se conserva, pero su posición puede cambiar.
     * </p>
     *
     * @param random generador usado para barajar
     * @return un reto nuevo con las mismas opciones barajadas
     */
    public Reto barajado(RandomGenerator random) {
        List<String> mezcla = new ArrayList<>(opciones);
        Collections.shuffle(mezcla, random);
        return new Reto(pais, nivel, enunciado, mezcla, mezcla.indexOf(getRespuestaCorrecta()));
    }
}
