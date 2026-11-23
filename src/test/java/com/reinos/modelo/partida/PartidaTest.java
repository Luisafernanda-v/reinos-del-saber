package com.reinos.modelo.partida;

import com.reinos.modelo.jugador.DificultadCPU;
import com.reinos.modelo.jugador.EstrategiaAvance;
import com.reinos.modelo.jugador.Jugador;
import com.reinos.modelo.jugador.JugadorHumano;
import com.reinos.modelo.mapa.Casilla;
import com.reinos.modelo.mapa.Pais;
import com.reinos.modelo.mapa.Terreno;
import com.reinos.modelo.retos.Reto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.reinos.modelo.partida.PartidasDePrueba.SIEMPRE_CERO;
import static com.reinos.modelo.partida.PartidasDePrueba.atacableDe;
import static com.reinos.modelo.partida.PartidasDePrueba.correcta;
import static com.reinos.modelo.partida.PartidasDePrueba.incorrecta;
import static com.reinos.modelo.partida.PartidasDePrueba.iniciada;
import static com.reinos.modelo.partida.PartidasDePrueba.mapa;
import static com.reinos.modelo.partida.PartidasDePrueba.mapaReal;
import static com.reinos.modelo.partida.PartidasDePrueba.tresHumanos;
import static com.reinos.modelo.partida.PartidasDePrueba.unHumano;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link Partida}.
 */
class PartidaTest {

    /**
     * Mapa pequeño en el que cada jugador puede atacar una sede rival desde
     * el inicio.
     */
    private static final String SEDES_JUNTAS = "1 2 i\np 3\n";

    /**
     * Mapa pequeño en el que cada jugador puede atacar una casilla de agua
     * desde el inicio. En el mapa real, el agua no toca ninguna frontera
     * inicial.
     */
    private static final String AGUA_EN_FRONTERA = "1 I 2\nP N 3\n";

    private static Partida conTresHumanos() {
        return iniciada(tresHumanos(), mapaReal(), new Random(42));
    }

    // --- Inicio y sorteo ---

    @Test
    void iniciarSorteaOrdenYPaisesConUnGeneradorControlado() {
        Partida partida = iniciada(tresHumanos(), mapaReal(), SIEMPRE_CERO);

        assertEquals(List.of("Beto", "Caro", "Ana"),
                partida.getJugadores().stream().map(Jugador::getNombre).toList());
        assertEquals(List.of(Pais.IDIOMAS, Pais.PALABRAS, Pais.NUMEROS),
                partida.getJugadores().stream().map(Jugador::getPais).toList());
    }

    @Test
    void elMismoGeneradorProduceElMismoSorteo() {
        Partida a = iniciada(tresHumanos(), mapaReal(), new Random(7));
        Partida b = iniciada(tresHumanos(), mapaReal(), new Random(7));

        assertEquals(a.getJugadores().stream().map(Jugador::getNombre).toList(),
                b.getJugadores().stream().map(Jugador::getNombre).toList());
        assertEquals(a.getJugadores().stream().map(Jugador::getPais).toList(),
                b.getJugadores().stream().map(Jugador::getPais).toList());
    }

    @Test
    void cadaJugadorRecibeUnPaisDistintoYSuSede() {
        Partida partida = conTresHumanos();

        Set<Pais> paises = Set.copyOf(partida.getJugadores().stream().map(Jugador::getPais).toList());
        assertEquals(3, paises.size());
        for (Jugador jugador : partida.getJugadores()) {
            assertSame(partida.getMapa().getSede(jugador.getPais()), jugador.getSede());
            assertEquals(27, partida.casillasDe(jugador).size());
        }
        assertEquals(1, partida.getRonda());
        assertEquals(2, partida.getAccionesRestantes());
        assertEquals(EstadoPartida.ELIGIENDO_CASILLA, partida.getEstado());
    }

    @Test
    void elPaisDeUnJugadorNoSePuedeCambiarDuranteLaPartida() {
        Partida partida = conTresHumanos();
        Jugador jugador = partida.getJugadorActual();
        Jugador otro = partida.getJugadores().get(1);

        assertThrows(IllegalStateException.class,
                () -> jugador.asignarPais(otro.getPais(), otro.getSede()));
        assertNotEquals(otro.getPais(), jugador.getPais());
    }

    // --- Estados ---

    @Test
    void losMetodosRechazanLlamadasFueraDeEstado() {
        Partida sinIniciar = new Partida(tresHumanos(), mapaReal(), PartidasDePrueba.bancoReal(), new Random(1));
        assertNull(sinIniciar.getEstado());
        assertThrows(IllegalStateException.class, () -> sinIniciar.responder(0));
        assertThrows(IllegalStateException.class, sinIniciar::tiempoAgotado);
        assertThrows(IllegalStateException.class, sinIniciar::ejecutarAccionCPU);

        Partida partida = conTresHumanos();
        assertThrows(IllegalStateException.class, partida::iniciar);
        assertThrows(IllegalStateException.class, () -> partida.responder(0));
        assertThrows(IllegalStateException.class, partida::tiempoAgotado);
        assertThrows(IllegalStateException.class, partida::ejecutarAccionCPU);

        partida.atacar(atacableDe(partida, Terreno.TIERRA));
        Casilla otra = partida.casillasAtacables().get(0);
        assertThrows(IllegalStateException.class, () -> partida.atacar(otra));
        assertThrows(IllegalStateException.class, partida::ejecutarAccionCPU);
    }

