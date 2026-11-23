package com.reinos.modelo.partida;

import com.reinos.modelo.jugador.DificultadCPU;
import com.reinos.modelo.jugador.EstrategiaAvance;
import com.reinos.modelo.jugador.Jugador;
import com.reinos.modelo.jugador.JugadorCPU;
import com.reinos.modelo.jugador.JugadorHumano;
import com.reinos.modelo.retos.Nivel;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos elegidos en la pantalla de configuración.
 * <p>
 * Siempre participan {@value #PARTICIPANTES} jugadores: de 1 a 3 humanos, y
 * la CPU ocupa los puestos restantes con los nombres {@code CPU 1} y
 * {@code CPU 2}, que quedan reservados.
 * </p>
 */
public class ConfiguracionPartida {

    /**
     * Número de participantes de toda partida.
     */
    public static final int PARTICIPANTES = 3;

    /**
     * Prefijo de los nombres de los puestos de CPU.
     */
    private static final String PREFIJO_CPU = "CPU ";

    /**
     * Nombres de los humanos, en el orden en que se agregaron.
     */
    private final List<String> nombres = new ArrayList<>();

    /**
     * Niveles de los humanos, en la misma posición que su nombre.
     */
    private final List<Nivel> niveles = new ArrayList<>();

    /**
     * Dificultad de la CPU, obligatoria solo si hay algún puesto de CPU.
     */
    private DificultadCPU dificultadCPU;

    /**
     * Crea una configuración vacía, sin humanos ni dificultad de CPU.
     */
    public ConfiguracionPartida() {
    }

    /**
     * Agrega un humano a la configuración.
     * <p>
     * Los datos se comprueban en {@link #validar()}, para que la pantalla de
     * configuración pueda mostrar el error sin perder lo escrito.
     * </p>
     *
     * @param nombre nombre del jugador
     * @param nivel  nivel de sus preguntas
     */
    public void agregarHumano(String nombre, Nivel nivel) {
        if (nombre != null) {
            nombre = nombre.trim();
        }
        nombres.add(nombre);
        niveles.add(nivel);
    }

    /**
     * Cambia la dificultad de la CPU.
     *
     * @param dificultadCPU dificultad de los puestos de CPU
     */
    public void setDificultadCPU(DificultadCPU dificultadCPU) {
        this.dificultadCPU = dificultadCPU;
    }

    /**
     * Comprueba que la configuración permita crear una partida.
     *
     * @throws IllegalStateException si no hay de 1 a 3 humanos, algún humano
     *                               no tiene nombre o nivel, hay nombres
     *                               repetidos o reservados de CPU, o falta la
     *                               dificultad de la CPU cuando se necesita
     */
    public void validar() {
        if (nombres.isEmpty() || nombres.size() > PARTICIPANTES) {
            throw new IllegalStateException("Debe haber de 1 a " + PARTICIPANTES + " humanos");
        }
        for (int i = 0; i < nombres.size(); i++) {
            String nombre = nombres.get(i);
            if (nombre == null || nombre.isEmpty()) {
                throw new IllegalStateException("El jugador " + (i + 1) + " no tiene nombre");
            }
            if (niveles.get(i) == null) {
                throw new IllegalStateException(nombre + " no tiene nivel");
            }
            if (nombre.equalsIgnoreCase("CPU 1") || nombre.equalsIgnoreCase("CPU 2")) {
                throw new IllegalStateException("El nombre " + nombre + " está reservado para la CPU");
            }
            for (int j = 0; j < i; j++) {
                if (nombre.equalsIgnoreCase(nombres.get(j))) {
                    throw new IllegalStateException("El nombre " + nombre + " está repetido");
                }
            }
        }
        if (getPuestosCPU() > 0 && dificultadCPU == null) {
            throw new IllegalStateException("Falta la dificultad de la CPU");
        }
    }

    /**
     * Indica si la configuración permite crear una partida.
     *
     * @return {@code true} si {@link #validar()} no encuentra errores
     */
    public boolean esValida() {
        try {
            validar();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Crea los jugadores de la partida: primero los humanos y luego los
     * puestos de CPU.
     *
     * @return lista nueva con los {@value #PARTICIPANTES} jugadores
     * @throws IllegalStateException si la configuración no es válida
     */
    public List<Jugador> crearJugadores() {
        validar();
        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 0; i < nombres.size(); i++) {
            jugadores.add(new JugadorHumano(nombres.get(i), niveles.get(i)));
        }
        for (int i = 1; i <= getPuestosCPU(); i++) {
            jugadores.add(new JugadorCPU(PREFIJO_CPU + i, dificultadCPU, new EstrategiaAvance()));
        }
        return jugadores;
    }

    /**
     * Devuelve cuántos puestos ocupará la CPU.
     *
     * @return {@value #PARTICIPANTES} menos el número de humanos, o 0 si hay
     *         más humanos de los permitidos
     */
    public int getPuestosCPU() {
        return Math.max(0, PARTICIPANTES - nombres.size());
    }

    /**
     * Devuelve la dificultad de la CPU.
     *
     * @return la dificultad, o {@code null} si no se ha elegido
     */
    public DificultadCPU getDificultadCPU() {
        return dificultadCPU;
    }
}
