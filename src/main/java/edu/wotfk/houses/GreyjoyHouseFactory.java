package edu.wotfk.houses;

import edu.wotfk.domain.*;
/** Concrete family: every product retains Greyjoy's identity and mechanical bonuses. */
public final class GreyjoyHouseFactory implements HouseFactory {
    @Override public HouseId house() { return HouseId.GREYJOY; }
    @Override public Unit createUnit(UnitType type, String id) { return new GreyjoyUnit(id, type); }
    @Override public Diplomat createDiplomat() { return new GreyjoyDiplomat(); }
    @Override public Scout createScout() { return new GreyjoyScout(); }
    @Override public Cost recruitmentCost(UnitType type) { return new Cost(type == UnitType.GUARD ? 6 : 3, 2, type == UnitType.GUARD ? 1 : 0); }
    @Override public Resources startingResources() { return new Resources(18, 14, 5); }
    @Override public int startingReputation() { return 30; }
    @Override public int startingMorale() { return 70; }
    @Override public String home() { return "PK"; }
    @Override public String strengths() { return "Coastal combat, scouting and cheaper sea crossings."; }
    @Override public String weakness() { return "Weak inland defense and poor initial reputation."; }
    public static final class GreyjoyUnit extends BasicUnit {
        public GreyjoyUnit(String id, UnitType type) {
            super(id, HouseId.GREYJOY, type, type == UnitType.GUARD ? 7 : 4,
                  type == UnitType.GUARD ? 6 : 3, type == UnitType.GUARD ? 1 : 0, 3);
        }
    }
    public static final class GreyjoyDiplomat implements Diplomat {
        @Override public HouseId house() { return HouseId.GREYJOY; }
        @Override public String title() { return "Greyjoy envoy"; }
        @Override public int negotiationBonus() { return 2; }
    }
    public static final class GreyjoyScout implements Scout {
        @Override public HouseId house() { return HouseId.GREYJOY; }
        @Override public String title() { return "Greyjoy scout"; }
        @Override public int intelligenceBonus() { return 16; }
    }
}
