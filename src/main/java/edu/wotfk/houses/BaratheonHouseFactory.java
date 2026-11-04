package edu.wotfk.houses;

import edu.wotfk.domain.*;
/** Concrete family: every product retains Baratheon's identity and mechanical bonuses. */
public final class BaratheonHouseFactory implements HouseFactory {
    @Override public HouseId house() { return HouseId.BARATHEON; }
    @Override public Unit createUnit(UnitType type, String id) { return new BaratheonUnit(id, type); }
    @Override public Diplomat createDiplomat() { return new BaratheonDiplomat(); }
    @Override public Scout createScout() { return new BaratheonScout(); }
    @Override public Cost recruitmentCost(UnitType type) { return new Cost(type == UnitType.GUARD ? 6 : 3, 2, type == UnitType.GUARD ? 1 : 0); }
    @Override public Resources startingResources() { return new Resources(14, 18, 10); }
    @Override public int startingReputation() { return 65; }
    @Override public int startingMorale() { return 80; }
    @Override public String home() { return "SE"; }
    @Override public String strengths() { return "High morale, legitimacy and skilled envoys."; }
    @Override public String weakness() { return "A modest treasury limits rapid recruitment."; }
    public static final class BaratheonUnit extends BasicUnit {
        public BaratheonUnit(String id, UnitType type) {
            super(id, HouseId.BARATHEON, type, type == UnitType.GUARD ? 8 : 4,
                  type == UnitType.GUARD ? 8 : 5, type == UnitType.GUARD ? 1 : 0, 0);
        }
    }
    public static final class BaratheonDiplomat implements Diplomat {
        @Override public HouseId house() { return HouseId.BARATHEON; }
        @Override public String title() { return "Baratheon envoy"; }
        @Override public int negotiationBonus() { return 12; }
    }
    public static final class BaratheonScout implements Scout {
        @Override public HouseId house() { return HouseId.BARATHEON; }
        @Override public String title() { return "Baratheon scout"; }
        @Override public int intelligenceBonus() { return 4; }
    }
}
