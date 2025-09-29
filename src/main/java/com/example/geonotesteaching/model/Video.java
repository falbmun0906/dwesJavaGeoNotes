package com.example.geonotesteaching.model;

public record Video(String url, int width, int height, int seconds) implements Attachment {
    public Video {
        if (url == null || url.isBlank()) throw new IllegalArgumentException("URL requerida");
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Dimensiones inválidas");
        if (seconds < 0) throw new IllegalArgumentException("Duración inválida");
    }
}
