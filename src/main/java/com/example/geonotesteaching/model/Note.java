package com.example.geonotesteaching.model;

import java.time.Instant;

// Un 'record' para la nota, que incluye un campo de tipo 'sealed interface'.
// El 'id' se genera automáticamente usando el hash code y el timestamp, aunque
// en un entorno real usaríamos un UUID.
public record Note(long id, String title, String content, GeoPoint location, Instant createdAt, Attachment attachment) {
    public Note {
        if (title == null || title.isBlank() || title.trim().length() < 3) throw new IllegalArgumentException("title requerido con, al menos, 3 caracteres");
        if(content != null) content = content.trim();
        if (content == null || content.trim().isEmpty()) content = "-";
        // Otra opción sería utilizar un operador ternario de la siguiente forma:
        // 
        // content = (content == null || content.trim().isEmpty()) ? "-" : content.trim();
        if (location == null) throw new IllegalArgumentException("location requerido");
        if (createdAt == null) createdAt = Instant.now();
    }
}