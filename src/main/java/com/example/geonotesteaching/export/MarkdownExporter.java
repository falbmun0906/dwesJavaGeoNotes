package com.example.geonotesteaching.export;

import com.example.geonotesteaching.model.Note;

import java.util.List;

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
