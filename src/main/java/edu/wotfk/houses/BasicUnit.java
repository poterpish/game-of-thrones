package edu.wotfk.houses;

import edu.wotfk.domain.*;
import java.util.Objects;
/** Immutable common implementation for the five concrete military products. */
public class BasicUnit implements Unit {
    private final String id;
    private final HouseId house;
    private final UnitType type;
    private final int attack;
    private final int defense;
    private final int goldUpkeep;
    private final int coastalBonus;
    protected BasicUnit(String id, HouseId house, UnitType type, int attack, int defense, int goldUpkeep, int coastalBonus) {
        this.id = Objects.requireNonNull(id); this.house = Objects.requireNonNull(house); this.type = Objects.requireNonNull(type);
        if (attack < 1 || defense < 1 || goldUpkeep < 0 || coastalBonus < 0) throw new IllegalArgumentException("Invalid unit profile.");
        this.attack = attack; this.defense = defense; this.goldUpkeep = goldUpkeep; this.coastalBonus = coastalBonus;
    }
    @Override public String id() { return id; }
    @Override public HouseId house() { return house; }
    @Override public UnitType type() { return type; }
    @Override public String name() { return house.displayName() + " " + type.name().toLowerCase(java.util.Locale.ROOT); }
    @Override public int attack() { return attack; }
    @Override public int defense() { return defense; }
    @Override public int goldUpkeep() { return goldUpkeep; }
    @Override public int foodUpkeep() { return type == UnitType.GUARD ? 2 : 1; }
    @Override public int coastalBonus() { return coastalBonus; }
}
