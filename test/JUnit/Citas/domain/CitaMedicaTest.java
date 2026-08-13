package JUnit.Citas.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitaMedicaTest {

    private static final LocalDateTime VIERNES_10AM = LocalDateTime.of(2026, 8, 14, 10, 0);
    private static final LocalDateTime SABADO_10AM = LocalDateTime.of(2026, 8, 15, 10, 0);

    @Test
    @DisplayName("Crea una cita válida calculando el horario de fin por duración del tipo")
    void crearCitaValida() {
        // Arrange
        String nombrePaciente = "Ana Pérez";
        String medicoId = "MED-001";
        TipoCita tipoCita = TipoCita.GENERAL;

        // Act
        CitaMedica cita = CitaMedica.crear(nombrePaciente, medicoId, tipoCita, VIERNES_10AM);

        // Assert
        assertNotNull(cita);
        assertNotNull(cita.getId());
        // Assert
        assertEquals(nombrePaciente, cita.getNombrePaciente());
        assertEquals(medicoId, cita.getMedicoId());
        assertEquals(tipoCita, cita.getTipoCita());
        assertEquals(VIERNES_10AM, cita.getInicio());
        assertEquals(VIERNES_10AM.plusMinutes(30), cita.getFin());

        System.out.println("Cita creada -> " + cita.getNombrePaciente()
                + " | " + cita.getTipoCita().getDescripcion()
                + " | Inicio: " + cita.getInicio()
                + " | Fin: " + cita.getFin()
                + " | Costo: $" + cita.calcularCosto());
    }

    @Test
    @DisplayName("Devuelve null si el nombre del paciente está vacío")
    void crearConNombreVacioDevuelveNull() {
        // Arrange
        String nombrePaciente = "   ";
        String medicoId = "MED-001";

        // Act
        CitaMedica cita = CitaMedica.crear(nombrePaciente, medicoId, TipoCita.GENERAL, VIERNES_10AM);

        System.out.println("Nombre vacío -> cita creada? " + (cita != null));

        // Assert
        assertNull(cita);
    }

    @Test
    @DisplayName("Devuelve null si el médico está vacío")
    void crearConMedicoVacioDevuelveNull() {
        // Arrange
        String nombrePaciente = "Ana Pérez";
        String medicoId = "";

        // Act
        CitaMedica cita = CitaMedica.crear(nombrePaciente, medicoId, TipoCita.GENERAL, VIERNES_10AM);

        System.out.println("Médico vacío -> cita creada? " + (cita != null));

        // Assert
        assertNull(cita);
    }

    @Test
    @DisplayName("Devuelve null si la cita no cabe en el horario de atención")
    void crearFueraDeHorarioDevuelveNull() {
        // Arrange
        LocalDateTime citaQueFinalizaFuera = LocalDateTime.of(2026, 8, 14, 19, 45);

        // Act
        CitaMedica cita = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.ESPECIALISTA, citaQueFinalizaFuera);

        System.out.println("Cita fuera de horario (" + citaQueFinalizaFuera + ") -> cita creada? " + (cita != null));

        // Assert
        assertNull(cita);
    }

    @Test
    @DisplayName("Detecta solapamiento entre dos citas")
    void detectaSolapamiento() {
        // Arrange
        CitaMedica cita1 = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.GENERAL, VIERNES_10AM);
        LocalDateTime inicioDeOtra = VIERNES_10AM.plusMinutes(15);
        LocalDateTime finDeOtra = inicioDeOtra.plusMinutes(30);

        // Act
        boolean seSolapa = cita1.seSolapaCon(inicioDeOtra, finDeOtra);

        System.out.println("Cita1: " + cita1.getInicio() + " - " + cita1.getFin()
                + " | Otra: " + inicioDeOtra + " - " + finDeOtra
                + " | Se solapa? " + seSolapa);

        // Assert
        assertTrue(seSolapa);
    }

    @Test
    @DisplayName("No detecta solapamiento cuando las citas son consecutivas")
    void noDetectaSolapamientoConsecutivo() {
        // Arrange
        CitaMedica cita1 = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.GENERAL, VIERNES_10AM);
        LocalDateTime inicioDeOtra = cita1.getFin();
        LocalDateTime finDeOtra = inicioDeOtra.plusMinutes(30);

        // Act
        boolean seSolapa = cita1.seSolapaCon(inicioDeOtra, finDeOtra);

        System.out.println("Citas consecutivas -> se solapan? " + seSolapa);

        // Assert
        assertFalse(seSolapa);
    }

    @Test
    @DisplayName("Calcula costo bruto igual a la tarifa base entre semana")
    void calculaCostoBrutoEntreSemana() {
        // Arrange
        CitaMedica cita = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.ESPECIALISTA, VIERNES_10AM);

        // Act
        double costo = cita.calcularCostoBruto();

        System.out.println("Costo bruto ESPECIALISTA -> $" + costo);

        // Assert
        assertEquals(70.00, costo, 0.001);
    }

    @Test
    @DisplayName("Aplica recargo del 30% el fin de semana")
    void calculaCostoConRecargoFinDeSemana() {
        // Arrange
        CitaMedica cita = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.GENERAL, SABADO_10AM);

        // Act
        double costo = cita.calcularCosto();

        System.out.println("Costo GENERAL sábado (recargo 30%) -> $" + costo);

        // Assert
        assertEquals(32.50, costo, 0.001);
    }

    @Test
    @DisplayName("No aplica recargo en día de semana")
    void calculaCostoSinRecargoEntreSemana() {
        // Arrange
        CitaMedica cita = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.CHECKUP, VIERNES_10AM);

        // Act
        double costo = cita.calcularCosto();

        System.out.println("Costo CHECKUP viernes (sin recargo) -> $" + costo);

        // Assert
        assertEquals(50.00, costo, 0.001);
    }

    @Test
    @DisplayName("Identifica los días de fin de semana")
    void identificaFinDeSemana() {
        // Act
        boolean esSabado = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.GENERAL, SABADO_10AM).esFinDeSemana();
        boolean esViernes = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.GENERAL, VIERNES_10AM).esFinDeSemana();

        // Assert
        System.out.println("Es fin de semana? sábado: " + esSabado + " | viernes: " + esViernes);

        // Assert
        assertTrue(esSabado);
        assertFalse(esViernes);
    }
}