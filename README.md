# Tarea Java por parejas

``Francisco Alba y Rocío Luque — Curso 2025/26``



<br>

# Índice de contenidos

- [Flujo de trabajo en pareja: IntelliJ Idea Code With Me](#flujo-de-trabajo-en-pareja-intellij-idea-code-with-me)
- [Organización en Packages](#organización-en-packages)
    - [Organización en este proyecto Java](#organización-en-este-proyecto-java)
    - [Ajuste de packages: `Timeline.Render`](#ajuste-de-packages-timelinerender)
- [Bloque A — Fundamentos y calentamiento (20-30')](#bloque-a---fundamentos-y-calentamiento-20-30)
    - [A1. Validación y excepciones](#a1-validaci%C3%B3n-y-excepciones)
    - [A2. Equals/HashCode vs. Records (conceptual)](#a2-equalshashcode-vs-records-conceptual)
- [Bloque B — Jerarquía *sealed* y `switch` moderno (25–35′)](#bloque-b--jerarqu%C3%ADa-sealed-y-switch-moderno-2535)
    - [B1. Nuevo subtipo: `Video`](#b1-nuevo-subtipo-video)
    - [B2. Formato corto vs. largo en `switch`](#b2-formato-corto-vs-largo-en-switch)
- [Bloque C — *Text Blocks* y exportación (20–25′)](#bloque-c--text-blocks-y-exportaci%C3%B3n-2025)
    - [C1. Export JSON pretty](#c1-export-json-pretty)
    - [C2. Export Markdown (extra)](#c2-export-markdown-extra)
- [Bloque D — Colecciones y orden (25–30′)](#bloque-d--colecciones-y-orden-2530)
    - [D1. Orden por fecha y límite](#d1-orden-por-fecha-y-l%C3%ADmite)
    - [D2. Búsqueda con varios criterios](#d2-b%C3%BAsqueda-con-varios-criterios)
- [Bloque E — *Pattern Matching* + *Record Patterns* (Java 21) (20–30′)](#bloque-e--pattern-matching--record-patterns-java-21-2030)
    - [E1. `instanceof` con patrón](#e1-instanceof-con-patr%C3%B3n)
    - [E2. *Record patterns* en `if` o `switch`](#e2-record-patterns-en-if-o-switch)
- [Bloque F — Errores y robustez (15–20′)](#bloque-f--errores-y-robustez-1520)
    - [F1. Manejo de `InputMismatch`/`NumberFormat`](#f1-manejo-de-inputmismatchnumberformat)
    - [F2. Comprobaciones nulas](#f2-comprobaciones-nulas)
- [Bloque G — Extensión opcional (si hay tiempo)](#bloque-g--extensi%C3%B3n-opcional-si-hay-tiempo)
    - [G1. Vista invertida (java 21, **Sequenced**)](#g1-vista-invertida-java-21-sequenced)
    - [G2. Demo *virtual threads* (muy opcional)](#g2-demo-virtual-threads-muy-opcional)


# Proyecto GeoNotes

## Flujo de trabajo en pareja: IntelliJ Idea Code With Me

Para este proyecto, hemos trabajado en pareja usando **IntelliJ Code With Me** (aprovechando la licencia Student Pack que nos ofrece JetBrains por ser alumnos del I.E.S. Rafael Alberti), editando directamente sobre la rama `main` y haciendo commits regulares. Esto nos ha permitido avanzar juntos sin preocuparnos por la gestión de varias ramas ni conflictos complejos de merge, lo que simplifica mucho la coordinación.

Trabajar en tiempo real con Code With Me nos ha dado la ventaja de poder discutir decisiones de diseño, revisar cambios al momento y resolver dudas al instante. Cada miembro podía ver lo que el otro estaba haciendo, lo que hizo que la colaboración fuera más fluida y eficiente que si trabajáramos por separado.

Además, los commits claros y descriptivos no han sirvido como registro de todo lo que íbamos implementando. Esto nos ha permitido mantener un historial ordenado y fácil de seguir, lo que facilita tanto la revisión del proyecto como su entrega final.  

---

## Organización en Packages
  
Venimos de trabajar principalmente con **Kotlin** siguiendo el modelo **MVC** (Model–View–Controller), donde solemos estructurar los proyectos en packages como `model`, `data`, `service`, `ui` y `utils`, en la que cada capa tiene una responsabilidad clara:

- **Model** → contiene las entidades del dominio (por ejemplo, `User`, `Note`…), sus atributos y reglas básicas.
- **View (ui)** → gestiona la interfaz con el usuario (en nuestro caso, normalmente terminal o interfaz gráfica).
- **Controller (app)** → coordina la lógica, recibe las acciones de la vista y las traduce en operaciones sobre el modelo.

A menudo lo complementábamos con otros packages:
- **data** → acceso a la base de datos (DAOs, repositorios).
- **service** → lógica de negocio (procesar, validar, aplicar reglas).
- **utils** → funciones auxiliares reutilizables.

### Organización en este proyecto Java

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

En nuestro caso, hemos optado por aplicar el formato largo en el ``case Link l`` ya que es el caso que contiene una lógica que justifique su uso.

```java
final class Describe {
    public static String describeAttachment(Attachment a) {
        return switch (a) {
            ...
            case Link l -> {
                String label = (l.label() == null || l.label().isEmpty()) ? l.url() : l.label();
                yield "🔗 " + label;
            }
            ...
        };
    }
}
```

---

## Bloque C — *Text Blocks* y exportación (20–25′)

### C1. Export JSON pretty

Se ha modificado la exportación JSON para mejorar la legibilidad y asegurar la validez del contenido. Ahora cada nota se representa en múltiples líneas con sangría uniforme, incluyendo un bloque propio para `"location"` con sus campos `lat` y `lon`, y la lista completa de notas se alinea dentro del bloque `"notes"`. Además, se escapan las comillas dobles en `content` mediante `replace("\"", "\\\"")` para evitar que rompan el JSON. Para facilitar la escritura y visualización de este formato se utilizan text blocks (`"""`), permitiendo estructurar la cadena de forma clara y similar a Kotlin. Estas mejoras no cambian la validez del JSON, pero hacen que sea mucho más legible y fácil de depurar o documentar.

```java
public final class Timeline {
    private final Map<Long, Note> notes = new LinkedHashMap<>();

    public void addNote(Note note) { notes.put(note.id(), note); }
    public Note getNote(long id) { return notes.get(id); }
    public Map<Long, Note> getNotes() { return notes; }

    // Esta clase final genera la salida JSON usando 'text blocks'.
    public final class Render extends AbstractExporter implements Exporter {
        @Override public String export() {
            var notesList = notes.values().stream()
                // Un 'text block' es una cadena de texto multilinea que no necesita
                // concatenación ni caracteres de escape para las comillas.
                .map(note -> {
                    // Escapamos las comillas dobles en content
                    String safeContent = note.content().replace("\"", "\\\"");
                    return """
                           {
                             "id": %d,
                             "title": "%s",
                             "content": "%s",
                             "location": {
                               "lat": %f,
                               "lon": %f
                             },
                             "createdAt": "%s"
                           }
                         """.formatted(
                            note.id(),
                            note.title(),
                            safeContent,
                            note.location().lat(),
                            note.location().lon(),
                            note.createdAt()
                    );
                })
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.joining(",\n"));

            return """
                   {
                     "notes": [
                   %s
                     ]
                   }
                   """.formatted(notesList);
        }
    }
}
```

### C2. Export Markdown (extra)

Hemos creado la clase ``MarkdownExporter``, lo que nos permite exportar las notas en formato .md siguiendo el patrón requerido ``[ID 1] Título — (lat, lon) — YYYY-MM-DD``

```java
public final class MarkdownExporter extends AbstractExporter {

    private final List<Note> notes;

    public MarkdownExporter(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public String export() {
        var builder = new StringBuilder("# GeoNotes\n\n");
        for (Note note : notes) {
            builder.append("- [ ID ")
                    .append(note.id())
                    .append(" ] ")
                    .append(note.title())
                    .append(" — (")
                    .append(note.location().lat())
                    .append(", ")
                    .append(note.location().lon())
                    .append(") — ")
                    .append(note.createdAt().toString().substring(0, 10)) // YYYY-MM-DD
                    .append("\n");
        }
        return builder.toString();
    }
}
```

En la siguiente captura se ve un ejemplo de la salida por consola del formato Markdown:

<img width="2269" height="592" alt="Captura de pantalla 2025-09-30 005423" src="https://github.com/user-attachments/assets/c740190d-437f-4828-93cc-221d2d4584bb" />

---

## Bloque D — Colecciones y orden (25–30′)

### D1. Orden por fecha y límite

En nuestro caso, para resolver el ejercicio de listar las últimas **N** notas optamos por implementar el método `latest(int n)` usando una lambda directamente en el `Comparator`:

En ``Timeline``:

```java
public List<Note> latest(int n) {
        return notes.values().stream()
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt())) // descendente
                .limit(n)
                .toList();
    }
```

En ``GeoNotes`` añadimos:

```java
private static void listarUltimasNotas(Timeline timeline, Scanner scanner) {
        System.out.print("Introduce el número de notas a listar: ");
        int n = Integer.parseInt(scanner.nextLine());

        var latestNotes = timeline.latest(n);
        latestNotes.forEach(note ->
                System.out.println("- " + note.title() + " (" + note.createdAt() + ")"));
    }
```
En lugar de usar la forma propuesta en el snippet del profesor con:

```java
var latest = timeline.getNotes()
.values().stream()
.sorted(java.util.Comparator.comparing(Note::createdAt).reversed())
.limit(n)
.toList();
```

Hemos elegido esta variante porque queríamos explorar las distintas formas que ofrece Java para expresar comparadores, comparando así con el enfoque más declarativo de Kotlin (similar a usar sortedByDescending).

Un ejemplo de la salida por consola es la mostrada a continuación:

<img width="1289" height="298" alt="captura_listadoReverso" src="https://github.com/user-attachments/assets/3809b0da-b246-4dda-a1dc-01ea946525d9" />

### D2. Búsqueda con varios criterios

Para este bloque, hemos conservado la función proporcionanda originalmente en el proyecto, ``filterNotes``, y creado una nueva función para hacer el filtro por rango de lat/lon (o área).

```java
private static void filterNotesByArea() {
        System.out.println("\n--- Filtrar notas por área geográfica ---");

        System.out.print("Latitud mínima: ");
        double lat1 = Double.parseDouble(scanner.nextLine());
        System.out.print("Latitud máxima: ");
        double lat2 = Double.parseDouble(scanner.nextLine());
        System.out.print("Longitud mínima: ");
        double lon1 = Double.parseDouble(scanner.nextLine());
        System.out.print("Longitud máxima: ");
        double lon2 = Double.parseDouble(scanner.nextLine());

        double minLat = Math.min(lat1, lat2);
        double maxLat = Math.max(lat1, lat2);
        double minLon = Math.min(lon1, lon2);
        double maxLon = Math.max(lon1, lon2);

        GeoPoint topLeft = new GeoPoint(minLat, minLon);
        GeoPoint bottomRight = new GeoPoint(maxLat, maxLon);
        GeoArea area = new GeoArea(topLeft, bottomRight);

        var filtered = timeline.getNotes().values().stream()
                .filter(note -> Match.isInArea(note.location(), area))
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("No se encontraron notas en esa área.");
        } else {
            System.out.println("\n--- Resultados ---");
            filtered.forEach(n -> System.out.printf("ID: %d | %s | %s | (%.2f, %.2f)%n",
                    n.id(), n.title(), n.content(),
                    n.location().lat(), n.location().lon()));
        }
    }
```

También, hemos añadido una llamada a dichas funciones a través de un nuevo menú al que se accede desde el menú principal al elegir la opción ``3. Búsqueda avanzada`` y que llama a la función ``busquedaAvanzada()``.

```java
private static void busquedaAvanzada() {
        System.out.println("""
                ----------Filtrar nota----------
                1. Por palabra clave
                2. Por area geográfica (lat/lon)
                Elige opción: 
                """);
        int option = Integer.parseInt(scanner.nextLine ());

        switch (option) {
            case 1 -> filterNotes();
            case 2 -> filterNotesByArea();
        }
    }
```

Para comprobar el funcionamiento de esta nueva característica, hemos creado dos notas, una ubicada en (1, 1) y otra en (3, 3), llamadas ``Nota 1`` y ``Nota 2`` respectivamente:

<img width="1262" height="300" alt="captura_d2_nota_dentrofuera" src="https://github.com/user-attachments/assets/aea8b4bd-4991-4484-9ae7-cbc006f0a223" />

Tras esto, hemos realizado una búsqueda avanzada por ``lat / lon``, introduciendo los valores ``0, 2, 0 y 2``, respectivamente, para así dejar la primera nota dentro del área y la dos fuera, obteniendo el siguiente resultado por consola:

<img width="1245" height="221" alt="captura_d2_salida" src="https://github.com/user-attachments/assets/7752c527-5214-47a8-a5e4-e9c602eb3290" />

Como se puede apreciar en la captura, la nueva funcionalidad filtra la nota que se encuentra en las coordenadas ``(1,1)``, mientras que omite la segunda nota, que se encuentra en las coordenadas ``(3,3)``.

---

## Bloque E — *Pattern Matching* + *Record Patterns* (Java 21) (20–30′)

> El proyecto docente usa un enfoque clásico en `Match`, pero ahora practicaremos lo nuevo.

### E1. `instanceof` con patrón

En este bloque hemos trabajado con una de las novedades introducidas en Java 21: el pattern matching en instanceof, que simplifica el casting tradicional y hace el código más expresivo y seguro.

Para ello, en la clase ``Describe`` añadimos el método ``mediaPixels(Object o)``, que devuelve el producto de ``width * height`` si el objeto es una ``Photo`` o un ``Video``, y 0 en cualquier otro caso.

  ```java
public static int mediaPixels(Object o) {
    if (o instanceof Photo p) {
        return p.width() * p.height();
    } else if (o instanceof Video v) {
        return v.width() * v.height();
    } else {
        return 0;
    }
}
 ```

En versiones anteriores de Java, este código requeriría un casting explícito tras comprobar el tipo:

```java
if (o instanceof Photo) {
    Photo p = (Photo) o;
    return p.width() * p.height();
}
```

Un ejemplo de uso sería:

```java
System.out.println(mediaPixels(new Photo("paisaje", 1920, 1080))); // 2073600
System.out.println(mediaPixels(new Video("tutorial", 1280, 720))); // 921600
System.out.println(mediaPixels("cadena cualquiera"));              // 0
```

### E2. *Record patterns* en `if` o `switch`

En este ejercicio usamos los record patterns de Java 21 para desestructurar directamente los valores de un ``GeoPoint`` dentro de un ``switch``.
El objetivo es que el método identifique de forma automática si el punto está en el origen, sobre el ecuador, el meridiano de Greenwich, o en unas coordenadas normales.

En la clase ``Match`` añadimos el método:


```java
public static String where(GeoPoint p) {
    return switch (p) {
        case GeoPoint(double lat, double lon) when lat == 0 && lon == 0 -> "ORIGIN";
        case GeoPoint(double lat, double lon) when lat == 0 -> "Equator";
        case GeoPoint(double lat, double lon) when lon == 0 -> "Greenwich";
        case GeoPoint(double lat, double lon) -> "(" + lat + ", " + lon + ")";
    };
}
```
Gracias a esta sintaxis, no hace falta acceder manualmente a ``p.lat()`` o ``p.lon()``: el propio ``switch`` los extrae y permite aplicar condiciones directamente.
Es una forma más moderna y legible de escribir este tipo de comprobaciones.

En ``GeoNotes`` añadimos una nueva opción al menú ``8. Consultar ubicación (where)`` que pide las coordenadas por consola y muestra el resultado que devuelve Match.where():

```java
private static void consultarUbicacion() {
    System.out.println("\n--- Consultar ubicación (where) ---");

    System.out.print("Introduce la latitud: ");
    double lat = Double.parseDouble(scanner.nextLine());
    System.out.print("Introduce la longitud: ");
    double lon = Double.parseDouble(scanner.nextLine());

    GeoPoint point = new GeoPoint(lat, lon);
    String resultado = Match.where(point);

    System.out.println("Resultado: " + resultado);
}
```

Ejemplo de salida por consola:

<img width="1092" height="540" alt="captura-salida-where" src="https://github.com/user-attachments/assets/1f9ceb8e-f1f7-46c6-a957-76c4ebe62255" />

---

## Bloque F — Errores y robustez (15–20′)

### F1. Manejo de `InputMismatch`/`NumberFormat`

En este ejercicio nos hemos asegurado de que en todas las lecturas de datos numéricos se usa ``Double.parseDouble(scanner.nextLine())`` o ``Integer.parseInt(scanner.nextLine())`` para leer los números.

Además, se añaden los bloques ``catch`` para ``NumberFormatException`` y ``IllegalArgumentException``.

La creación de la nota solo se ejecuta si las coordenadas son válidas.

En algunas partes del código, el control de errores ya existía, pero no envolvía correctamente las líneas de lectura de datos, como en el siguiente fragmeto, que ha sido refactorizado para que el control sea real:

```java
try {
    /*
     * Lectura robusta de números: mejor parsear desde nextLine() para controlar errores y limpieza del buffer.
     * (Si fuese una app real, haríamos bucles hasta entrada válida).
     */
    System.out.print("Latitud: ");
    var lat = Double.parseDouble(scanner.nextLine());
    System.out.print("Longitud: ");
    var lon = Double.parseDouble(scanner.nextLine());
    ...
    } catch (NumberFormatException e) {
    System.out.println("❌ Entrada no válida. Por favor, introduce un número válido para latitud y longitud.");
    } catch (IllegalArgumentException e) {
    System.out.println("❌ Error: " + e.getMessage());
    }
```

### F2. Comprobaciones nulas

Para este ejercicio practicamos la forma clásica de Java de manejar posibles `null` (sin null-safety de Kotlin).  
El objetivo era que, si un `Link` tiene su `label` nulo o vacío, se muestre la `url` al exportar.

En el `MarkdownExporter` hemos hecho lo siguiente:

```java
@Override
public String export() {
    var builder = new StringBuilder("# GeoNotes\n\n");
    for (Note note : notes) {
        builder.append("- [ ID ")
                .append(note.id())
                .append(" ] ")
                .append(note.title())
                .append(" — (")
                .append(note.location().lat())
                .append(", ")
                .append(note.location().lon())
                .append(") — ")
                .append(note.createdAt().toString().substring(0, 10)) // YYYY-MM-DD
                .append("\n");
        Attachment a = note.attachment();
        if (a != null) {
            builder.append("    🔗 ").append(Describe.describeAttachment(a)).append("\n");
        }
    }
    return builder.toString();
}
```

- Primero comprobamos si la nota tiene un attachment.
- Luego usamos ``Describe.describeAttachment(a)``, que internamente verifica si el label es ``null`` o vacío; en ese caso muestra la url.
- Finalmente, añadimos la línea al exportador, evitando ``NullPointerException`` y manteniendo consistencia entre exportadores.

De esta manera, aseguramos consistencia en los exportadores y evitamos ``NullPointerException`` al mostrar enlaces.

En el caso de `JsonExporter`, los `Link` ya se incluyen en el `payload` generado, pero al usar `Describe.describeAttachment` en otros exportadores (como Markdown) garantizamos la misma lógica de comprobación nula, mostrando siempre la `url` si `label` es `null` o vacío. Esto asegura consistencia entre diferentes formatos de exportación.

---

## Bloque G — Extensión opcional (si hay tiempo)

### G1. Vista invertida (java 21, **Sequenced**)

Para este ejercicio añadimos la opción de listar las notas en orden inverso sin alterar la estructura principal de ``Timeline``.

- Sustituimos internamente el ``Map`` por un ``SequencedMap`` para mantener el orden de inserción y poder acceder fácilmente a las notas en orden inverso.
- Creamos el método ``reversed()`` en ``Timeline`` que devuelve las notas invertidas.
- En el CLI, extraemos la lógica a una función independiente ``listarNotasInvertidas(Timeline timeline)`` que recorre las notas invertidas y las muestra por consola.
- La opción del menú simplemente llama a esta función, manteniendo el código limpio y modular.

Ejemplo de salida en consola:

<img width="1386" height="442" alt="captura-notas-reversed" src="https://github.com/user-attachments/assets/7edbb7d9-4b43-44b8-a961-232bb05553e5" />

---

### G2. Demo *virtual threads* (muy opcional)

En este ejercicio hemos añadido una clase auxiliar llamada ``VirtualDemo``, ubicada en el paquete ``service``, ya que su función pertenece a la lógica de ejecución del programa y no al modelo de datos ni a la interfaz de usuario.

La clase VirtualDemo utiliza un ``ExecutorService`` basado en hilos virtuales (``Executors.newVirtualThreadPerTaskExecutor()``), una característica moderna de Java que permite ejecutar múltiples tareas de forma concurrente y eficiente sin crear un hilo físico por cada tarea.

```java
public class VirtualDemo {
    public static void runIO() {
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 50; i++) {
                int taskId = i;
                exec.submit(() -> {
                    try {
                        Thread.sleep(200 + (int)(Math.random() * 100)); // 200–300ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Tarea " + taskId + " ejecutada en " + Thread.currentThread());
                });
            }
        }
        System.out.println("✅ Todas las tareas lanzadas (Virtual Threads demo)");
    }
}
```

Cada tarea se lanza mediante ``exec.submit()`` dentro de un bucle de 50 iteraciones, simulando operaciones de entrada/salida con ``Thread.sleep()`` y mostrando el hilo en el que se ejecuta.

Las tareas no se ejecutan de forma secuencial, sino en paralelo, gracias a que cada una se ejecuta en su propio hilo virtual.
Esto demuestra el comportamiento concurrente del sistema y la ligereza de los virtual threads frente a los hilos tradicionales.

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
