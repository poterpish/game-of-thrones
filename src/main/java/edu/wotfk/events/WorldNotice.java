package edu.wotfk.events;

import edu.wotfk.domain.HouseId;
public record WorldNotice(String type, int round, HouseId actor, String message) implements WorldEvent { }
