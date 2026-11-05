package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import edu.wotfk.economy.*;
/** Coordinates phases; calculations and mutations stay in their dedicated services. */
public final class TurnService {
    private final EconomyService economy = new EconomyService();
    public String endRound(CampaignState state) {
        if (state.finished()) return "Campaign already finished.";
        new edu.wotfk.combat.SiegeService().advance(state);
        EconomyLedger playerLedger = economy.forecast(state, state.player());
        for (HouseState house : state.houses()) if (state.alive(house.id())) economy.settle(state, house.id());
        state.log("The council closes round " + state.round() + ".");
        if (state.round() >= state.maxRounds()) state.finish(null, "The Great Council ends the twelve-round session.");
        else { state.nextRound(); for (HouseState house : state.houses()) house.resetActions(); }
        return playerLedger.summary();
    }
}
