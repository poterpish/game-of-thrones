package edu.wotfk.economy;

import edu.wotfk.domain.*;
import java.util.Objects;
/** Visitor computes one house's budget without changing the world. */
public final class EconomyVisitor implements WorldVisitor {
    private final HouseId owner;
    private final int round;
    private final boolean winter;
    private java.util.Set<String> blockaded = java.util.Set.of();
    public EconomyVisitor excluding(java.util.Set<String> blockaded) { this.blockaded = java.util.Set.copyOf(blockaded); return this; }
    private int goldIncome;
    private int foodIncome;
    private int goldUpkeep;
    private int foodUpkeep;
    public EconomyVisitor(HouseId owner, int round, boolean winter) { this.owner = Objects.requireNonNull(owner); this.round = round; this.winter = winter; }
    @Override public void visit(Territory territory) {
        if (territory.owner() == owner && territory.productive(round) && !blockaded.contains(territory.id())) { goldIncome += territory.goldYield(); foodIncome += territory.foodYield(); }
    }
    @Override public void visit(Army army) {
        if (army.owner() != owner) return;
        for (Unit unit : army.units()) { goldUpkeep += unit.goldUpkeep(); foodUpkeep += unit.foodUpkeep(); }
        if (winter && !army.units().isEmpty()) foodUpkeep++;
    }
    public EconomyLedger ledger() { return new EconomyLedger(goldIncome, foodIncome, goldUpkeep, foodUpkeep); }
}
