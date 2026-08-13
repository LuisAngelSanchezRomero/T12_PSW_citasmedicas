package JUnit.Citas;

import JUnit.Citas.application.GestorCitas;
import JUnit.Citas.application.ResultadoRegistro;
import JUnit.Citas.domain.CitaMedica;
import JUnit.Citas.domain.TipoCita;
import JUnit.Citas.domain.port.RepositorioCitas;
import JUnit.Citas.infrastructure.RepositorioCitasEnMemoria;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        RepositorioCitas repositorio = new RepositorioCitasEnMemoria();
        GestorCitas gestor = new GestorCitas(repositorio);

        ResultadoRegistro r1 = gestor.registrarCita(
                "Ana Pérez", "MED-001", TipoCita.GENERAL,
                LocalDateTime.of(2026, 8, 14, 9, 0));

        if (r1.esExitoso()) {
            CitaMedica cita1 = r1.getCita();
            System.out.println("Cita registrada para " + cita1.getNombrePaciente()
                    + " (" + cita1.getTipoCita().getDescripcion() + ")");
            System.out.println("Inicio: " + cita1.getInicio()
                    + " | Fin: " + cita1.getFin());
            System.out.println("Costo: $" + gestor.calcularCosto(cita1));
        } else {
            System.out.println("No se pudo registrar: " + r1.getMotivo());
        }

        boolean libre = gestor.horarioDisponible(
                "MED-001", LocalDateTime.of(2026, 8, 14, 10, 0), TipoCita.ESPECIALISTA);
        System.out.println("¿10:00 disponible para MED-001? " + libre);

        ResultadoRegistro r2 = gestor.registrarCita(
                "Luis Gómez", "MED-001", TipoCita.CHECKUP,
                LocalDateTime.of(2026, 8, 15, 10, 0));

        if (r2.esExitoso()) {
            System.out.println("Cita de fin de semana registrada, costo: $"
                    + gestor.calcularCosto(r2.getCita()));
        } else {
            System.out.println("No se pudo registrar: " + r2.getMotivo());
        }
    }
}