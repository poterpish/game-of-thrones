package edu.wotfk.houses;

import edu.wotfk.domain.*;
/** Abstract Factory: creates a coherent military, diplomatic and intelligence family. */
public interface HouseFactory {
    HouseId house();
    Unit createUnit(UnitType type, String id);
    Diplomat createDiplomat();
    Scout createScout();
    Cost recruitmentCost(UnitType type);
    Resources startingResources();
    int startingReputation();
    int startingMorale();
    String home();
    String strengths();
    String weakness();
}
