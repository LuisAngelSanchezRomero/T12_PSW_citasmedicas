package JUnit.Citas.application;

import JUnit.Citas.domain.CitaMedica;

public class ResultadoRegistro {

    private final boolean exitoso;
    private final String motivo;
    private final CitaMedica cita;

    private ResultadoRegistro(boolean exitoso, String motivo, CitaMedica cita) {
        this.exitoso = exitoso;
        this.motivo = motivo;
        this.cita = cita;
    }

    public static ResultadoRegistro exitoso(CitaMedica cita) {
        return new ResultadoRegistro(true, null, cita);
    }

    public static ResultadoRegistro fallo(String motivo) {
        return new ResultadoRegistro(false, motivo, null);
    }

    public boolean esExitoso() {
        return exitoso;
    }

    public String getMotivo() {
        return motivo;
    }

    public CitaMedica getCita() {
        return cita;
    }
}