package edu.wotfk.houses;

import edu.wotfk.domain.*;
/** Concrete family: every product retains Lannister's identity and mechanical bonuses. */
public final class LannisterHouseFactory implements HouseFactory {
    @Override public HouseId house() { return HouseId.LANNISTER; }
    @Override public Unit createUnit(UnitType type, String id) { return new LannisterUnit(id, type); }
    @Override public Diplomat createDiplomat() { return new LannisterDiplomat(); }
    @Override public Scout createScout() { return new LannisterScout(); }
    @Override public Cost recruitmentCost(UnitType type) { return new Cost(type == UnitType.GUARD ? 7 : 3, 2, type == UnitType.GUARD ? 1 : 0); }
    @Override public Resources startingResources() { return new Resources(28, 12, 6); }
    @Override public int startingReputation() { return 35; }
    @Override public int startingMorale() { return 65; }
    @Override public String home() { return "CR"; }
    @Override public String strengths() { return "Wealth and strong professional guards."; }
    @Override public String weakness() { return "Low trust and weak food production."; }
    public static final class LannisterUnit extends BasicUnit {
        public LannisterUnit(String id, UnitType type) {
            super(id, HouseId.LANNISTER, type, type == UnitType.GUARD ? 8 : 5,
                  type == UnitType.GUARD ? 7 : 4, type == UnitType.GUARD ? 1 : 0, 0);
        }
    }
    public static final class LannisterDiplomat implements Diplomat {
        @Override public HouseId house() { return HouseId.LANNISTER; }
        @Override public String title() { return "Lannister envoy"; }
        @Override public int negotiationBonus() { return 8; }
    }
    public static final class LannisterScout implements Scout {
        @Override public HouseId house() { return HouseId.LANNISTER; }
        @Override public String title() { return "Lannister scout"; }
        @Override public int intelligenceBonus() { return 5; }
    }
}
