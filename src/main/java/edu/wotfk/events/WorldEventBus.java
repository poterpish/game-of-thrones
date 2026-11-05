package edu.wotfk.events;

import java.util.*;
/** Observer publisher. Notifications cannot roll back already-committed game rules. */
public final class WorldEventBus {
    private final List<WorldObserver> observers = new ArrayList<>();
    private final List<String> failures = new ArrayList<>();
    public void subscribe(WorldObserver observer) { if (!observers.contains(observer)) observers.add(Objects.requireNonNull(observer)); }
    public void unsubscribe(WorldObserver observer) { observers.remove(observer); }
    public void publish(WorldEvent event) {
        for (WorldObserver observer : List.copyOf(observers)) {
            try { observer.onEvent(event); }
            catch (RuntimeException failure) { failures.add(observer.getClass().getSimpleName() + ": " + failure.getMessage()); }
        }
    }
    public List<String> failures() { return List.copyOf(failures); }
}
