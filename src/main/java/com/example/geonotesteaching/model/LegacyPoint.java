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
