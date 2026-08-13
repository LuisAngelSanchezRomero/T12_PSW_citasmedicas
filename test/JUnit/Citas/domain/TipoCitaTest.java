package JUnit.Citas.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TipoCitaTest {

    @Test
    @DisplayName("GENERAL define tarifa base y duración correctas")
    void generalDefineTarifaYDuración() {
        // Arrange
        double tarifaEsperada = 25.00;
        int duracionEsperada = 30;

        // Act
        double tarifa = TipoCita.GENERAL.getTarifaBase();
        int duracion = TipoCita.GENERAL.getDuracionMinutos();

        System.out.println("GENERAL -> Tarifa: $" + tarifa + " | Duración: " + duracion + " min");

        // Assert
        assertEquals(tarifaEsperada, tarifa, 0.001);
        assertEquals(duracionEsperada, duracion);
    }

    @Test
    @DisplayName("ESPECIALISTA define tarifa base y duración correctas")
    void especialistaDefineTarifaYDuración() {
        // Arrange
        double tarifaEsperada = 70.00;
        int duracionEsperada = 45;

        // Act
        double tarifa = TipoCita.ESPECIALISTA.getTarifaBase();
        int duracion = TipoCita.ESPECIALISTA.getDuracionMinutos();

        System.out.println("ESPECIALISTA -> Tarifa: $" + tarifa + " | Duración: " + duracion + " min");

        // Assert
        assertEquals(tarifaEsperada, tarifa, 0.001);
        assertEquals(duracionEsperada, duracion);
    }

    @Test
    @DisplayName("CHECKUP define tarifa base y duración correctas")
    void checkupDefineTarifaYDuración() {
        // Arrange
        double tarifaEsperada = 50.00;
        int duracionEsperada = 60;

        // Act
        double tarifa = TipoCita.CHECKUP.getTarifaBase();
        int duracion = TipoCita.CHECKUP.getDuracionMinutos();

        System.out.println("CHECKUP -> Tarifa: $" + tarifa + " | Duración: " + duracion + " min");

        // Assert
        assertEquals(tarifaEsperada, tarifa, 0.001);
        assertEquals(duracionEsperada, duracion);
    }
}