package edu.wotfk.data;

import edu.wotfk.domain.*;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;
/** Adapter: converts the legacy row API into the MapSource object graph. */
public final class CsvMapAdapter implements MapSource {
    private final LegacyMapReader reader;
    private final Supplier<InputStream> input;
    public CsvMapAdapter() { this(new LegacyMapReader(), () -> CsvMapAdapter.class.getResourceAsStream("/data/westeros.map")); }
    public CsvMapAdapter(LegacyMapReader reader, Supplier<InputStream> input) {
        this.reader = Objects.requireNonNull(reader); this.input = Objects.requireNonNull(input);
    }
    @Override public WorldMap load() {
        Map<String, Territory> territories = new LinkedHashMap<>();
        Map<String, List<WorldMap.Route>> routes = new LinkedHashMap<>();
        for (String[] row : reader.readRows(input.get())) {
            HouseId owner = switch (row[2]) {
                case "S" -> HouseId.STARK; case "L" -> HouseId.LANNISTER; case "T" -> HouseId.TARGARYEN;
                case "B" -> HouseId.BARATHEON; case "G" -> HouseId.GREYJOY; case "N" -> null;
                default -> throw new IllegalArgumentException("Unknown legacy owner code: " + row[2]);
            };
            Territory t;
            try { t = new Territory(row[0], row[1], owner, Integer.parseInt(row[3]), Integer.parseInt(row[4]), Integer.parseInt(row[5]), Integer.parseInt(row[6])); }
            catch (NumberFormatException bad) { throw new IllegalArgumentException("Invalid numbers for " + row[0], bad); }
            if (territories.putIfAbsent(t.id(), t) != null) throw new IllegalArgumentException("Duplicate territory: " + t.id());
            List<WorldMap.Route> links = new ArrayList<>();
            if (!row[7].isBlank()) for (String token : row[7].split(",")) {
                String link = token.strip(); boolean sea = link.startsWith("~");
                links.add(new WorldMap.Route(sea ? link.substring(1) : link, sea));
            }
            routes.put(t.id(), links);
        }
        return new WorldMap(territories, routes);
    }
}
