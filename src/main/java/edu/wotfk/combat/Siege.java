package edu.wotfk.combat;

import edu.wotfk.domain.HouseId;
public final class Siege {
    private final HouseId attacker;
    private final String origin;
    private final String target;
    private int progress;
    public Siege(HouseId attacker, String origin, String target) { this.attacker = attacker; this.origin = origin; this.target = target; }
    public HouseId attacker() { return attacker; }
    public String origin() { return origin; }
    public String target() { return target; }
    public int progress() { return progress; }
    public void advance() { progress = Math.min(2, progress + 1); }
}
