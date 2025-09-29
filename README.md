# Tarea Java por parejas

``Francisco Alba y Rocío Luque — Curso 2025/26``



<br>

# Índice de contenidos

- [Cómo empezar](#cómo-empezar)
- [Bloque A — Fundamentos y calentamiento](#bloque-a-fundamentos-y-calentamiento-20-30)
- [Bloque B — Jerarquía sealed y switch moderno](#bloque-b-—-jerarquía-sealed-y-switch-moderno)
- [Bloque C — Text Blocks y exportación](#bloque-c-—-text-blocks-y-exportación)
- [Bloque D — Colecciones y orden](#bloque-d-—-colecciones-y-orden)
- [Bloque E — Pattern Matching + Record Patterns (Java 21)](#bloque-e-—-pattern-matching--record-patterns-java-21)
- [Bloque F — Errores y robustez](#bloque-f-—-errores-y-robustez)
- [Bloque G — Extensión opcional](#bloque-g-—-extensión-opcional)
- [Entrega sugerida](#entrega-sugeridaa)
- [Rúbrica](#rúbrica)
- [Apéndice — Snippets útiles](#apéndice-—-snippets-útiles)


# Proyecto GeoNotes – Organización en Packages
  
Venimos de trabajar principalmente con **Kotlin** siguiendo el modelo **MVC** (Model–View–Controller), donde solemos estructurar los proyectos en packages como `model`, `data`, `service`, `ui` y `utils`, en la que cada capa tiene una responsabilidad clara:

- **Model** → contiene las entidades del dominio (por ejemplo, `User`, `Note`…), sus atributos y reglas básicas.
- **View (ui)** → gestiona la interfaz con el usuario (en nuestro caso, normalmente terminal o interfaz gráfica).
- **Controller (app)** → coordina la lógica, recibe las acciones de la vista y las traduce en operaciones sobre el modelo.

A menudo lo complementábamos con otros packages:
- **data** → acceso a la base de datos (DAOs, repositorios).
- **service** → lógica de negocio (procesar, validar, aplicar reglas).
- **utils** → funciones auxiliares reutilizables.

---

## Flujo de trabajo en pareja: IntelliJ Idea Code With Me

Para este proyecto, hemos trabajado en pareja usando **IntelliJ Code With Me** (aprovechando la licencia Student Pack que nos ofrece JetBrains por ser alumnos del I.E.S. Rafael Alberti), editando directamente sobre la rama `main` y haciendo commits regulares. Esto nos ha permitido avanzar juntos sin preocuparnos por la gestión de varias ramas ni conflictos complejos de merge, lo que simplifica mucho la coordinación.

Trabajar en tiempo real con Code With Me nos ha dado la ventaja de poder discutir decisiones de diseño, revisar cambios al momento y resolver dudas al instante. Cada miembro podía ver lo que el otro estaba haciendo, lo que hizo que la colaboración fuera más fluida y eficiente que si trabajáramos por separado.

Además, los commits claros y descriptivos no han sirvido como registro de todo lo que íbamos implementando. Esto nos ha permitido mantener un historial ordenado y fácil de seguir, lo que facilita tanto la revisión del proyecto como su entrega final.  

## Organización en este proyecto Java

En este caso, al trabajar con **Java** y con un proyecto más pequeño, hemos decidido simplificar la estructura. Para mantener claridad y buenas prácticas, hemos optado por la siguiente distribución de packages:

- **app** → Contiene el punto de entrada de la aplicación (`GeoNotes`).
- **model** → Incluye las entidades principales del dominio: `GeoPoint`, `GeoArea`, `Note`, y los adjuntos (`Attachment`, `Photo`, `Audio`, `Link`).
- **export** → Agrupa todo lo relacionado con la exportación de datos (`Exporter`, `AbstractExporter`, `JsonExporter`).
- **service** → Lógica auxiliar como búsqueda, descripción y organización temporal (`Match`, `Describe`, `Timeline`).

De esta forma, y para evitar tener packages vacíos y mantener la organización clara, separando responsabilidades sin añadir complejidad innecesaria, hemos decidido:

- No incluir `data` ya que no presenta persistencia en base de datos.
- Excluir `ui` puesto que el proyecto concentra la presentación gráfica en la propia app.
- Se centra en el **modelo** y en **servicios de apoyo**, además de la parte de **exportación**.


### Ajuste de packages: `Timeline.Render`

Para que el proyecto compile correctamente y se respete la **relación `sealed`** entre `AbstractExporter` y sus implementaciones, hemos decidido mover la clase interna `Render` de `Timeline` al package `export`.

#### Motivo:

- En Java, las clases **`sealed`** solo permiten heredar de ellas a clases que estén:  
  1. Declaradas explícitamente en `permits`.  
  2. Visibles desde el package de la clase `sealed`.  

- Inicialmente, `Timeline.Render` estaba en `service`, mientras que `AbstractExporter` estaba en `export`.  
- Aunque podríamos haber hecho `Render` **`public static`** y modificar su constructor para acceder a las notas, esto habría implicado **cambios significativos en la lógica original de `Timeline`**, que preferimos mantener intacta por ser código del profesor.

#### Beneficios del cambio:

1. `Timeline.Render` queda en el **mismo package que `AbstractExporter`**, cumpliendo con los requisitos de la clase `sealed`.  
2. Evitamos modificar la lógica interna de `Timeline`, minimizando el riesgo de introducir errores.  
3. Mantiene la **estructura limpia y coherente** de packages (`export` para exportadores, `service` para lógica de apoyo, `model` para entidades, `app` para el main).  

En resumen, el cambio es una **adaptación técnica necesaria** para cumplir con las restricciones de Java `sealed` sin alterar la funcionalidad original del proyecto.


---

## Bloque A - Fundamentos y calentamiento (20-30')

### A1. Validación y excepciones

Condicionales ``if`` añadidas para controlar los nuevos requirimientos:
```java
public record Note(long id, String title, String content, GeoPoint location, Instant createdAt, Attachment attachment) {
    public Note {
        ...
        if(content != null) content = content.trim();
        if (content == null || content.trim().isEmpty()) content = "-";
        // Otra opción sería utilizar un operador ternario de la siguiente forma:
        //
        // content = (content == null || content.trim().isEmpty()) ? "-" : content.trim();
        ...
    }
}
```

### A2. Equals/HashCode vs. Records (conceptual)

Para este ejercicio hemos creado la clase `LegacyPoint`, que es una clase clásica de Java con los campos `lat` y `lon`, un constructor, getters, y los métodos `equals()`, `hashCode()` y `toString()` implementados manualmente. Esto nos permite comparar su comportamiento con el record `GeoPoint`.

```java
package com.example.geonotesteaching.model;

import java.util.Objects;

public class LegacyPoint {
    private double lat;
    private double lon;

    public LegacyPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() { return lat; }
    public double getLon() { return lon; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LegacyPoint)) return false;
        LegacyPoint that = (LegacyPoint) o;
        return Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lon, lon) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }

    @Override
    public String toString() {
        return "LegacyPoint{" + "lat=" + lat + ", lon=" + lon + '}';
    }
}
```

**Nota de diseño:**  
Hemos hecho los campos de `LegacyPoint` privados y añadido getters como buena práctica de Java. Esto protege la información interna y sigue principios SOLID como **responsabilidad única** y **abierto/cerrado**, evitando que otras clases dependan directamente de la implementación interna.

**Tabla comparativa entre ``GeoPoint`` (record) y ``LegacyPoint`` (clásica)**:

| Característica           | GeoPoint (record)                             | LegacyPoint (clásica)                               |
|--------------------------|-----------------------------------------------|-----------------------------------------------------|
| Declaración              | `public record GeoPoint(double lat, double lon)` | `public class LegacyPoint { double lat, lon; ... }` |
| Constructor              | Generado automáticamente; validaciones opcionales en compact constructor | Manual (`public LegacyPoint(double lat, double lon)`) |
| Getters                  | Generados automáticamente (`lat()`, `lon()`)  | Manual (`getLat()`, `getLon()`)                    |
| equals / hashCode / toString | Automáticos                                | Manuales con `Objects.hash()` y `instanceof`       |
| Inmutabilidad            | Sí, por defecto                                | No, campos mutables a menos que sean `final`      |
| Código necesario         | Muy conciso                                   | Mucho más largo, repetitivo                        |
| Uso recomendado          | Datos simples, inmutables                     | Necesitas setters, herencia o lógica compleja     |

---

## Bloque B — Jerarquía *sealed* y `switch` moderno (25–35′)

### B1. Nuevo subtipo: `Video`

Nuevos ``case`` añadidos a ``public static String describeAttachment(Attachment a)``:

```java
final class Describe {
    public static String describeAttachment(Attachment a) {
        return switch (a) {
            ...
            case Video v when v.seconds() > 120 -> "📹 Vídeo largo";
            case Video v -> "📹 Video";
        };
    }
}
```

Como se puede observar en la siguiente captura, el ``switch`` obliga a cubrir ``Video``.

<img width="1231" height="298" alt="Captura de pantalla 2025-09-30 000802" src="https://github.com/user-attachments/assets/1148690a-3f19-4570-b652-7fa32f537113" />

### B2. Formato corto vs. largo en `switch`

**Objetivo:** usar `yield` con bloques.

* Cambia alguna rama de `Describe` a bloque:

  ```java
  case Audio a when a.duration() > 300 -> {
    var mins = a.duration() / 60;
    yield "🎵 Audio (" + mins + " min)";
  }
  ```
* Asegúrate de compilar y probar.

---

## Bloque C — *Text Blocks* y exportación (20–25′)

### C1. Export JSON pretty

**Objetivo:** mejorar legibilidad del JSON.

* En `Timeline.Render.export()`, ajusta el *text block* para alinear y sangrar mejor.
* Escapa comillas del `content` si hiciera falta (p. ej., `replace("\"","\\\"")` antes de `formatted`).

### C2. Export Markdown (extra)

**Objetivo:** practicar *text blocks*.

* Crea `MarkdownExporter` (implementa `Exporter`) que genere:

  ```md
  # GeoNotes
  - [ID 1] Título — (lat, lon) — YYYY-MM-DD
  ```
* Muestra su salida desde la CLI (añade opción 6: “Exportar Markdown”).

---

## Bloque D — Colecciones y orden (25–30′)

### D1. Orden por fecha y límite

**Objetivo:** practicar Streams y Comparator.

* Añade método en `Timeline`:

  ```java
  public java.util.List<Note> latest(int n)
  ```

  que devuelva las `n` notas más recientes (por `createdAt` descendente).
* Añade opción en CLI: “Listar últimas N”.

### D2. Búsqueda con varios criterios

**Objetivo:** filtros encadenados.

* En CLI, añade una opción “Buscar avanzada”:

  * Por rango de lat/lon (ej.: lat entre A–B).
  * Por palabra clave en `title` o `content`.
* Reutiliza `Match.isInArea` o crea un método auxiliar.

---

## Bloque E — *Pattern Matching* + *Record Patterns* (Java 21) (20–30′)

> El proyecto docente usa un enfoque clásico en `Match`, pero ahora practicaremos lo nuevo.

### E1. `instanceof` con patrón

**Objetivo:** simplificar *casting*.

* En `Describe` añade un método:

  ```java
  static int mediaPixels(Object o)
  ```

  que:

  * Si es `Photo p`, devuelva `p.width() * p.height()`.
  * Si es `Video v`, devuelva `v.width() * v.height()`.
  * Si no, 0.
* Implementa con `if (o instanceof Photo p) { ... }`.

### E2. *Record patterns* en `if` o `switch`

**Objetivo:** desestructurar con patrón.

* Crea método en `Match`:

  ```java
  static String where(GeoPoint p)
  ```

  que use:

  ```java
  return switch (p) {
    case GeoPoint(double lat, double lon) when lat == 0 && lon == 0 -> "ORIGIN";
    case GeoPoint(double lat, double lon) when lat == 0 -> "Equator";
    case GeoPoint(double lat, double lon) when lon == 0 -> "Greenwich";
    case GeoPoint(double lat, double lon) -> "(" + lat + "," + lon + ")";
  };
  ```
* Añade opción CLI para consultar `where`.

---

## Bloque F — Errores y robustez (15–20′)

### F1. Manejo de `InputMismatch`/`NumberFormat`

**Objetivo:** entradas seguras.

* Asegura que **todas** las lecturas de números usan `Double.parseDouble(scanner.nextLine())` y están en `try/catch` con mensajes claros (ya está iniciado en `GeoNotes`).

### F2. Comprobaciones nulas

**Objetivo:** práctica “clásica” (sin null-safety de Kotlin).

* Si `label` en `Link` es nulo/vacío, muestra la `url` al exportar (ya implementado en `Describe`; revisa consistencia en exportadores).

---

## Bloque G — Extensión opcional (si hay tiempo)

### G1. Vista invertida (java 21, **Sequenced**)

**Objetivo:** mostrar la API moderna sin cambiar el enfoque clásico.

* Sustituye el `Map<Long, Note>` interno por `SequencedMap<Long,Note>` (con `LinkedHashMap`).
* Añade método:

  ```java
  public java.util.Collection<Note> reversed() { return notes.reversed().values(); }
  ```
* Opción CLI: “Listar (reversed)”.

### G2. Demo *virtual threads* (muy opcional)

**Objetivo:** idea general de Loom.

* Crea `VirtualDemo.runIO()` que lance \~50 tareas “simuladas” (sleep 200–300 ms) con:

  ```java
  try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) { ... }
  ```
* Muestra el hilo actual en cada tarea. **No** mezclar con la lógica del proyecto (solo demo).

---

## Entrega sugerida

* Cambios en el código + pequeño `README` con:

  * Lista de ejercicios hechos.
  * Notas sobre decisiones de diseño y Java vs Kotlin (2–4 bullets).

## Rúbrica (10 ptos)

* **A–B**: Validación, sealed + switch/guards (4 ptos).
* **C–D**: Text Blocks/Exporter, Streams/orden (3 ptos).
* **E**: Pattern matching + record patterns (2 ptos).
* **F**: Robustez de entradas + nulos (1 pto).
  *(+1 extra por G1 o G2)*

---

## Apéndice — Snippets útiles

**Orden por fecha:**

```java
var latest = timeline.getNotes()
    .values().stream()
    .sorted(java.util.Comparator.comparing(Note::createdAt).reversed())
    .limit(n)
    .toList();
```

**Text Block con `.formatted(...)`:**

```java
String json = """
  { "title": "%s", "content": "%s" }
  """.formatted(title, content.replace("\"","\\\""));
```

**`instanceof` con patrón:**

```java
if (obj instanceof Photo p) {
    // p ya está casteado a Photo
}
```

**`switch` con `yield`:**

```java
String label = switch (a) {
  case Audio au when au.duration() > 600 -> {
    var min = au.duration() / 60;
    yield "🎵 Podcast (" + min + " min)";
  }
  case Audio au -> "🎵 Audio";
  default -> "Otro";
};
```

---