    @Test
    void atacarRechazaCasillasPropiasOSinFrontera() {
        Partida partida = conTresHumanos();
        Jugador actual = partida.getJugadorActual();
        Casilla lejana = partida.getMapa().getCasillas().stream()
                .filter(c -> c.getPais() != actual.getPais())
                .filter(c -> !partida.getMapa().esFrontera(c.getCoordenada(), actual.getPais()))
                .findFirst()
                .orElseThrow();

        assertThrows(IllegalArgumentException.class, () -> partida.atacar(actual.getSede()));
        assertThrows(IllegalArgumentException.class, () -> partida.atacar(lejana));
        assertThrows(IllegalArgumentException.class, () -> partida.atacar(null));
        assertEquals(2, partida.getAccionesRestantes());
        assertEquals(EstadoPartida.ELIGIENDO_CASILLA, partida.getEstado());
    }

    @Test
    void atacarPresentaUnRetoDelPaisYNivelDeQuienResponde() {
        Partida partida = conTresHumanos();
        Casilla casilla = atacableDe(partida, Terreno.TIERRA);

        partida.atacar(casilla);

        Reto reto = partida.getRetoActual();
        assertEquals(EstadoPartida.RESPONDIENDO_RETO, partida.getEstado());
        assertEquals(casilla.getPais(), reto.pais());
        assertEquals(((JugadorHumano) partida.getJugadorActual()).getNivel(),
                reto.nivel());
    }

    // --- Series y acciones ---

    @Test
    void responderIndicaSiHuboAciertoYUnAciertoConquistaLaTierra() {
        Partida partida = conTresHumanos();
        Jugador actual = partida.getJugadorActual();
        Casilla casilla = atacableDe(partida, Terreno.TIERRA);

        partida.atacar(casilla);
        assertTrue(partida.responder(correcta(partida)));

        assertEquals(actual.getPais(), casilla.getPais());
        assertEquals(28, partida.casillasDe(actual).size());
        assertEquals(1, partida.getAccionesRestantes());
        assertEquals(EstadoPartida.ELIGIENDO_CASILLA, partida.getEstado());
        assertNull(partida.getSerie());
    }

    @Test
    void unAciertoConSerieIncompletaPresentaUnRetoNuevo() {
        Partida partida = iniciada(tresHumanos(), mapa(AGUA_EN_FRONTERA), new Random(42));
        partida.atacar(atacableDe(partida, Terreno.AGUA));
        Reto primero = partida.getRetoActual();

        assertTrue(partida.responder(correcta(partida)));

        assertEquals(EstadoPartida.RESPONDIENDO_RETO, partida.getEstado());
        assertEquals(1, partida.getSerie().getAciertos());
        assertNotEquals(primero.enunciado(), partida.getRetoActual().enunciado());
    }

    @Test
    void unFalloConsumeLaAccionYTerminaLaSerie() {
        Partida partida = iniciada(tresHumanos(), mapa(AGUA_EN_FRONTERA), new Random(42));
        Jugador actual = partida.getJugadorActual();
        Casilla agua = atacableDe(partida, Terreno.AGUA);
        Pais paisOriginal = agua.getPais();

        partida.atacar(agua);
        partida.responder(correcta(partida));
        assertFalse(partida.responder(incorrecta(partida)));

        assertEquals(paisOriginal, agua.getPais());
        assertNull(partida.getSerie());
        assertNull(partida.getRetoActual());
        assertEquals(1, partida.getAccionesRestantes());
        assertSame(actual, partida.getJugadorActual());
        assertEquals(EstadoPartida.ELIGIENDO_CASILLA, partida.getEstado());
    }

    @Test
    void elTiempoAgotadoEquivaleAFallo() {
        Partida partida = conTresHumanos();
        Casilla casilla = atacableDe(partida, Terreno.TIERRA);
        Pais paisOriginal = casilla.getPais();

        partida.atacar(casilla);
        partida.tiempoAgotado();

        assertEquals(paisOriginal, casilla.getPais());
        assertNull(partida.getSerie());
        assertEquals(1, partida.getAccionesRestantes());
        assertEquals(EstadoPartida.ELIGIENDO_CASILLA, partida.getEstado());
    }

