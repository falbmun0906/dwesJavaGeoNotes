package com.example.geonotesteaching.export;

import com.example.geonotesteaching.model.Note;

import java.util.List;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

// La clase 'Timeline' usa un 'SequencedMap' para mantener las notas en orden de inserción.
// A diferencia de un HashMap, un 'SequencedMap' garantiza el orden y permite acceder
// al primer y último elemento de forma eficiente.
public final class Timeline {
    private final Map<Long, Note> notes = new LinkedHashMap<>();

    public void addNote(Note note) { notes.put(note.id(), note); }
    public Note getNote(long id) { return notes.get(id); }
    public Map<Long, Note> getNotes() { return notes; }

    /**
     * Devuelve las n notas más recientes ordenadas por fecha de creación (descendente).
     */
    public List<Note> latest(int n) {
        return notes.values().stream()
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt())) // descendente
                .limit(n)
                .toList();
    }

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