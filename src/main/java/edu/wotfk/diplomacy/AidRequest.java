package edu.wotfk.diplomacy;

import edu.wotfk.domain.HouseId;
public record AidRequest(HouseId requester, HouseId recipient, int dueRound) { }
