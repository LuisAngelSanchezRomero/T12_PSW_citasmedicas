# Sistema de Citas Médicas

Sistema para la gestión de citas médicas que permite **registrar citas**, **calcular su costo** y **validar la disponibilidad de horario**. Implementado en **Java 21** con **Maven**, siguiendo principios de arquitectura limpia (dominio, aplicación e infraestructura) y con pruebas unitarias **JUnit 5** bajo el patrón **AAA (Arrange - Act - Assert)**.

---

## Requisitos

- JDK 21 o superior
- Maven 3.9 o superior

---

## Estructura del proyecto

```
SistemadeCitasMedicas/
├── pom.xml                                    # Configuración Maven (JUnit 5, Java 21)
├── src/main/java/JUnit/Citas/
│   ├── Main.java                              # Punto de entrada / demo por consola
│   ├── domain/                                # Núcleo del negocio (sin dependencias externas)
│   │   ├── CitaMedica.java                    # Entidad: validación, solapamiento, costo
│   │   ├── TipoCita.java                      # Tipos de cita (tarifa base + duración)
│   │   ├── HorarioAtencion.java               # Reglas de horario y recargos
│   │   └── port/RepositorioCitas.java         # Puerto de salida (abstracción de datos)
│   ├── application/                           # Casos de uso
│   │   ├── GestorCitas.java                   # Registrar, calcular costo, disponibilidad
│   │   └── ResultadoRegistro.java             # Resultado de la operación (éxito/motivo)
│   └── infrastructure/                        # Adaptadores
│       └── RepositorioCitasEnMemoria.java     # Implementación en memoria del puerto
└── test/JUnit/Citas/                          # Pruebas unitarias (patrón AAA)
    ├── domain/
    │   ├── CitaMedicaTest.java
    │   └── TipoCitaTest.java
    └── application/
        └── GestorCitasTest.java
```

---

## Reglas de negocio

| Regla | Detalle |
|---|---|
| **Horario de atención** | De `08:00` a `20:00`. La cita debe caber completa dentro de este rango (inicio + duración). |
| **Recargo de fin de semana** | Sábados y domingos: **+30%** sobre la tarifa base. |
| **Disponibilidad** | Un médico no puede tener dos citas **superpuestas**. Médicos distintos sí pueden compartir horario. |
| **Datos obligatorios** | Nombre del paciente, identificador del médico, tipo de cita y fecha/hora de inicio. |

### Tipos de cita

| Tipo | Tarifa base | Duración |
|---|---|---|
| `GENERAL` (Consulta general) | $25.00 | 30 min |
| `ESPECIALISTA` (Consulta con especialista) | $70.00 | 45 min |
| `CHECKUP` (Chequeo integral) | $50.00 | 60 min |

**Ejemplo de costo:** un `ESPECIALISTA` en sábado cuesta `$70.00 × 1.30 = $91.00`.

---

## Cómo ejecutar

### Compilar y ejecutar los tests

```
mvn test
```

Los tests están en la carpeta `test/` (configurada en el `pom.xml` mediante `<testSourceDirectory>`). Verás el resumen:

```
Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Cada test imprime por consola los datos que verifica (tarifas, costos, disponibilidad, solapamientos).

### Ejecutar la aplicación demo

```
mvn compile
java -cp target\classes JUnit.Citas.Main
```

En Windows, si los acentos se ven distorsionados en la consola:

```
chcp 65001
java -Dstdout.encoding=UTF-8 -cp target\classes JUnit.Citas.Main
```

---

## Patrón AAA en las pruebas

Todas las pruebas siguen el patrón **Arrange - Act - Assert** de forma explícita:

```java
@Test
@DisplayName("Calcula el costo con recargo de fin de semana")
void calculaCostoConRecargoFinDeSemana() {

    // Arrange
    CitaMedica cita = CitaMedica.crear("Ana Pérez", "MED-001", TipoCita.GENERAL, SABADO_10AM);

    // Act
    double costo = cita.calcularCosto();

    // Assert
    assertEquals(32.50, costo, 0.001);
}
```

- **Arrange**: prepara los datos y el escenario.
- **Act**: ejecuta la operación bajo prueba.
- **Assert**: verifica el resultado esperado.

---

## API de `GestorCitas`

| Método | Descripción |
|---|---|
| `ResultadoRegistro registrarCita(paciente, medicoId, tipoCita, inicio)` | Registra la cita si el horario está libre. Devuelve `ResultadoRegistro` con la cita creada o el motivo del rechazo (sin lanzar excepciones). |
| `double calcularCosto(CitaMedica cita)` | Calcula el costo total aplicando el recargo de fin de semana si corresponde. |
| `boolean horarioDisponible(medicoId, inicio, tipoCita)` | Indica si el médico está libre en ese horario. |
| `List<CitaMedica> listarCitasDelMedico(medicoId)` | Devuelve todas las citas registradas de un médico. |

### Manejo de errores sin excepciones

El proyecto evita excepciones personalizadas. Los casos de fallo se comunican de forma explícita y testeable:

- `CitaMedica.crear(...)` devuelve `null` si los datos son inválidos o la cita no cabe en el horario.
- `GestorCitas.registrarCita(...)` devuelve `ResultadoRegistro` con `esExitoso()`, `getMotivo()` y `getCita()`.

---

## Diseño y decisiones

- **Arquitectura limpia**: el dominio no conoce la capa de datos; `GestorCitas` depende del puerto `RepositorioCitas`, no de una implementación concreta.
- **Inversión de dependencias**: fácil de probar inyectando `RepositorioCitasEnMemoria` (o un fake).
- **Inyección por constructor** en `GestorCitas`.
- **Sin enums ni excepciones** como parte de la superficie de fallo, simplificando la legibilidad y las pruebas.