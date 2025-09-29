package com.example.geonotesteaching.export;

public abstract sealed class AbstractExporter implements Exporter permits JsonExporter, MarkdownExporter, Timeline.Render {
    public abstract String export();
}