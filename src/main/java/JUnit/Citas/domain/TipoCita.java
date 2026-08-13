package JUnit.Citas.domain;

public class TipoCita {

    public static final TipoCita GENERAL = new TipoCita("Consulta general", 25.00, 30);
    public static final TipoCita ESPECIALISTA = new TipoCita("Consulta con especialista", 70.00, 45);
    public static final TipoCita CHECKUP = new TipoCita("Chequeo integral", 50.00, 60);

    private final String descripcion;
    private final double tarifaBase;
    private final int duracionMinutos;

    private TipoCita(String descripcion, double tarifaBase, int duracionMinutos) {
        this.descripcion = descripcion;
        this.tarifaBase = tarifaBase;
        this.duracionMinutos = duracionMinutos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getTarifaBase() {
        return tarifaBase;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }
}