package edu.wotfk.domain;

import java.util.*;
/** Immutable topology with mutable territory state. Connection order is deterministic. */
public final class WorldMap {
    public record Route(String destination, boolean sea) { }
    private final Map<String, Territory> territories;
    private final Map<String, List<Route>> routes;
    public WorldMap(Map<String, Territory> territories, Map<String, List<Route>> routes) {
        if (territories.isEmpty()) throw new IllegalArgumentException("Map is empty.");
        this.territories = Collections.unmodifiableMap(new LinkedHashMap<>(territories));
        Map<String, List<Route>> copy = new LinkedHashMap<>();
        for (String id : territories.keySet()) {
            if (!routes.containsKey(id)) throw new IllegalArgumentException("Missing routes for " + id);
            copy.put(id, List.copyOf(routes.get(id)));
        }
        this.routes = Collections.unmodifiableMap(copy);
        validate();
    }
    private void validate() {
        for (var entry : routes.entrySet()) {
            Set<String> unique = new HashSet<>();
            for (Route route : entry.getValue()) {
                if (!territories.containsKey(route.destination()) || route.destination().equals(entry.getKey()) || !unique.add(route.destination()))
                    throw new IllegalArgumentException("Invalid or duplicate route at " + entry.getKey());
                if (!routes.get(route.destination()).contains(new Route(entry.getKey(), route.sea())))
                    throw new IllegalArgumentException("Routes must be reciprocal: " + entry.getKey() + " -> " + route.destination());
            }
        }
        Set<String> seen = new HashSet<>(); ArrayDeque<String> queue = new ArrayDeque<>();
        queue.add(territories.keySet().iterator().next());
        while (!queue.isEmpty()) { String id = queue.remove(); if (seen.add(id)) for (Route route : routes.get(id)) queue.add(route.destination()); }
        if (seen.size() != territories.size()) throw new IllegalArgumentException("Map contains unreachable territories.");
    }
    public Territory territory(String id) {
        Territory found = territories.get(id.toUpperCase(Locale.ROOT));
        if (found == null) throw new IllegalArgumentException("Unknown territory: " + id);
        return found;
    }
    public Collection<Territory> territories() { return territories.values(); }
    public List<Route> routes(String id) { territory(id); return routes.get(id.toUpperCase(Locale.ROOT)); }
    public Optional<Route> route(String from, String to) { return routes(from).stream().filter(r -> r.destination().equalsIgnoreCase(to)).findFirst(); }
    public boolean coastal(String id) { return routes(id).stream().anyMatch(Route::sea); }
    public List<Territory> ownedBy(HouseId house) { return territories.values().stream().filter(t -> t.owner() == house).toList(); }
    public String atlas() {
        StringBuilder out = new StringBuilder(String.format("%-3s %-18s %-11s %s%n", "ID", "TERRITORY", "OWNER", "ROUTES (~ sea)"));
        for (Territory t : territories.values()) {
            String links = String.join(" ", routes(t.id()).stream().map(r -> (r.sea() ? "~" : "") + r.destination()).toList());
            out.append(String.format("%-3s %-18s %-11s %s%n", t.id(), t.name(), t.owner() == null ? "Neutral" : t.owner().displayName(), links));
        }
        return out.toString();
    }
}
