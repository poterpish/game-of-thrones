package edu.wotfk.domain;

import java.util.Locale;
public enum HouseId {
    STARK, LANNISTER, TARGARYEN, BARATHEON, GREYJOY;
    public static HouseId parse(String text) {
        try { return valueOf(text.strip().toUpperCase(Locale.ROOT)); }
        catch (RuntimeException invalid) { throw new IllegalArgumentException("Choose Stark, Lannister, Targaryen, Baratheon or Greyjoy."); }
    }
    public String displayName() { return name().charAt(0) + name().substring(1).toLowerCase(Locale.ROOT); }
}
