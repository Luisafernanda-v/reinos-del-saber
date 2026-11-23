package com.reinos.modelo.partida;

import com.reinos.modelo.jugador.Jugador;
import com.reinos.modelo.jugador.JugadorCPU;
import com.reinos.modelo.jugador.JugadorHumano;
import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.mapa.Mapa;
import com.reinos.modelo.mapa.Pais;
import com.reinos.modelo.retos.BancoRetos;
import com.reinos.modelo.retos.Reto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Partida en curso y única fuente de verdad del juego.
 * <p>
 * Funciona como una máquina de estados ({@link EstadoPartida}). Los métodos
 * que modifican la partida lanzan {@link IllegalStateException} si se llaman
 * en un estado que no les corresponde. Antes de {@link #iniciar()} la
 * partida no tiene estado y solo acepta esa llamada.
 * </p>
 * <p>
 * Al terminar cada acción, si se conquistó una sede la partida pasa a
 * {@link EstadoPartida#FINALIZADA}. Si no, y quedan acciones, el jugador
 * sigue en su turno; si no quedan, el turno pasa directamente al siguiente
 * jugador.
 * </p>
 */
public class Partida {

    /**
     * Mapa de esta partida; sus casillas cambian de país al conquistarlas.
     */
    private final Mapa mapa;

    /**
     * Banco del que se piden los retos de cada intento.
     */
    private final BancoRetos banco;

    /**
     * Generador usado para el sorteo y para las respuestas de la CPU.
     */
    private final RandomGenerator random;

    /**
     * Jugadores en el orden de los turnos, que se sortea al iniciar.
     */
    private final List<Jugador> jugadores;

    /**
     * Ronda actual, empezando en 1; avanza cuando juega el primer jugador
     * otra vez.
     */
    private int ronda;

    /**
     * Posición en {@link #jugadores} de quien tiene el turno.
     */
    private int turno;

    /**
     * Estado actual, o {@code null} antes de iniciar la partida.
     */
    private EstadoPartida estado;

    /**
     * Acciones que le quedan al jugador en este turno.
     */
    private int accionesRestantes;

    /**
     * Ataque en curso, o {@code null} si no hay uno.
     */
    private SerieRetos serie;

    /**
     * Jugador que conquistó una sede, o {@code null} si la partida sigue.
     */
    private Jugador ganador;

    /**
     * Crea una partida sin iniciar.
     *
     * @param configuracion datos de los jugadores
     * @param mapa          mapa recién cargado, que solo usa esta partida
     * @param banco         banco de retos
     * @param random        generador aleatorio; las pruebas pueden pasar uno
     *                      con semilla fija para obtener resultados repetibles
     * @throws IllegalStateException si la configuración no es válida
     */
    public Partida(ConfiguracionPartida configuracion, Mapa mapa, BancoRetos banco, RandomGenerator random) {
        this.mapa = mapa;
        this.banco = banco;
        this.random = random;
        this.jugadores = configuracion.crearJugadores();
    }

    /**
     * Sortea el orden de los turnos y el país de cada jugador, y empieza el
     * primer turno.
     *
     * @throws IllegalStateException si la partida ya se inició o el mapa no
     *                               tiene una sede por país
     */
    public void iniciar() {
        if (estado != null) {
            throw new IllegalStateException("La partida ya se inició");
        }
        Collections.shuffle(jugadores, random);
        List<Pais> paises = new ArrayList<>(List.of(Pais.values()));
        Collections.shuffle(paises, random);
        for (int i = 0; i < jugadores.size(); i++) {
            Casilla sede = mapa.getSede(paises.get(i));
            if (sede == null) {
                throw new IllegalStateException("El mapa no tiene sede de " + paises.get(i));
            }
            jugadores.get(i).asignarPais(paises.get(i), sede);
        }
        ronda = 1;
        turno = 0;
        empezarTurno();
    }

    /**
     * Empieza un ataque humano contra una casilla y presenta el primer reto.
     * <p>
     * El ataque consume una acción aunque no logre conquistar la casilla.
     * </p>
     *
     * @param casilla casilla atacada, que debe ser una de
     *                {@link #casillasAtacables()}
     * @throws IllegalStateException    si el estado no es
     *                                  {@link EstadoPartida#ELIGIENDO_CASILLA}
     * @throws IllegalArgumentException si la casilla no se puede atacar
     */
    public void atacar(Casilla casilla) {
        exigirEstado(EstadoPartida.ELIGIENDO_CASILLA);
        empezarAtaque(casilla);
        pedirReto();
        estado = EstadoPartida.RESPONDIENDO_RETO;
    }

    /**
     * Responde el reto actual.
     * <p>
     * Un acierto avanza la serie y presenta un reto nuevo; si completa la
     * serie, conquista la casilla. Un fallo termina la serie sin conquistar
     * la casilla.
     * </p>
     *
     * @param indice opción elegida, de 0 a 3
     * @return {@code true} si la respuesta fue correcta
     * @throws IllegalStateException    si el estado no es
     *                                  {@link EstadoPartida#RESPONDIENDO_RETO}
     * @throws IllegalArgumentException si el índice no está entre 0 y 3
     */
    public boolean responder(int indice) {
        exigirEstado(EstadoPartida.RESPONDIENDO_RETO);
        boolean acierto = serie.getRetoActual().esCorrecta(indice);
        if (!acierto) {
            terminarAccion(false);
            return false;
        }
        serie.registrarAcierto();
        if (serie.estaCompleta()) {
            terminarAccion(true);
        } else {
            pedirReto();
        }
        return true;
    }

    /**
     * Avisa que se agotó el tiempo del reto actual, lo que equivale a un
     * fallo.
     *
     * @throws IllegalStateException si el estado no es
     *                               {@link EstadoPartida#RESPONDIENDO_RETO}
     */
    public void tiempoAgotado() {
        exigirEstado(EstadoPartida.RESPONDIENDO_RETO);
        terminarAccion(false);
    }

    /**
     * Resuelve una acción completa de la CPU: elige la casilla y simula cada
     * reto de la serie hasta conquistarla o fallar.
     *
     * @return la casilla que atacó la CPU
     * @throws IllegalStateException si el estado no es
     *                               {@link EstadoPartida#TURNO_CPU}
     */
    public Casilla ejecutarAccionCPU() {
        exigirEstado(EstadoPartida.TURNO_CPU);
        JugadorCPU cpu = (JugadorCPU) getJugadorActual();
        Casilla casilla = cpu.elegirCasilla(this);
        empezarAtaque(casilla);
        while (!serie.estaCompleta()) {
            if (!cpu.simularRespuesta(random)) {
                terminarAccion(false);
                return casilla;
            }
            serie.registrarAcierto();
        }
        terminarAccion(true);
        return casilla;
    }

    /**
     * Devuelve las casillas que puede atacar quien tiene el turno.
     *
     * @return casillas enemigas de la frontera del jugador actual
     * @throws IllegalStateException si la partida no se ha iniciado
     */
    public List<Casilla> casillasAtacables() {
        return casillasAtacablesPor(getJugadorActual());
    }

    /**
     * Devuelve las casillas que puede atacar un jugador: las de otros países
     * que tocan su territorio.
     *
     * @param jugador jugador que ataca
     * @return casillas enemigas de la frontera del jugador, en el orden del
     *         mapa
     * @throws IllegalStateException si la partida no se ha iniciado
     */
    public List<Casilla> casillasAtacablesPor(Jugador jugador) {
        if (jugador.getPais() == null) {
            throw new IllegalStateException("La partida no se ha iniciado");
        }
        List<Casilla> atacables = new ArrayList<>();
        for (Casilla casilla : mapa.getCasillas()) {
            if (mapa.esFrontera(casilla.getCoordenada(), jugador.getPais())) {
                atacables.add(casilla);
            }
        }
        return atacables;
    }

    /**
     * Devuelve las casillas que tiene un jugador.
     *
     * @param jugador jugador consultado
     * @return casillas del país del jugador
     * @throws IllegalStateException si la partida no se ha iniciado
     */
    public List<Casilla> casillasDe(Jugador jugador) {
        if (jugador.getPais() == null) {
            throw new IllegalStateException("La partida no se ha iniciado");
        }
        return mapa.casillasDePais(jugador.getPais());
    }

    /**
     * Valida la casilla, consume una acción y abre la serie de retos.
     *
     * @param casilla casilla atacada
     * @throws IllegalArgumentException si la casilla no se puede atacar
     */
    private void empezarAtaque(Casilla casilla) {
        if (casilla == null || !casillasAtacables().contains(casilla)) {
            throw new IllegalArgumentException("La casilla " + casilla
                    + " no está en la frontera de " + getJugadorActual());
        }
        accionesRestantes--;
        serie = new SerieRetos(casilla);
    }

    /**
     * Pide al banco un reto nuevo del país de la casilla atacada y del nivel
     * de quien responde.
     */
    private void pedirReto() {
        JugadorHumano humano = (JugadorHumano) getJugadorActual();
        serie.setRetoActual(banco.obtener(serie.getCasilla().getPais(), humano.getNivel()));
    }

    /**
     * Cierra la acción en curso y decide el siguiente estado.
     *
     * @param conquistada {@code true} si la serie se completó
     */
    private void terminarAccion(boolean conquistada) {
        Casilla casilla = serie.getCasilla();
        serie = null;
        Jugador actual = getJugadorActual();
        if (conquistada) {
            casilla.conquistar(actual.getPais());
            if (casilla.esSede()) {
                ganador = actual;
                estado = EstadoPartida.FINALIZADA;
                return;
            }
        }
        if (accionesRestantes > 0) {
            estado = estadoDeTurno(actual);
        } else {
            pasarTurno();
        }
    }

    /**
     * Pasa el turno al siguiente jugador y avanza la ronda cuando vuelve a
     * jugar el primero.
     */
    private void pasarTurno() {
        turno = (turno + 1) % jugadores.size();
        if (turno == 0) {
            ronda++;
        }
        empezarTurno();
    }

    /**
     * Calcula las acciones del jugador actual según su tamaño y fija el
     * estado de su turno.
     */
    private void empezarTurno() {
        Jugador actual = getJugadorActual();
        accionesRestantes = TamanoCiudad.desde(casillasDe(actual).size()).getAcciones();
        estado = estadoDeTurno(actual);
    }

    /**
     * Devuelve el estado que corresponde al turno de un jugador sin ataque
     * en curso.
     *
     * @param jugador jugador que tiene el turno
     * @return {@link EstadoPartida#ELIGIENDO_CASILLA} si es humano o
     *         {@link EstadoPartida#TURNO_CPU} si es CPU
     */
    private static EstadoPartida estadoDeTurno(Jugador jugador) {
        return jugador.esHumano() ? EstadoPartida.ELIGIENDO_CASILLA : EstadoPartida.TURNO_CPU;
    }

    /**
     * Comprueba que la partida esté en el estado esperado.
     *
     * @param esperado estado que admite la llamada
     * @throws IllegalStateException si el estado actual es otro
     */
    private void exigirEstado(EstadoPartida esperado) {
        if (estado != esperado) {
            throw new IllegalStateException("Se esperaba el estado " + esperado + " y el estado es " + estado);
        }
    }

    /**
     * Devuelve el estado actual.
     *
     * @return el estado, o {@code null} si la partida no se ha iniciado
     */
    public EstadoPartida getEstado() {
        return estado;
    }

    /**
     * Devuelve la ronda actual.
     *
     * @return la ronda, empezando en 1, o 0 si la partida no se ha iniciado
     */
    public int getRonda() {
        return ronda;
    }

    /**
     * Devuelve las acciones que le quedan al jugador en este turno.
     *
     * @return acciones restantes
     */
    public int getAccionesRestantes() {
        return accionesRestantes;
    }

    /**
     * Devuelve quién tiene el turno.
     *
     * @return el jugador actual
     */
    public Jugador getJugadorActual() {
        return jugadores.get(turno);
    }

    /**
     * Devuelve los jugadores en el orden de los turnos.
     *
     * @return lista inmutable con los jugadores
     */
    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(jugadores);
    }

    /**
     * Devuelve el mapa de la partida.
     *
     * @return el mapa
     */
    public Mapa getMapa() {
        return mapa;
    }

    /**
     * Devuelve el ataque en curso.
     *
     * @return la serie actual, o {@code null} si no hay ataque en curso
     */
    public SerieRetos getSerie() {
        return serie;
    }

    /**
     * Devuelve el reto que se está respondiendo.
     *
     * @return el reto actual, o {@code null} si no hay reto en curso
     */
    public Reto getRetoActual() {
        return serie == null ? null : serie.getRetoActual();
    }

    /**
     * Devuelve el ganador de la partida.
     *
     * @return el ganador, o {@code null} si la partida no ha terminado
     */
    public Jugador getGanador() {
        return ganador;
    }
}
