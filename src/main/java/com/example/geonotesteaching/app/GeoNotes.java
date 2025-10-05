package com.example.geonotesteaching.app;

import com.example.geonotesteaching.export.MarkdownExporter;
import com.example.geonotesteaching.model.*;
import com.example.geonotesteaching.export.Timeline;

import java.time.Instant;
import java.util.Scanner;
import com.example.geonotesteaching.service.*;

/*
 * GeoNotes — Clase principal con una CLI sencilla.
 *
 * COSAS A FIJARSE (Java 11 → 21):
 * - Java 11: API estándar consolidada; aquí usamos Instant (java.time) para fechas.
 * - Java 14: "switch expressions" con flechas (->) y posibilidad de yield en bloques (en este archivo usamos la forma simple; ver Describe para más).
 * - Java 15: "Text Blocks" (""" ... """) — se usan en Timeline.Render para generar JSON multilínea.
 * - Java 16: "records" (GeoPoint, Note, etc.) — clases inmutables concisas con constructor canónico, equals/hashCode/toString.
 * - Java 17: "sealed classes/interfaces" (Attachment) — jerarquías cerradas que el compilador puede verificar exhaustivamente.
 * - Java 17: pattern matching para instanceof (lo verás en Describe).
 * - Java 21: "record patterns" (lo verás en Match donde se desestructura un record directamente en un switch/if).
 * - Java 21: "Sequenced Collections" (Timeline podría usar SequencedMap/LinkedHashMap.reversed(); aquí mostramos el enfoque clásico, pero coméntalo en clase).
 * - Java 21: "Virtual Threads" (demo aparte en el proyecto moderno; no se usan aquí).
 */
public class GeoNotes {

    /*
     * timeline es el "modelo" de la aplicación: guarda las notas en memoria.
     * Timeline tiene una inner class no estática (Render) que sabe exportar el contenido a JSON con Text Blocks.
     * -> OJO: inner class no estática = necesita una instancia externa para crearse (ver exportNotesToJson()).
     */
    private static final Timeline timeline = new Timeline();

    /*
     * Scanner para leer del stdin. Mantener uno único y reutilizarlo es buena práctica para la CLI.
     */
    private static final Scanner scanner = new Scanner(System.in);

    /*
     * Generador simple de IDs. En un proyecto real, probablemente usarías UUID o una secuencia persistente.
     */
    private static long noteCounter = 1;

    // La clase principal del programa. Contiene el menú interactivo para la CLI.
    public static void main(String[] args) {
        /*
         * Modo "examples":
         * Gradle define una tarea 'examples' que invoca main con el argumento "examples".
         * Útil para mostrar rápidamente la salida JSON sin teclear en la CLI.
         */
        if (args != null && args.length > 0 && "examples".equalsIgnoreCase(args[0])) {
            seedExamples();
            exportNotesToJson();
            return;
        }
        System.out.println("--------------------------------------");
        System.out.println("  📝 Bienvenid@ a la aplicación GeoNotes");
        System.out.println("--------------------------------------");
        boolean running = true;
        while (running) {
            printMenu();
            try {

                /*
                 * Leemos la opción como String y la convertimos a int.
                 * En lugar de nextInt(), usamos nextLine()+parseInt() para evitar "pegarse" con saltos de línea restantes.
                 */
                int choice = Integer.parseInt(scanner.nextLine().trim());

                /*
                 * SWITCH EXPRESSION (Java 14):
                 * - Sintaxis con flechas (->), no hace falta 'break' y es más clara.
                 * - Si usáramos bloques complejos, podríamos usar 'yield' para devolver un valor.
                 * Aquí lo empleamos en su forma de "switch moderno" sobre efectos (no devuelve valor).
                 */
                switch (choice) {
                    case 1 -> createNote();
                    case 2 -> listNotes();
                    case 3 -> busquedaAvanzada();
                    case 4 -> exportNotesToJson();
                    case 5 -> exportMarkDown();
                    case 6 -> listarUltimasNotas(timeline, scanner);
                    case 7 -> consultarUbicacion();
                    case 8 -> listarNotasInvertidas();
                    case 9 -> VirtualDemo.runIO();
                    case 10 -> running = false;
                    default -> System.out.println("❌ Opción no válida. Inténtalo de nuevo.");
                }
            } catch (NumberFormatException e) {
                /*
                 * Manejo de errores "clásico" (en Kotlin tendrías null-safety y Result más idiomáticos).
                 * Aquí mostramos un mensaje claro al usuario.
                 */
                System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
            }
        }
        System.out.println("¡Gracias por usar GeoNotes! 👋");
    }