    @Test
    void lasAccionesSeCalculanAlIniciarElTurnoYElTurnoPasaAlAgotarlas() {
        Partida partida = conTresHumanos();
        List<Jugador> orden = partida.getJugadores();
        Jugador primero = orden.get(0);

        // El primero conquista una casilla y llega a 28 (pueblo), pero sus
        // acciones de este turno no cambian.
        partida.atacar(atacableDe(partida, Terreno.TIERRA));
        partida.responder(correcta(partida));
        assertEquals(28, partida.casillasDe(primero).size());
        assertEquals(1, partida.getAccionesRestantes());

        // Al agotar las acciones, el turno pasa directamente al siguiente.
        partida.atacar(atacableDe(partida, Terreno.TIERRA));
        partida.tiempoAgotado();
        assertSame(orden.get(1), partida.getJugadorActual());
        assertEquals(EstadoPartida.ELIGIENDO_CASILLA, partida.getEstado());
        assertEquals(1, partida.getRonda());

        // Los otros dos fallan sus acciones.
        for (int i = 1; i <= 2; i++) {
            assertSame(orden.get(i), partida.getJugadorActual());
            int acciones = partida.getAccionesRestantes();
            for (int a = 0; a < acciones; a++) {
                partida.atacar(atacableDe(partida, Terreno.TIERRA));
                partida.tiempoAgotado();
            }
        }

        // La ronda avanza después del tercer participante, y el primero ya
        // recibe las acciones de pueblo.
        assertSame(primero, partida.getJugadorActual());
        assertEquals(2, partida.getRonda());
        assertEquals(3, partida.getAccionesRestantes());
    }

    // --- Final de la partida ---

    @Test
    void conquistarUnaSedeTerminaLaPartidaYDeclaraGanadorAlAtacante() {
        Partida partida = iniciada(tresHumanos(), mapa(SEDES_JUNTAS), new Random(3));
        Jugador atacante = partida.getJugadorActual();
        Casilla sede = partida.casillasAtacables().stream().filter(Casilla::esSede).findFirst().orElseThrow();

        partida.atacar(sede);
        for (int i = 0; i < 4; i++) {
            assertEquals(EstadoPartida.RESPONDIENDO_RETO, partida.getEstado());
            assertTrue(partida.responder(correcta(partida)));
        }

        assertEquals(EstadoPartida.FINALIZADA, partida.getEstado());
        assertSame(atacante, partida.getGanador());
        assertEquals(atacante.getPais(), sede.getPais());
        assertThrows(IllegalStateException.class, () -> partida.atacar(sede));
    }

    @Test
    void ejecutarAccionCpuResuelveUnaAccionCompleta() {
        // Con SIEMPRE_CERO la CPU 1 juega primero y acierta todos los retos.
        // En el mapa real ninguna sede es atacable al inicio, así que su
        // primera acción conquista una casilla y le queda 1 acción.
        Partida partida = iniciada(unHumano(DificultadCPU.FACIL), mapaReal(), SIEMPRE_CERO);
        assertEquals(EstadoPartida.TURNO_CPU, partida.getEstado());
        Jugador cpu = partida.getJugadorActual();
        assertEquals(2, partida.getAccionesRestantes());

        Casilla atacada = partida.ejecutarAccionCPU();

        assertFalse(atacada.esSede());
        assertEquals(cpu.getPais(), atacada.getPais());
        assertEquals(28, partida.casillasDe(cpu).size());
        assertNull(partida.getSerie());
        assertSame(cpu, partida.getJugadorActual());
        assertEquals(1, partida.getAccionesRestantes());
        assertEquals(EstadoPartida.TURNO_CPU, partida.getEstado());
    }

    @Test
    void ejecutarAccionCpuPuedeTerminarLaPartida() {
        // Con SIEMPRE_CERO la CPU 1 juega primero y acierta todos los retos.
        Partida partida = iniciada(unHumano(DificultadCPU.FACIL), mapa(SEDES_JUNTAS), SIEMPRE_CERO);
        assertEquals(EstadoPartida.TURNO_CPU, partida.getEstado());
        Jugador cpu = partida.getJugadorActual();

        Casilla atacada = partida.ejecutarAccionCPU();

        assertTrue(atacada.esSede());
        assertEquals(EstadoPartida.FINALIZADA, partida.getEstado());
        assertSame(cpu, partida.getGanador());
    }

    @Test
    void unaPartidaCompletaConRespuestasSimuladasLlegaAFinalizada() {
        Partida partida = iniciada(unHumano(DificultadCPU.DIFICIL), mapaReal(), new Random(2024));
        EstrategiaAvance estrategiaHumano = new EstrategiaAvance();
        int pasos = 0;
        while (partida.getEstado() != EstadoPartida.FINALIZADA && pasos++ < 10_000) {
            switch (partida.getEstado()) {
                case ELIGIENDO_CASILLA -> partida.atacar(
                        estrategiaHumano.elegirCasilla(partida, partida.getJugadorActual()));
                case RESPONDIENDO_RETO -> partida.responder(correcta(partida));
                case TURNO_CPU -> partida.ejecutarAccionCPU();
                default -> throw new AssertionError(partida.getEstado());
            }
        }

        assertEquals(EstadoPartida.FINALIZADA, partida.getEstado());
        Jugador ganador = partida.getGanador();
        assertNotNull(ganador);
        long sedesDelGanador = partida.getMapa().getCasillas().stream()
                .filter(c -> c.esSede() && c.getPais() == ganador.getPais())
                .count();
        assertEquals(2, sedesDelGanador);
    }
}
