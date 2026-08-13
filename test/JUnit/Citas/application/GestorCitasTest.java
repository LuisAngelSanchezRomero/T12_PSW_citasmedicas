package JUnit.Citas.application;

import JUnit.Citas.domain.CitaMedica;
import JUnit.Citas.domain.TipoCita;
import JUnit.Citas.domain.port.RepositorioCitas;
import JUnit.Citas.infrastructure.RepositorioCitasEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestorCitasTest {

    private static final String MEDICO_001 = "MED-001";
    private static final LocalDateTime LUNES_09AM = LocalDateTime.of(2026, 8, 17, 9, 0);
    private static final LocalDateTime SABADO_10AM = LocalDateTime.of(2026, 8, 15, 10, 0);

    private RepositorioCitas repositorio;
    private GestorCitas gestor;

    @BeforeEach
    void setUp() {
        // Arrange
        repositorio = new RepositorioCitasEnMemoria();
        gestor = new GestorCitas(repositorio);
    }

    @Test
    @DisplayName("Registra una cita cuando el horario está libre")
    void registraCitaEnHorarioLibre() {
        // Arrange
        String nombrePaciente = "Ana Pérez";

        // Act
        ResultadoRegistro resultado = gestor.registrarCita(nombrePaciente, MEDICO_001, TipoCita.GENERAL, LUNES_09AM);

        System.out.println("Registrar cita -> " + resultado.getCita().getNombrePaciente()
                + " | " + resultado.getCita().getTipoCita().getDescripcion()
                + " | Inicio: " + resultado.getCita().getInicio()
                + " | Costo: $" + gestor.calcularCosto(resultado.getCita()));

        // Assert
        assertTrue(resultado.esExitoso());
        assertEquals(nombrePaciente, resultado.getCita().getNombrePaciente());
        assertEquals(1, gestor.listarCitasDelMedico(MEDICO_001).size());
    }

    @Test
    @DisplayName("Rechaza la cita cuando el horario ya está ocupado por el mismo médico")
    void rechazaCitaCuandoHaySolapamiento() {
        // Arrange
        gestor.registrarCita("Ana Pérez", MEDICO_001, TipoCita.GENERAL, LUNES_09AM);
        LocalDateTime inicioSolapado = LUNES_09AM.plusMinutes(10);

        // Act
        ResultadoRegistro resultado = gestor
                .registrarCita("Luis Gómez", MEDICO_001, TipoCita.ESPECIALISTA, inicioSolapado);

        System.out.println("Rechazo por solapamiento -> exitoso:" + resultado.esExitoso()
                + " | motivo: " + resultado.getMotivo());

        // Assert
        assertFalse(resultado.esExitoso());
        assertNull(resultado.getCita());
        assertTrue(resultado.getMotivo().contains("ya tiene una cita"));
        assertEquals(1, gestor.listarCitasDelMedico(MEDICO_001).size());
    }

    @Test
    @DisplayName("Permite citas del mismo horario para médicos distintos")
    void permiteMismoHorarioParaMedicosDistintos() {
        // Arrange
        gestor.registrarCita("Ana Pérez", MEDICO_001, TipoCita.GENERAL, LUNES_09AM);

        // Act
        ResultadoRegistro resultadoOtroMedico = gestor
                .registrarCita("Luis Gómez", "MED-002", TipoCita.GENERAL, LUNES_09AM);

        System.out.println("Mismo horario, otro médico (MED-002) -> exitoso: "
                + resultadoOtroMedico.esExitoso());

        // Assert
        assertTrue(resultadoOtroMedico.esExitoso());
        assertEquals("MED-002", resultadoOtroMedico.getCita().getMedicoId());
        assertEquals(1, gestor.listarCitasDelMedico(MEDICO_001).size());
        assertEquals(1, gestor.listarCitasDelMedico("MED-002").size());
    }

    @Test
    @DisplayName("Rechaza la cita si los datos son inválidos")
    void rechazaCitaConDatosInvalidos() {
        // Arrange
        String nombreVacio = "  ";

        // Act
        ResultadoRegistro resultado = gestor.registrarCita(nombreVacio, MEDICO_001, TipoCita.GENERAL, LUNES_09AM);

        System.out.println("Datos inválidos -> exitoso: " + resultado.esExitoso()
                + " | motivo: " + resultado.getMotivo());

        // Assert
        assertFalse(resultado.esExitoso());
        assertNull(resultado.getCita());
        assertTrue(resultado.getMotivo().contains("Datos inválidos"));
    }

    @Test
    @DisplayName("Calcula el costo total de una cita")
    void calculaCostoDeUnaCita() {
        // Arrange
        ResultadoRegistro resultado = gestor.registrarCita("Ana Pérez", MEDICO_001, TipoCita.CHECKUP, LUNES_09AM);

        // Act
        double costo = gestor.calcularCosto(resultado.getCita());

        System.out.println("Costo CHECKUP -> $" + costo);

        // Assert
        assertEquals(50.00, costo, 0.001);
    }

    @Test
    @DisplayName("Calcula el costo con recargo de fin de semana")
    void calculaCostoConRecargoFinDeSemana() {
        // Arrange
        ResultadoRegistro resultado = gestor.registrarCita("Ana Pérez", MEDICO_001, TipoCita.ESPECIALISTA, SABADO_10AM);

        // Act
        double costo = gestor.calcularCosto(resultado.getCita());

        System.out.println("Costo ESPECIALISTA sábado -> $" + costo);

        // Assert
        assertEquals(91.00, costo, 0.001);
    }

    @Test
    @DisplayName("Reporta horario disponible cuando no hay citas previas")
    void horarioDisponibleSinCitas() {
        // Act
        boolean disponible = gestor.horarioDisponible(MEDICO_001, LUNES_09AM, TipoCita.GENERAL);

        System.out.println("Disponibilidad LUNES_09AM MED-001 -> " + disponible);

        // Assert
        assertTrue(disponible);
    }

    @Test
    @DisplayName("Reporta horario no disponible cuando se solapa con una cita existente")
    void horarioNoDisponiblePorSolapamiento() {
        // Arrange
        gestor.registrarCita("Ana Pérez", MEDICO_001, TipoCita.GENERAL, LUNES_09AM);
        LocalDateTime inicioSolapado = LUNES_09AM.plusMinutes(20);

        // Act
        boolean disponible = gestor.horarioDisponible(MEDICO_001, inicioSolapado, TipoCita.CHECKUP);

        System.out.println("Disponibilidad solapada MED-001 -> " + disponible);

        // Assert
        assertFalse(disponible);
    }

    @Test
    @DisplayName("Reporta horario no disponible si la cita queda fuera del horario de atención")
    void horarioNoDisponibleFueraDeHorario() {
        // Act
        boolean disponible = gestor.horarioDisponible(MEDICO_001, LocalDateTime.of(2026, 8, 17, 19, 45), TipoCita.ESPECIALISTA);

        System.out.println("Disponibilidad fuera de horario (19:45) -> " + disponible);

        // Assert
        assertFalse(disponible);
    }

    @Test
    @DisplayName("Lista únicamente las citas del médico consultado")
    void listaCitasSoloDelMedicoConsultado() {
        // Arrange
        gestor.registrarCita("Ana Pérez", MEDICO_001, TipoCita.GENERAL, LUNES_09AM);
        gestor.registrarCita("Luis Gómez", "MED-002", TipoCita.CHECKUP, LUNES_09AM);
        gestor.registrarCita("Carla Ruiz", MEDICO_001, TipoCita.ESPECIALISTA,
                LocalDateTime.of(2026, 8, 17, 11, 0));

        // Act
        List<CitaMedica> citasMedico001 = gestor.listarCitasDelMedico(MEDICO_001);

        System.out.println("Citas de MED-001 -> " + citasMedico001.size()
                + " | pacientes: " + citasMedico001.stream().map(CitaMedica::getNombrePaciente).toList());

        // Assert
        assertEquals(2, citasMedico001.size());
        assertTrue(citasMedico001.stream().allMatch(c -> c.getMedicoId().equals(MEDICO_001)));
    }
}