package edu.wotfk.intelligence;

import edu.wotfk.domain.HouseId;
import java.util.*;
/** Immutable dated observation. Later world changes must not rewrite earlier intelligence. */
public record IntelReport(HouseId target, int observedRound, int gold, int food, int influence, int troops, int morale, String armyLocation, Map<String,Integer> garrisons) {
    public IntelReport { garrisons = Collections.unmodifiableMap(new LinkedHashMap<>(garrisons)); }
    public String describe(int currentRound) {
        return target.displayName() + " - intelligence from round " + observedRound + " (" + (currentRound-observedRound) + " rounds old)\n"
                + "Observed gold " + gold + ", food " + food + ", influence " + influence + ".\n"
                + "Observed army: " + troops + " detachments at " + armyLocation + ", morale " + morale + ".\n"
                + "Observed garrisons: " + garrisons + "\nThese are historical observations, not live values.";
    }
}
