package edu.wotfk.houses;

import edu.wotfk.domain.*;
/** Concrete family: every product retains Targaryen's identity and mechanical bonuses. */
public final class TargaryenHouseFactory implements HouseFactory {
    @Override public HouseId house() { return HouseId.TARGARYEN; }
    @Override public Unit createUnit(UnitType type, String id) { return new TargaryenUnit(id, type); }
    @Override public Diplomat createDiplomat() { return new TargaryenDiplomat(); }
    @Override public Scout createScout() { return new TargaryenScout(); }
    @Override public Cost recruitmentCost(UnitType type) { return new Cost(type == UnitType.GUARD ? 6 : 3, 2, type == UnitType.GUARD ? 1 : 0); }
    @Override public Resources startingResources() { return new Resources(16, 16, 9); }
    @Override public int startingReputation() { return 45; }
    @Override public int startingMorale() { return 75; }
    @Override public String home() { return "DS"; }
    @Override public String strengths() { return "Elite guards and an influential restoration claim."; }
    @Override public String weakness() { return "Elite upkeep and sea travel strain the treasury."; }
    public static final class TargaryenUnit extends BasicUnit {
        public TargaryenUnit(String id, UnitType type) {
            super(id, HouseId.TARGARYEN, type, type == UnitType.GUARD ? 10 : 5,
                  type == UnitType.GUARD ? 8 : 4, type == UnitType.GUARD ? 2 : 0, 0);
        }
    }
    public static final class TargaryenDiplomat implements Diplomat {
        @Override public HouseId house() { return HouseId.TARGARYEN; }
        @Override public String title() { return "Targaryen envoy"; }
        @Override public int negotiationBonus() { return 7; }
    }
    public static final class TargaryenScout implements Scout {
        @Override public HouseId house() { return HouseId.TARGARYEN; }
        @Override public String title() { return "Targaryen scout"; }
        @Override public int intelligenceBonus() { return 7; }
    }
}
