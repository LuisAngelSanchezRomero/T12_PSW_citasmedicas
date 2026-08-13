package JUnit.Citas.domain.port;

import JUnit.Citas.domain.CitaMedica;

import java.util.List;

public interface RepositorioCitas {

    void guardar(CitaMedica cita);

    List<CitaMedica> buscarPorMedico(String medicoId);

    List<CitaMedica> buscarTodas();
}