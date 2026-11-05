package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import java.util.*;
/** Mutable session aggregate, owned by services. Views must not expose hidden balances. */
public final class CampaignState {
    private final edu.wotfk.diplomacy.DiplomacyState diplomacy = new edu.wotfk.diplomacy.DiplomacyState();
    public edu.wotfk.diplomacy.DiplomacyState diplomacy() { return diplomacy; }
    private final edu.wotfk.events.WorldEventBus events = new edu.wotfk.events.WorldEventBus();
    private final edu.wotfk.events.NotificationObserver notifications = new edu.wotfk.events.NotificationObserver();
    private final edu.wotfk.events.EventTriggerObserver triggers = new edu.wotfk.events.EventTriggerObserver();
    private final java.util.EnumMap<HouseId, edu.wotfk.combat.Siege> sieges = new java.util.EnumMap<>(HouseId.class);
    public edu.wotfk.events.WorldEventBus events() { return events; }
    public edu.wotfk.events.NotificationObserver notifications() { return notifications; }
    public edu.wotfk.events.EventTriggerObserver triggers() { return triggers; }
    public java.util.Map<HouseId, edu.wotfk.combat.Siege> sieges() { return sieges; }
    private final WorldMap map;
    private final EnumMap<HouseId, HouseState> houses;
    private final HouseId player;
    private final long seed;
    private final Random random;
    private final int maxRounds;
    private int round = 1;
    private String ending;
    private HouseId winner;
    private final List<String> journal = new ArrayList<>();
    public CampaignState(WorldMap map, Map<HouseId, HouseState> houses, HouseId player, long seed, int maxRounds) {
        this.map = Objects.requireNonNull(map); this.houses = new EnumMap<>(HouseId.class); this.houses.putAll(houses);
        this.player = Objects.requireNonNull(player); this.seed = seed; this.random = new Random(seed); this.maxRounds = maxRounds;
        events.subscribe(new edu.wotfk.events.ChronicleObserver(this::log));
        events.subscribe(notifications); events.subscribe(triggers);
    }
    public WorldMap map() { return map; }
    public Collection<HouseState> houses() { return Collections.unmodifiableCollection(houses.values()); }
    public HouseState house(HouseId id) { return Objects.requireNonNull(houses.get(id), "Unknown house"); }
    public HouseId player() { return player; }
    public long seed() { return seed; }
    public Random random() { return random; }
    public int round() { return round; }
    public int maxRounds() { return maxRounds; }
    public boolean alive(HouseId id) { return !map.ownedBy(id).isEmpty(); }
    public void nextRound() { if (!finished()) round++; }
    public boolean finished() { return ending != null; }
    public String ending() { return ending; }
    public HouseId winner() { return winner; }
    public void finish(HouseId winner, String ending) { if (!finished()) { this.winner = winner; this.ending = Objects.requireNonNull(ending); log(ending); } }
    public void log(String message) { journal.add("Round " + round + "  " + message); }
    public List<String> journal() { return List.copyOf(journal); }
}
