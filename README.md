# Los_bichos — Gestión de Eventos

## Integrantes
- Sebastian Cortes Arango
- Juan Camilo Ortiz Garcia
- Juan Pablo Larrea Betancur

---

## Descripción
Sistema de escritorio en **Java 21 + JavaFX** para gestionar eventos, recintos, entradas, compras, pagos e incidencias, aplicando patrones de diseño GoF y principios SOLID.

---

## Compilar y ejecutar

**Requisitos:** JDK 21, JavaFX SDK 21, Maven 3.8+

```bash
# Clonar
git clone https://github.com/IngScortes/Los_bichos.git
cd Los_bichos/LosBichos

# Compilar y probar
mvn compile
mvn test
```

**Ejecutar en IntelliJ:** Run → Edit Configurations → Application
- Main class: `com.pgii.eventos.app.Main`
- VM options: `--module-path "ruta/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml`

---

## Patrones de diseño

### Command — Acciones reversibles sobre eventos y compras
Encapsula operaciones como publicar un evento o cancelar una compra, permitiendo deshacerlas.
```java
public interface Comando { void ejecutar(); void deshacer(); }

// El invocador mantiene historial con una pila
public void ejecutar(Comando cmd) { cmd.ejecutar(); historial.push(cmd); }
public void deshacer()           { historial.pop().deshacer(); }
```

### Observer — Notificaciones automáticas de cambios de estado
Cuando un evento cambia de estado, notifica automáticamente a todos los observadores registrados.
```java
public interface Observador { void notificar(String evento, String mensaje, Object fuente); }

// En Evento.setEstado()
notificarObservadores("ESTADO_EVENTO", "El evento " + nombre + " cambió a " + estado);
```

### Strategy — Métodos de pago intercambiables
Permite cambiar entre Efectivo, Tarjeta de crédito y PSE sin modificar la lógica del cliente.
```java
public interface MetodoPago { ResultadoPago procesarPago(double monto); }

MetodoPago metodo = new PagoPSE(email, password); // intercambiable
ResultadoPago resultado = metodo.procesarPago(150_000);
```

### Builder — Construcción de compras complejas
Construye un objeto `Compra` paso a paso validando campos obligatorios al final.
```java
Compra compra = new CompraBuilder()
    .setIdCompra("C-001")
    .setUsuario(usuario)
    .setEvento(evento)
    .addItem(entrada)
    .build(); // valida que id, usuario y evento no sean nulos
```

### Factory — Creación de eventos con políticas automáticas
Centraliza la creación de eventos asignando políticas según su categoría.
```java
Evento concierto = EventoFactory.crearEvento("E-001", "Rock Fest",
    CategoriaEvento.CONCIERTO, ...);
// Asigna automáticamente: "No reembolsable. Cancelación con 48h da derecho a 50%."
```

### Adapter — Exportación de reportes a PDF
Adapta la API de Apache PDFBox a la interfaz `IReporteExporter` que usa el sistema.
```java
public interface IReporteExporter { void exportar(List<String[]> filas, String ruta) throws Exception; }

IReporteExporter exporter = new PDFBoxAdapter();
exporter.exportar(filas, "reporte.pdf"); // el cliente no conoce PDFBox
```

### Composite — Jerarquía de recintos (Recinto → Zona → Asiento)
Permite calcular capacidad y ocupación de forma uniforme en cualquier nivel de la jerarquía.
```java
public interface ComponenteRecinto {
    int getCapacidadTotal();
    int getOcupacionActual();
    List<ComponenteRecinto> getHijos();
}
// Mismo código para un asiento individual o un recinto completo
```

### Decorator — Servicios adicionales a compras
Agrega parqueadero o merchandising a una entrada de forma dinámica y apilable.
```java
ItemCompra entrada = new EntradaVIP("E-001");
entrada = new ParqueaderoDecorator(entrada);    // +$10.000
entrada = new MerchandisingDecorator(entrada);  // +$25.000
// getPrecio() suma todos los costos automáticamente
```

---

## Principios SOLID

| Principio | Aplicación en el proyecto |
|-----------|--------------------------|
| **S** — Responsabilidad única | `EventoFactory` solo crea eventos. `CompraService` solo gestiona compras. Cada clase tiene una razón para cambiar. |
| **O** — Abierto/Cerrado | Agregar `PagoCripto` o `ExcelAdapter` no requiere modificar código existente, solo implementar la interfaz. |
| **L** — Sustitución de Liskov | Cualquier `MetodoPago`, `Comando` u `Observador` concreto puede reemplazar a su interfaz sin afectar al cliente. |
| **I** — Segregación de interfaces | `IReporteExporter`, `Observador` y `Comando` son interfaces mínimas; ninguna fuerza métodos innecesarios. |
| **D** — Inversión de dependencias | `InvocadorComandos` depende de `Comando` (interfaz), no de comandos concretos. El cliente usa `IReporteExporter`, no `PDFBoxAdapter`. |

---

## Repositorio
[https://github.com/IngScortes/Los_bichos](https://github.com/IngScortes/Los_bichos)