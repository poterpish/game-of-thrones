package edu.wotfk.diplomacy;
import edu.wotfk.domain.HouseId;
import java.util.*;
/** Stores symmetric war and alliance relationships. */
public final class DiplomacyState {
    private final Set<Set<HouseId>> wars = new HashSet<>();
    private final Set<Set<HouseId>> alliances = new HashSet<>();
    private static Set<HouseId> pair(HouseId a, HouseId b) {
        if (a == null || b == null || a == b) return Set.of();
        return Set.of(a, b);
    }
    public boolean atWar(HouseId a, HouseId b) { return wars.contains(pair(a,b)) && a != b; }
    public boolean allied(HouseId a, HouseId b) { return a != null && (a == b || alliances.contains(pair(a,b))); }
    public void declareWar(HouseId a, HouseId b) {
        if (a == null || b == null || a == b) throw new IllegalArgumentException("Choose another house.");
        alliances.remove(pair(a,b)); wars.add(pair(a,b));
    }
}
