package JUnit.Citas.domain;

import java.time.LocalTime;

public final class HorarioAtencion {

    public static final LocalTime APERTURA = LocalTime.of(8, 0);
    public static final LocalTime CIERRE = LocalTime.of(20, 0);
    public static final double RECARGO_FIN_DE_SEMANA = 0.30;

    private HorarioAtencion() {
    }
}