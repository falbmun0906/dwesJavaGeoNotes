package com.example.geonotesteaching.export;

import com.example.geonotesteaching.service.Timeline;

public abstract sealed class AbstractExporter implements Exporter permits JsonExporter, Timeline.Render {
    public abstract String export();
}