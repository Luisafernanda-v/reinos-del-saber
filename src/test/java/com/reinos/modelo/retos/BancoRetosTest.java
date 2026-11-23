package com.reinos.modelo.retos;

import com.reinos.modelo.mapa.Pais;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de {@link BancoRetos}.
 */
class BancoRetosTest {

    private static final String ENCABEZADO = "pais;nivel;enunciado;correcta;incorrecta1;incorrecta2;incorrecta3\n";

    /**
     * Carga el banco con las preguntas reales del juego.
     *
     * @param random generador del banco
     * @return el banco cargado
     */
    static BancoRetos bancoReal(Random random) {
        InputStream entrada = BancoRetosTest.class.getResourceAsStream("/retos/preguntas.csv");
        assertNotNull(entrada, "Falta retos/preguntas.csv");
        BancoRetos banco = new BancoRetos(random);
        banco.cargar(entrada);
        return banco;
    }

    /**
     * Genera filas válidas con una pregunta por cada combinación de país y
     * nivel.
     *
     * @return las 9 filas, sin encabezado
     */
    private static String filasCompletas() {
        StringBuilder filas = new StringBuilder();
        for (Pais pais : Pais.values()) {
            for (Nivel nivel : Nivel.values()) {
                filas.append(pais).append(';').append(nivel)
                        .append(";Pregunta de ").append(pais).append(' ').append(nivel)
                        .append(";a;b;c;d\n");
            }
        }
        return filas.toString();
    }

    private static InputStream flujo(String texto) {
        return new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8));
    }

    private static BancoRetos cargar(String csv) {
        BancoRetos banco = new BancoRetos(new Random(1));
        banco.cargar(flujo(csv));
        return banco;
    }

    @Test
    void omiteElEncabezadoYLasLineasVacias() {
        BancoRetos banco = cargar(ENCABEZADO + "\n" + filasCompletas() + "\n");

        assertEquals(9, banco.getCantidad());
    }

    @Test
    void recortaLosEspaciosDeCadaCampo() {
        BancoRetos banco = cargar(ENCABEZADO + filasCompletas()
                + " numeros ; facil ; ¿Extra? ; sí ; no ; tal vez ; nunca \n");

        assertEquals(10, banco.getCantidad());
    }

    @Test
    void rechazaFilasMalFormadasIndicandoLaLinea() {
        String[] filasMalas = {
            "NUMEROS;FACIL;¿Faltan campos?;a;b;c",
            "MUSICA;FACIL;¿País inválido?;a;b;c;d",
            "NUMEROS;EXTREMO;¿Nivel inválido?;a;b;c;d",
            "NUMEROS;FACIL;¿Opción repetida?;a;a;c;d",
            "NUMEROS;FACIL;;a;b;c;d",
        };
        for (String fila : filasMalas) {
            String csv = ENCABEZADO + "NUMEROS;FACIL;¿Válida?;a;b;c;d\n" + fila + "\n";
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> cargar(csv), fila);
            assertTrue(error.getMessage().startsWith("Línea 3"), error.getMessage());
        }
    }

    @Test
    void rechazaPreguntasRepetidas() {
        String csv = ENCABEZADO + filasCompletas()
                + "NUMEROS;FACIL;Pregunta de NUMEROS FACIL;1;2;3;4\n";

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> cargar(csv));
        assertTrue(error.getMessage().startsWith("Línea 11"), error.getMessage());
    }

    @Test
    void exigeLasNueveCombinacionesDePaisYNivel() {
        String sinUnaCombinacion = ENCABEZADO + filasCompletas().lines()
                .filter(fila -> !fila.startsWith("PALABRAS;DIFICIL"))
                .reduce("", (a, b) -> a + b + "\n");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> cargar(sinUnaCombinacion));
        assertTrue(error.getMessage().contains("PALABRAS"), error.getMessage());
    }

    @Test
    void entregaRetosDelPaisYNivelPedidos() {
        BancoRetos banco = bancoReal(new Random(3));
        for (Pais pais : Pais.values()) {
            for (Nivel nivel : Nivel.values()) {
                Reto reto = banco.obtener(pais, nivel);
                assertEquals(pais, reto.pais());
                assertEquals(nivel, reto.nivel());
            }
        }
    }

    @Test
    void noEntregaLaMismaPreguntaDosVecesSeguidas() {
        BancoRetos banco = new BancoRetos(new Random(5));
        banco.agregar(new Reto(Pais.NUMEROS, Nivel.FACIL, "¿1 + 1?", List.of("2", "3", "4", "5"), 0));
        banco.agregar(new Reto(Pais.NUMEROS, Nivel.FACIL, "¿2 + 2?", List.of("4", "3", "5", "6"), 0));

        String anterior = banco.obtener(Pais.NUMEROS, Nivel.FACIL).enunciado();
        for (int i = 0; i < 50; i++) {
            String actual = banco.obtener(Pais.NUMEROS, Nivel.FACIL).enunciado();
            assertNotEquals(anterior, actual);
            anterior = actual;
        }
    }

    @Test
    void repiteSiSoloHayUnaPregunta() {
        BancoRetos banco = new BancoRetos(new Random(5));
        banco.agregar(new Reto(Pais.NUMEROS, Nivel.FACIL, "¿1 + 1?", List.of("2", "3", "4", "5"), 0));

        assertEquals("¿1 + 1?", banco.obtener(Pais.NUMEROS, Nivel.FACIL).enunciado());
        assertEquals("¿1 + 1?", banco.obtener(Pais.NUMEROS, Nivel.FACIL).enunciado());
        assertThrows(IllegalStateException.class, () -> banco.obtener(Pais.IDIOMAS, Nivel.FACIL));
    }

    @Test
    void elArchivoRealCargaSinErrores() {
        assertTrue(bancoReal(new Random(1)).getCantidad() >= 9);
    }
}
