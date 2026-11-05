package edu.wotfk.diplomacy;
import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import static edu.wotfk.game.GameRules.*;
/** Diplomacy operations for the campaign facade. */
public final class DiplomacyService {
    public String declareWar(CampaignState state, HouseId actor, HouseId target) {
        require(target != null && target != actor, "Choose another house.");
        require(state.alive(target), "Target house has no territory.");
        require(!state.diplomacy().atWar(actor, target), "Already at war with this house.");
        pay(state, actor, new Cost(0, 0, 2), 1);
        state.diplomacy().declareWar(actor, target);
        state.log(actor.displayName() + " declares war on " + target.displayName() + ".");
        return "War declared on " + target.displayName() + ".";
    }
    public String overview(CampaignState state, HouseId actor) {
        StringBuilder out = new StringBuilder("Relations for " + actor.displayName() + ":");
        for (HouseId other : HouseId.values()) {
            if (other == actor) continue;
            out.append("\n").append(other.displayName()).append(": ")
               .append(state.diplomacy().atWar(actor, other) ? "WAR" : state.diplomacy().allied(actor, other) ? "ALLY" : "NEUTRAL");
        }
        return out.toString();
    }
}
