package edu.wotfk.domain;

import edu.wotfk.houses.*;
import java.util.Objects;
public final class HouseState {
    private final HouseFactory factory;
    private final Resources resources;
    private final Army army;
    private final Diplomat diplomat;
    private final Scout scout;
    private int reputation;
    private int actions = 3;
    private int serial;
    public HouseState(HouseFactory factory) {
        this.factory = Objects.requireNonNull(factory);
        resources = factory.startingResources(); reputation = factory.startingReputation();
        diplomat = factory.createDiplomat(); scout = factory.createScout();
        army = new Army(factory.house(), factory.home(), factory.startingMorale());
    }
    public HouseId id() { return factory.house(); }
    public HouseFactory factory() { return factory; }
    public Resources resources() { return resources; }
    public Army army() { return army; }
    public Diplomat diplomat() { return diplomat; }
    public Scout scout() { return scout; }
    public int reputation() { return reputation; }
    public int actions() { return actions; }
    public void changeReputation(int delta) { reputation = Math.max(0, Math.min(100, reputation + delta)); }
    public void spendActions(int count) {
        if (count < 0 || count > actions) throw new IllegalStateException("Not enough action points.");
        actions -= count;
    }
    public void resetActions() { actions = 3; }
    public Unit newUnit(UnitType type) { return factory.createUnit(type, id().name() + "-" + (++serial)); }
}
