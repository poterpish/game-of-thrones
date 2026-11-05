package edu.wotfk.units;

import edu.wotfk.domain.*;
/** Component-compatible wrapper; concrete decorators change only their own concern. */
public abstract class UnitDecorator implements Unit {
    protected final Unit wrapped;
    protected UnitDecorator(Unit wrapped) { this.wrapped = java.util.Objects.requireNonNull(wrapped); }
    @Override public String id() { return wrapped.id(); }
    @Override public String name() { return wrapped.name(); }
    @Override public HouseId house() { return wrapped.house(); }
    @Override public UnitType type() { return wrapped.type(); }
    @Override public int attack() { return wrapped.attack(); }
    @Override public int defense() { return wrapped.defense(); }
    @Override public int goldUpkeep() { return wrapped.goldUpkeep(); }
    @Override public int foodUpkeep() { return wrapped.foodUpkeep(); }
    @Override public int coastalBonus() { return wrapped.coastalBonus(); }
}
