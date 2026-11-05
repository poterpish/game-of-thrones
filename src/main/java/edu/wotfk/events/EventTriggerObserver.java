package edu.wotfk.events;

import java.util.*;
/** Remembers narrative milestones; authoritative ownership stays in Territory. */
public final class EventTriggerObserver implements WorldObserver {
    private final Set<String> milestones = new HashSet<>();
    @Override public void onEvent(WorldEvent event) { milestones.add(event.type() + ":" + event.actor()); }
    public boolean happened(String type, edu.wotfk.domain.HouseId actor) { return milestones.contains(type + ":" + actor); }
}
