package edu.wotfk.events;

import java.util.*;
public final class NotificationObserver implements WorldObserver {
    private final List<String> pending = new ArrayList<>();
    @Override public void onEvent(WorldEvent event) { pending.add(event.message()); }
    public List<String> drain() { List<String> result = List.copyOf(pending); pending.clear(); return result; }
}
