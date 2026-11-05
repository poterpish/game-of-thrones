package edu.wotfk.combat;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
public final class CombatService {
    public String attack(CampaignState state, HouseId actor, String id, BattleStrategy strategy) {
        Siege siege = state.sieges().get(actor);
        int reduction = siege != null && siege.target().equalsIgnoreCase(id) && new SiegeService().valid(state, siege) ? siege.progress() : 0;
        BattleOutcome result = new BattleService().fight(state, actor, id, strategy, reduction);
        if (result.victory()) new CaptureService().capture(state, actor, id);
        else state.log(actor.displayName() + " is repelled at " + state.map().territory(id).name() + ".");
        return result.summary() + (result.victory() ? " Captured " + state.map().territory(id).name() + "." : " The defender retains the castle.");
    }
}
