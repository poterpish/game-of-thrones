package edu.wotfk.diplomacy;

import edu.wotfk.domain.HouseId;
import java.util.*;
/** Each unordered pair has one treaty and one symmetric trust score. */
public final class DiplomacyState {
    private final java.util.EnumMap<HouseId, HouseId> endorsements = new java.util.EnumMap<>(HouseId.class);
    private final java.util.EnumMap<HouseId, AidRequest> aidRequests = new java.util.EnumMap<>(HouseId.class);
    private final java.util.EnumMap<HouseId, Integer> aidCooldown = new java.util.EnumMap<>(HouseId.class);
    public Map<HouseId, HouseId> endorsements() { return endorsements; }
    public Map<HouseId, AidRequest> aidRequests() { return aidRequests; }
    public Map<HouseId, Integer> aidCooldown() { return aidCooldown; }
    public int supporters(edu.wotfk.campaign.CampaignState state, HouseId claimant) {
        return (int) endorsements.entrySet().stream().filter(e -> e.getValue() == claimant && state.alive(e.getKey()) && allied(e.getKey(), claimant)).count();
    }
    public void revokeBetween(HouseId a, HouseId b) {
        if (endorsements.get(a) == b) endorsements.remove(a);
        if (endorsements.get(b) == a) endorsements.remove(b);
    }
    private final Map<String, Relation> pairs = new LinkedHashMap<>();
    public DiplomacyState() {
        for (HouseId a : HouseId.values()) for (HouseId b : HouseId.values()) if (a.ordinal() < b.ordinal()) {
            int trust = (a == HouseId.GREYJOY || b == HouseId.GREYJOY) ? -10 : 10;
            if ((a == HouseId.STARK && b == HouseId.LANNISTER) || (a == HouseId.TARGARYEN && b == HouseId.BARATHEON)) trust = -25;
            pairs.put(key(a, b), new Relation(trust));
        }
    }
    private String key(HouseId a, HouseId b) {
        if (a == null || b == null || a == b) throw new IllegalArgumentException("Choose another living house.");
        return a.ordinal() < b.ordinal() ? a.name() + ":" + b.name() : b.name() + ":" + a.name();
    }
    public Relation between(HouseId a, HouseId b) { return pairs.get(key(a, b)); }
    public boolean allied(HouseId a, HouseId b) { return a != null && b != null && a != b && between(a,b).status() == Relation.Status.ALLIED; }
    public boolean atWar(HouseId a, HouseId b) { return a != null && b != null && a != b && between(a,b).status() == Relation.Status.WAR; }
    public void changeOthersTrust(HouseId actor, int delta) { for (HouseId other : HouseId.values()) if (other != actor) between(actor, other).changeTrust(delta); }
}
