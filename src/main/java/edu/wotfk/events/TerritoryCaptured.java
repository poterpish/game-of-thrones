package edu.wotfk.events;

import edu.wotfk.domain.HouseId;
public record TerritoryCaptured(HouseId actor, HouseId previousOwner, String territory, int round) implements WorldEvent {
    @Override public String type() { return "capture"; }
    @Override public String message() { return actor.displayName() + " captured " + territory + " from " + (previousOwner == null ? "the regency" : previousOwner.displayName()) + "."; }
}
