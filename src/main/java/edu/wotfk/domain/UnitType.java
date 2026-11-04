package edu.wotfk.domain;

import java.util.Locale;
public enum UnitType {
    MILITIA, GUARD;
    public static UnitType parse(String value) {
        try { return valueOf(value.toUpperCase(Locale.ROOT)); }
        catch (RuntimeException error) { throw new IllegalArgumentException("Unit type must be militia or guard."); }
    }
}
