package com.example.geonotesteaching.export;

import com.example.geonotesteaching.model.Attachment;
import com.example.geonotesteaching.model.Link;
import com.example.geonotesteaching.model.Note;
import com.example.geonotesteaching.service.Describe;

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
            Attachment a = note.attachment();
            if (a != null) {
                builder.append("    🔗 ").append(Describe.describeAttachment(a)).append("\n");
            }
        }
        return builder.toString();
    }
}
