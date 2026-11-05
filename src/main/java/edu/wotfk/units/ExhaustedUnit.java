package edu.wotfk.units;

import edu.wotfk.domain.Unit;
public final class ExhaustedUnit extends UnitDecorator {
    public ExhaustedUnit(Unit unit) { super(unit); }
    @Override public String name() { return super.name() + " [exhausted]"; }
    @Override public int attack() { return Math.max(1, super.attack() * 3 / 4); }
    @Override public int defense() { return Math.max(1, super.defense() * 3 / 4); }
}
