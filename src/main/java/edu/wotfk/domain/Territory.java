package edu.wotfk.domain;

import java.util.Objects;
/** Owner is the single source of truth for political control; null means neutral. */
public final class Territory implements edu.wotfk.economy.WorldElement {
    @Override public void accept(edu.wotfk.economy.WorldVisitor visitor) { visitor.visit(this); }
    private final String id;
    private final String name;
    private HouseId owner;
    private int garrison;
    private int fort;
    private final int goldYield;
    private final int foodYield;
    private int damagedUntil;
    public Territory(String id, String name, HouseId owner, int garrison, int fort, int goldYield, int foodYield) {
        if (id == null || !id.matches("[A-Z]{2}") || name == null || name.isBlank()) throw new IllegalArgumentException("Invalid territory identity.");
        if (garrison < 0 || garrison > 8 || fort < 0 || fort > 3 || goldYield < 0 || foodYield < 0) throw new IllegalArgumentException("Invalid territory values.");
        this.id = id; this.name = name; this.owner = owner; this.garrison = garrison; this.fort = fort;
        this.goldYield = goldYield; this.foodYield = foodYield;
    }
    public String id() { return id; }
    public String name() { return name; }
    public HouseId owner() { return owner; }
    public int garrison() { return garrison; }
    public int fort() { return fort; }
    public int goldYield() { return goldYield; }
    public int foodYield() { return foodYield; }
    public boolean productive(int round) { return round > damagedUntil; }
    public void damageUntil(int round) { damagedUntil = Math.max(damagedUntil, round); }
    public void reinforce() {
        if (garrison >= 8) throw new IllegalStateException("Garrison already at capacity.");
        garrison++;
    }
    public void improveFort() {
        if (fort >= 3) throw new IllegalStateException("Fortifications already at capacity.");
        fort++;
    }
    public void capture(HouseId conqueror) { owner = Objects.requireNonNull(conqueror); garrison = 1; }
    public void reduceGarrison(int loss) { if (loss < 0) throw new IllegalArgumentException("Negative loss."); garrison = Math.max(0, garrison - loss); }
}
