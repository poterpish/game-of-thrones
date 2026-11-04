package edu.wotfk.houses;

import edu.wotfk.domain.HouseId;
import java.util.*;
/** Registry selects a family; this is not the Factory Method pattern used for story events. */
public final class HouseFactories {
    private HouseFactories() { }
    public static Map<HouseId, HouseFactory> all() {
        EnumMap<HouseId, HouseFactory> result = new EnumMap<>(HouseId.class);
        for (HouseFactory factory : List.of(new StarkHouseFactory(), new LannisterHouseFactory(), new TargaryenHouseFactory(), new BaratheonHouseFactory(), new GreyjoyHouseFactory()))
            result.put(factory.house(), factory);
        return Collections.unmodifiableMap(result);
    }
}
