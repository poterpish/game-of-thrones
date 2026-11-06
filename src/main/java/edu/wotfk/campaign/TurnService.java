package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import edu.wotfk.economy.*;
import edu.wotfk.ai.AiController;
import edu.wotfk.combat.SiegeService;
/** Player finishes -> opponents -> siege -> economy -> victory -> next round. */
public final class TurnService {
    private final EconomyService economy = new EconomyService();
    public String endRound(CampaignState state) {
        if (state.finished()) return "Campaign already finished.";
        int firstNotice = state.journal().size();
        edu.wotfk.story.EventService stories = new edu.wotfk.story.EventService();
        for (HouseId id : HouseId.values()) stories.resolveAutomatically(state, id);
        AiController ai = new AiController();
        for (HouseId id : HouseId.values()) if (id != state.player() && state.alive(id)) {
            new edu.wotfk.diplomacy.DiplomacyService().answerAiRequest(state,id); ai.play(state, id);
        }
        new edu.wotfk.diplomacy.DiplomacyService().expireRequests(state);
        new edu.wotfk.intrigue.FeastService().resolveDue(state);
        new SiegeService().advance(state);
        EconomyLedger playerLedger = economy.forecast(state, state.player());
        for (HouseState house : state.houses()) if (state.alive(house.id())) economy.settle(state, house.id());
        state.log("The council closes round " + state.round() + ".");
        new VictoryService().evaluate(state);
        if (!state.finished()) { state.nextRound(); for (HouseState house : state.houses()) house.resetActions(); stories.offerAll(state); }
        String notices = String.join("\n", state.journal().subList(firstNotice, state.journal().size()));
        state.notifications().drain();
        return playerLedger.summary() + "\n\nWORLD RESPONSE\n" + notices
                + (state.pendingEvents().containsKey(state.player()) ? "\n\n" + stories.describe(state, state.player()) : "");
    }
}
