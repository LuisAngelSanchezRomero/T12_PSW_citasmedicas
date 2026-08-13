package JUnit.Citas.domain;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.UUID;

public class CitaMedica {

    private final String id;
    private final String nombrePaciente;
    private final String medicoId;
    private final TipoCita tipoCita;
    private final LocalDateTime inicio;
    private final LocalDateTime fin;

    private CitaMedica(String id, String nombrePaciente, String medicoId,
                       TipoCita tipoCita, LocalDateTime inicio, LocalDateTime fin) {
        this.id = id;
        this.nombrePaciente = nombrePaciente;
        this.medicoId = medicoId;
        this.tipoCita = tipoCita;
        this.inicio = inicio;
        this.fin = fin;
    }

    public static CitaMedica crear(String nombrePaciente, String medicoId,
                                   TipoCita tipoCita, LocalDateTime inicio) {
        if (esDatosInvalidos(nombrePaciente, medicoId, tipoCita, inicio)) {
            return null;
        }

        LocalDateTime fin = inicio.plusMinutes(tipoCita.getDuracionMinutos());

        if (inicio.toLocalTime().isBefore(HorarioAtencion.APERTURA)
                || fin.toLocalTime().isAfter(HorarioAtencion.CIERRE)) {
            return null;
        }

        return new CitaMedica(UUID.randomUUID().toString(),
                nombrePaciente.trim(), medicoId.trim(), tipoCita, inicio, fin);
    }

    private static boolean esDatosInvalidos(String nombrePaciente, String medicoId,
                                            TipoCita tipoCita, LocalDateTime inicio) {
        return nombrePaciente == null || nombrePaciente.isBlank()
                || medicoId == null || medicoId.isBlank()
                || tipoCita == null
                || inicio == null;
    }

    public boolean seSolapaCon(CitaMedica otra) {
        return seSolapaCon(otra.getInicio(), otra.getFin());
    }

    public boolean seSolapaCon(LocalDateTime otroInicio, LocalDateTime otroFin) {
        return this.inicio.isBefore(otroFin) && otroInicio.isBefore(this.fin);
    }

    public boolean estaEnHorario() {
        return !inicio.toLocalTime().isBefore(HorarioAtencion.APERTURA)
                && !fin.toLocalTime().isAfter(HorarioAtencion.CIERRE);
    }

    public double calcularCosto() {
        double costo = tipoCita.getTarifaBase();
        if (esFinDeSemana()) {
            costo = costo * (1 + HorarioAtencion.RECARGO_FIN_DE_SEMANA);
        }
        return costo;
    }

    public double calcularCostoBruto() {
        return tipoCita.getTarifaBase();
    }

    public boolean esFinDeSemana() {
        DayOfWeek dia = inicio.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }

    public String getId() {
        return id;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public String getMedicoId() {
        return medicoId;
    }

    public TipoCita getTipoCita() {
        return tipoCita;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }
}