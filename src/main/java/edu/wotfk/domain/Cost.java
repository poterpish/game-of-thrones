package edu.wotfk.domain;

public record Cost(int gold, int food, int influence) {
    public static final Cost NONE = new Cost(0, 0, 0);
    public Cost {
        if (gold < 0 || food < 0 || influence < 0) throw new IllegalArgumentException("Costs cannot be negative.");
    }
}
