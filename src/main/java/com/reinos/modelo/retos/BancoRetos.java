package com.reinos.modelo.retos;

import com.reinos.modelo.mapa.Pais;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Conjunto de preguntas del juego.
 * <p>
 * Entrega retos al azar del país y del nivel pedidos. Una pregunta puede
 * repetirse durante la partida, pero no dos veces seguidas si hay otra
 * disponible. Cada reto entregado tiene sus opciones barajadas.
 * </p>
 * <p>
 * Las preguntas se leen de un archivo CSV en UTF-8, separado por
 * {@code ;}, con este encabezado:
 * {@code pais;nivel;enunciado;correcta;incorrecta1;incorrecta2;incorrecta3}.
 * </p>
 */
public class BancoRetos {

    /**
     * Número de campos que debe tener cada fila del CSV.
     */
    private static final int CAMPOS_POR_FILA = 7;

    /**
     * Preguntas del banco, con la respuesta correcta en la posición 0.
     */
    private final List<Reto> retos = new ArrayList<>();

    /**
     * Última pregunta entregada, sin barajar, para no repetirla seguida.
     */
    private Reto ultimo;

    /**
     * Generador usado para elegir las preguntas y barajar sus opciones.
     */
    private final RandomGenerator random;

    /**
     * Crea un banco vacío.
     *
     * @param random generador usado para elegir y barajar; las pruebas pueden
     *               pasar uno con semilla fija para obtener resultados
     *               repetibles
     */
    public BancoRetos(RandomGenerator random) {
        this.random = random;
    }

    /**
     * Lee las preguntas de un CSV y las agrega al banco.
     * <p>
     * Omite la primera línea, que es el encabezado, y las líneas vacías, y
     * recorta los espacios de cada campo. Al terminar exige al menos una
     * pregunta para cada combinación de país y nivel. Si una fila falla, las
     * filas anteriores ya quedan agregadas.
     * </p>
     *
     * @param entrada flujo con el CSV en UTF-8
     * @throws IllegalArgumentException si una fila está mal formada o
     *                                  repetida, indicando su número de
     *                                  línea, o si falta alguna combinación
     *                                  de país y nivel
     * @throws UncheckedIOException     si falla la lectura del flujo
     */
    public void cargar(InputStream entrada) {
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea = lector.readLine();
            int numeroLinea = 1;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) {
                    continue;
                }
                try {
                    agregar(leerFila(linea));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Línea " + numeroLinea + ": " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudieron leer las preguntas", e);
        }
        validarCombinaciones();
    }

    /**
     * Convierte una fila del CSV en un reto.
     *
     * @param linea fila del CSV, sin el salto de línea
     * @return el reto leído, con la respuesta correcta en la posición 0
     * @throws IllegalArgumentException si la fila no tiene 7 campos, el país o
     *                                  el nivel no son válidos, o el reto no
     *                                  es válido
     */
    private static Reto leerFila(String linea) {
        String[] campos = linea.split(";", -1);
        if (campos.length != CAMPOS_POR_FILA) {
            throw new IllegalArgumentException("se esperaban " + CAMPOS_POR_FILA
                    + " campos y hay " + campos.length);
        }
        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }
        Pais pais;
        try {
            pais = Pais.valueOf(campos[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("país desconocido " + campos[0]);
        }
        Nivel nivel;
        try {
            nivel = Nivel.valueOf(campos[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("nivel desconocido " + campos[1]);
        }
        List<String> opciones = new ArrayList<>();
        opciones.add(campos[3]);
        opciones.add(campos[4]);
        opciones.add(campos[5]);
        opciones.add(campos[6]);
        return new Reto(pais, nivel, campos[2], opciones, 0);
    }

    /**
     * Comprueba que haya al menos una pregunta por combinación de país y
     * nivel.
     *
     * @throws IllegalArgumentException si falta alguna combinación
     */
    private void validarCombinaciones() {
        for (Pais pais : Pais.values()) {
            for (Nivel nivel : Nivel.values()) {
                if (candidatos(pais, nivel).isEmpty()) {
                    throw new IllegalArgumentException("Falta al menos una pregunta de "
                            + pais + " en nivel " + nivel);
                }
            }
        }
    }

    /**
     * Agrega una pregunta al banco.
     *
     * @param reto pregunta que se agrega
     * @throws IllegalArgumentException si ya existe una pregunta con el mismo
     *                                  país, nivel y enunciado
     */
    public void agregar(Reto reto) {
        for (Reto existente : retos) {
            if (existente.pais() == reto.pais() && existente.nivel() == reto.nivel()
                    && existente.enunciado().equalsIgnoreCase(reto.enunciado())) {
                throw new IllegalArgumentException("pregunta repetida: " + reto.enunciado());
            }
        }
        retos.add(reto);
    }

    /**
     * Entrega un reto al azar del país y nivel pedidos, con las opciones
     * barajadas.
     * <p>
     * Si hay más de una pregunta disponible, nunca entrega la misma pregunta
     * dos veces seguidas.
     * </p>
     *
     * @param pais  país cuyo tema debe tratar el reto
     * @param nivel nivel de quien responde
     * @return un reto nuevo del país y nivel pedidos
     * @throws IllegalStateException si no hay preguntas para esa combinación
     */
    public Reto obtener(Pais pais, Nivel nivel) {
        List<Reto> candidatos = candidatos(pais, nivel);
        if (candidatos.isEmpty()) {
            throw new IllegalStateException("No hay preguntas de " + pais + " en nivel " + nivel);
        }
        if (candidatos.size() > 1) {
            candidatos.remove(ultimo);
        }
        ultimo = candidatos.get(random.nextInt(candidatos.size()));
        return ultimo.barajado(random);
    }

    /**
     * Devuelve las preguntas de un país y un nivel.
     *
     * @param pais  país buscado
     * @param nivel nivel buscado
     * @return lista nueva con las preguntas encontradas
     */
    private List<Reto> candidatos(Pais pais, Nivel nivel) {
        List<Reto> resultado = new ArrayList<>();
        for (Reto reto : retos) {
            if (reto.pais() == pais && reto.nivel() == nivel) {
                resultado.add(reto);
            }
        }
        return resultado;
    }

    /**
     * Devuelve cuántas preguntas tiene el banco.
     *
     * @return número de preguntas
     */
    public int getCantidad() {
        return retos.size();
    }
}
