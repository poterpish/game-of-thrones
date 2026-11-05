package edu.wotfk.events;

import java.util.function.Consumer;
public final class ChronicleObserver implements WorldObserver {
    private final Consumer<String> append;
    public ChronicleObserver(Consumer<String> append) { this.append = java.util.Objects.requireNonNull(append); }
    @Override public void onEvent(WorldEvent event) { append.accept(event.message()); }
}