    private static void exportMarkDown() {
        var exporter = new MarkdownExporter(
                new java.util.ArrayList<>(timeline.getNotes().values())
        );
        System.out.println(exporter.export());
    }

    private static void busquedaAvanzada() {
        System.out.println("""
                ----------Filtrar nota----------
                1. Por palabra clave
                2. Por area geográfica (lat/lon)
                Elige opción: 
                """);
        try{
            int option = Integer.parseInt(scanner.nextLine ());

            switch (option) {
                case 1 -> filterNotes();
                case 2 -> filterNotesByArea();
                default -> System.out.println("❌ Opción no válida. Inténtalo de nuevo.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
        }

    }

    private static void printMenu() {
        System.out.println("\n--- Menú ---");
        System.out.println("1. Crear una nueva nota");
        System.out.println("2. Listar todas las notas");
        System.out.println("3. Búsqueda avanzada");
        System.out.println("4. Exportar notas a JSON (Text Blocks)");
        System.out.println("5. Exportar Markdown");
        System.out.println("6. Listar últimas notas");
        System.out.println("7. Consultar ubicación (where)");
        System.out.println("8. Listar notas invertidas");
        System.out.println("9. VirtualDemo");
        System.out.println("10. Salir");
        System.out.print("Elige una opción: ");
    }

    private static void createNote() {
        System.out.println("\n--- Crear una nueva nota ---");

        // 'var' (Java 10) para inferencia local: útil para código más legible; en APIs públicas, mejor tipos explícitos.
        System.out.print("Título: ");
        var title = scanner.nextLine();
        System.out.print("Contenido: ");
        var content = scanner.nextLine();

        try {
            /*
             * Lectura robusta de números: mejor parsear desde nextLine() para controlar errores y limpieza del buffer.
             * (Si fuese una app real, haríamos bucles hasta entrada válida).
             */
            System.out.print("Latitud: ");
            var lat = Double.parseDouble(scanner.nextLine());
            System.out.print("Longitud: ");
            var lon = Double.parseDouble(scanner.nextLine());

            /*
             * RECORDS (Java 16):
             * - GeoPoint es un record con "compact constructor" que valida rangos (ver clase GeoPoint).
             * - Note también es record; su constructor valida title/location/createdAt.
             * Ventaja: menos boilerplate (constructor/getters/equals/hashCode/toString generados).
             */

            var geoPoint = new GeoPoint(lat, lon);

            /*
             * Instant.now() (java.time) para timestamps — la API java.time es la recomendada desde Java 8.
             * attachment lo dejamos a null en este flujo simple; podrías pedirlo al usuario.
             */
            var note = new Note(noteCounter++, title, content, geoPoint, Instant.now(), null);
            timeline.addNote(note);
            System.out.println("✅ Nota creada con éxito.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada no válida. Por favor, introduce un número válido para latitud y longitud.");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void listNotes() {
        System.out.println("\n--- Notas disponibles ---");
        if (timeline.getNotes().isEmpty()) {
            System.out.println("No hay notas creadas.");
            return;
        }

        /*
         * Bucle forEach sobre el Map<Long, Note>.
         * En Kotlin harías algo similar con forEach y String templates.
         */
        timeline.getNotes().forEach((id, note) -> System.out.printf("ID: %d | Título: %s | Contenido: %s%n", id, note.title(), note.content()));
    }

    private static void filterNotes() {
        System.out.print("\nIntroduce la palabra clave para filtrar: ");
        var keyword = scanner.nextLine();
        System.out.println("\n--- Resultados de búsqueda ---");

        /*
         * Streams (desde Java 8) — muy similares a las funciones de colección en Kotlin.
         * Filtramos por título o contenido y recogemos en una List inmutable (toList() desde Java 16 retorna una lista no modificable).
         */
        var filtered = timeline.getNotes().values().stream().filter(n -> n.title().contains(keyword) || n.content().contains(keyword)).toList();
        if (filtered.isEmpty()) {
            System.out.println("No se encontraron notas con: " + keyword);
            return;
        }
        filtered.forEach(n -> System.out.printf("ID: %d | %s | %s%n", n.id(), n.title(), n.content()));
    }

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

    private static void exportNotesToJson() {
        /*
         * INNER CLASS NO ESTÁTICA:
         * - Timeline.Render es una clase interna "no estática" (inner class).
         * - Por eso se instancia con: timeline.new Render()
         * - Así Render queda LIGADA a ESTA instancia de Timeline (y accede a sus 'notes').
         *
         * Si Render fuera 'static', se instanciaría como 'new Timeline.Render(timeline)' pasando la Timeline explícita.
         */
        var renderer = timeline.new Render(); // ¿Por qué esto no funciona new Timeline().new Render();?

        /*
         * TEXT BLOCKS (Java 15) — ver Timeline.Render:
         * - Allí se usan literales de cadena multilínea """ ... """ para construir JSON legible.
         * - Se normaliza la indentación y no necesitas escapar comillas constantemente.
         */
        String json = renderer.export();

        System.out.println("\n--- Exportando notas a JSON ---");
        System.out.println(json);
    }

    private static void seedExamples() {
        /*
         * Semilla de ejemplo para la tarea Gradle 'examples'.
         * También aquí vemos la jerarquía sellada (sealed) Attachment con tres records:
         *   Photo, Audio, Link — y cómo se pasan a Note como polimorfismo clásico.
         */
        timeline.addNote(new Note(noteCounter++, "Cádiz", "Playita", new GeoPoint(36.5297, -6.2927), Instant.now(), new Photo("u", 2000, 1000)));
        timeline.addNote(new Note(noteCounter++, "Sevilla", "Triana", new GeoPoint(37.3826, -5.9963), Instant.now(), new Audio("a", 320)));
        timeline.addNote(new Note(noteCounter++, "Córdoba", "Mezquita", new GeoPoint(37.8790, -4.7794), Instant.now(), new Link("http://cordoba", "Oficial")));
        /*
         * DONDE VER EL RESTO DE NOVEDADES:
         * - Pattern matching para instanceof + switch con guardas 'when': ver Describe.
         * - Record patterns (Java 21): ver Match (desestructurar GeoPoint en switch/if).
         * - SequencedMap / reversed(): ver Timeline (versión moderna). En este “teaching” usamos LinkedHashMap clásico,
         *   pero explica a los alumnos que en Java 21 LinkedHashMap implementa SequencedMap y se puede pedir la vista invertida.
         * - Virtual Threads: demo aparte en el otro proyecto “moderno” (no se usan aquí).
         */
    }

    private static void listarUltimasNotas(Timeline timeline, Scanner scanner) {
        System.out.print("Introduce el número de notas a listar: ");
        try {
            int n = Integer.parseInt(scanner.nextLine());

            var latestNotes = timeline.latest(n);
            latestNotes.forEach(note ->
                    System.out.println("- " + note.title() + " (" + note.createdAt() + ")"));
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
        }
    }

    private static void consultarUbicacion() {
        System.out.println("\n--- Consultar ubicación (where) ---");

        try {
            System.out.print("Introduce la latitud: ");
            double lat = Double.parseDouble(scanner.nextLine());
            System.out.print("Introduce la longitud: ");
            double lon = Double.parseDouble(scanner.nextLine());

            GeoPoint point = new GeoPoint(lat, lon);
            String resultado = Match.where(point);

            System.out.println("Resultado: " + resultado);
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
        }
    }

    private static void listarNotasInvertidas() {
        System.out.println("--- Listar notas en orden inverso ---");
        for (Note note : GeoNotes.timeline.reversed()) {
            System.out.printf("- %s (%s)%n", note.title(), note.createdAt());
        }
    }
}