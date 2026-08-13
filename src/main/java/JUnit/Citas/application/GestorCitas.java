package JUnit.Citas.application;

import JUnit.Citas.domain.CitaMedica;
import JUnit.Citas.domain.TipoCita;
import JUnit.Citas.domain.port.RepositorioCitas;

import java.time.LocalDateTime;
import java.util.List;

public class GestorCitas {

    private final RepositorioCitas repositorioCitas;

    public GestorCitas(RepositorioCitas repositorioCitas) {
        this.repositorioCitas = repositorioCitas;
    }

    public ResultadoRegistro registrarCita(String nombrePaciente, String medicoId,
                                           TipoCita tipoCita, LocalDateTime inicio) {
        CitaMedica nueva = CitaMedica.crear(nombrePaciente, medicoId, tipoCita, inicio);

        if (nueva == null) {
            return ResultadoRegistro.fallo(
                    "Datos inválidos o cita fuera del horario de atención (08:00 - 20:00)");
        }

        boolean solapada = repositorioCitas.buscarPorMedico(medicoId).stream()
                .anyMatch(existente -> existente.seSolapaCon(nueva));

        if (solapada) {
            return ResultadoRegistro.fallo(
                    "El médico " + medicoId + " ya tiene una cita en el horario solicitado: " + inicio);
        }

        repositorioCitas.guardar(nueva);
        return ResultadoRegistro.exitoso(nueva);
    }

    public double calcularCosto(CitaMedica cita) {
        return cita.calcularCosto();
    }

    public boolean horarioDisponible(String medicoId, LocalDateTime inicio, TipoCita tipoCita) {
        CitaMedica candidata = CitaMedica.crear("Consulta de disponibilidad", medicoId, tipoCita, inicio);

        if (candidata == null) {
            return false;
        }

        return repositorioCitas.buscarPorMedico(medicoId).stream()
                .noneMatch(existente -> existente.seSolapaCon(candidata));
    }

    public List<CitaMedica> listarCitasDelMedico(String medicoId) {
        return repositorioCitas.buscarPorMedico(medicoId);
    }
}