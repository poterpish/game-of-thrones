package edu.wotfk.units;

import edu.wotfk.domain.Unit;
public final class CommanderSupportedUnit extends UnitDecorator {
    public CommanderSupportedUnit(Unit unit) { super(unit); }
    @Override public String name() { return super.name() + " [command]"; }
    @Override public int attack() { return super.attack() + 2; }
    @Override public int defense() { return super.defense() + 1; }
}
