package edu.wotfk.campaign;

import edu.wotfk.data.MapSource;
import edu.wotfk.domain.*;
import edu.wotfk.houses.HouseFactory;
import java.util.*;
/** Builder: construction steps are explicit; build validates the assembled aggregate. */
public final class CampaignBuilder {
    private MapSource mapSource;
    private HouseId player;
    private Long seed;
    private int maxRounds = 12;
    private Map<HouseId, HouseFactory> factories;
    public CampaignBuilder withMap(MapSource mapSource) { this.mapSource = Objects.requireNonNull(mapSource); return this; }
    public CampaignBuilder withPlayer(HouseId player) { this.player = Objects.requireNonNull(player); return this; }
    public CampaignBuilder withSeed(long seed) { this.seed = seed; return this; }
    public CampaignBuilder withRounds(int rounds) { if (rounds < 2 || rounds > 12) throw new IllegalArgumentException("Rounds must be 2..12."); maxRounds = rounds; return this; }
    public CampaignBuilder withFactories(Map<HouseId, HouseFactory> factories) { this.factories = new EnumMap<>(factories); return this; }
    public CampaignState build() {
        if (mapSource == null || player == null || seed == null || factories == null) throw new IllegalStateException("Builder requires map, player, seed and house factories.");
        WorldMap map = mapSource.load(); EnumMap<HouseId, HouseState> houses = new EnumMap<>(HouseId.class);
        for (HouseId id : HouseId.values()) {
            HouseFactory factory = factories.get(id);
            if (factory == null || factory.house() != id) throw new IllegalStateException("Missing or mismatched family for " + id);
            if (map.ownedBy(id).isEmpty() || map.territory(factory.home()).owner() != id) throw new IllegalStateException("Invalid starting home for " + id);
            HouseState house = new HouseState(factory);
            house.army().add(house.newUnit(UnitType.MILITIA)); house.army().add(house.newUnit(UnitType.MILITIA)); house.army().add(house.newUnit(UnitType.GUARD));
            houses.put(id, house);
        }
        CampaignState result = new CampaignState(map, houses, player, seed, maxRounds);
        result.log("The regency loses control. Five houses raise their banners.");
        return result;
    }
}
