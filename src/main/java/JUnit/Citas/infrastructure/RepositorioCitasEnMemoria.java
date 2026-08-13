package JUnit.Citas.infrastructure;

import JUnit.Citas.domain.CitaMedica;
import JUnit.Citas.domain.port.RepositorioCitas;

import java.util.ArrayList;
import java.util.List;

public class RepositorioCitasEnMemoria implements RepositorioCitas {

    private final List<CitaMedica> citas = new ArrayList<>();

    @Override
    public void guardar(CitaMedica cita) {
        citas.add(cita);
    }

    @Override
    public List<CitaMedica> buscarPorMedico(String medicoId) {
        return citas.stream()
                .filter(cita -> cita.getMedicoId().equals(medicoId))
                .toList();
    }

    @Override
    public List<CitaMedica> buscarTodas() {
        return List.copyOf(citas);
    }
}