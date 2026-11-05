package edu.wotfk.units;

import edu.wotfk.domain.Unit;
public final class EquippedUnit extends UnitDecorator {
    public EquippedUnit(Unit unit) { super(unit); }
    @Override public String name() { return super.name() + " [equipped]"; }
    @Override public int attack() { return super.attack() + 2; }
    @Override public int defense() { return super.defense() + 2; }
    @Override public int goldUpkeep() { return super.goldUpkeep() + 1; }
}
