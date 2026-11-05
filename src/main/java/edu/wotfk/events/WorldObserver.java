package edu.wotfk.events;

@FunctionalInterface
public interface WorldObserver { void onEvent(WorldEvent event); }
