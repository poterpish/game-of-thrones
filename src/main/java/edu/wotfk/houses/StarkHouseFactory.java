package edu.wotfk.houses;

import edu.wotfk.domain.*;
/** Concrete family: every product retains Stark's identity and mechanical bonuses. */
public final class StarkHouseFactory implements HouseFactory {
    @Override public HouseId house() { return HouseId.STARK; }
    @Override public Unit createUnit(UnitType type, String id) { return new StarkUnit(id, type); }
    @Override public Diplomat createDiplomat() { return new StarkDiplomat(); }
    @Override public Scout createScout() { return new StarkScout(); }
    @Override public Cost recruitmentCost(UnitType type) { return new Cost(type == UnitType.GUARD ? 6 : 3, 2, type == UnitType.GUARD ? 1 : 0); }
    @Override public Resources startingResources() { return new Resources(12, 24, 6); }
    @Override public int startingReputation() { return 70; }
    @Override public int startingMorale() { return 75; }
    @Override public String home() { return "WF"; }
    @Override public String strengths() { return "Strong defense, winter stores and trusted diplomacy."; }
    @Override public String weakness() { return "Limited starting gold and a long road to the capital."; }
    public static final class StarkUnit extends BasicUnit {
        public StarkUnit(String id, UnitType type) {
            super(id, HouseId.STARK, type, type == UnitType.GUARD ? 7 : 4,
                  type == UnitType.GUARD ? 9 : 6, type == UnitType.GUARD ? 1 : 0, 0);
        }
    }
    public static final class StarkDiplomat implements Diplomat {
        @Override public HouseId house() { return HouseId.STARK; }
        @Override public String title() { return "Stark envoy"; }
        @Override public int negotiationBonus() { return 6; }
    }
    public static final class StarkScout implements Scout {
        @Override public HouseId house() { return HouseId.STARK; }
        @Override public String title() { return "Stark scout"; }
        @Override public int intelligenceBonus() { return 10; }
    }
}
