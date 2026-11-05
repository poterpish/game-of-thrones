package edu.wotfk.events;

import edu.wotfk.domain.HouseId;
/** Notification of a committed fact, not an interactive story choice. */
public interface WorldEvent {
    String type();
    int round();
    HouseId actor();
    String message();
}
