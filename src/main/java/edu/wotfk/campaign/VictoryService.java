package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import java.util.*;
/** Every campaign terminates: elimination, a held military crown, or the final council. */
public final class VictoryService {
    public int councilScore(CampaignState state, HouseId house) {
        if (!state.alive(house)) return -1;
        HouseState h = state.house(house);
        return 2 * state.map().ownedBy(house).size() + (state.map().territory("KL").owner() == house ? 3 : 0)
                + h.resources().influence() / 3 + h.reputation() / 20;
    }
    public void evaluate(CampaignState state) {
        if (state.finished()) return;
        for (HouseState house : state.houses()) if (!state.alive(house.id())) house.army().lose(house.army().units().size());
        if (!state.alive(state.player())) { state.finish(null, "DEFEAT: your house has lost its last territory. Its claim to the throne ends."); return; }
        Territory capital = state.map().territory("KL");
        if (capital.owner() != null && state.round() >= capital.ownerSinceRound() + 2 && state.map().ownedBy(capital.owner()).size() >= 6) {
            finish(state, capital.owner(), "military conquest"); return;
        }
        if (state.round() >= state.maxRounds()) {
            int best = -1; HouseId leader = null; boolean tie = false;
            StringBuilder scores = new StringBuilder("GREAT COUNCIL\n");
            for (HouseId id : HouseId.values()) {
                int score = councilScore(state, id); if (score < 0) continue;
                scores.append(id.displayName()).append(": ").append(score).append(" points\n");
                if (score > best) { best = score; leader = id; tie = false; } else if (score == best) tie = true;
            }
            state.log(scores.toString().stripTrailing());
            if (tie) state.finish(null, "DIVIDED KINGDOM: the council is tied. No claimant wins; independent realms survive.\n" + scores);
            else { finish(state, leader, "the Great Council"); state.log("Council scores are final."); }
        }
    }
    private void finish(CampaignState state, HouseId winner, String route) { state.finish(winner, new EndingNarrator().narrate(state, winner, route)); }
}
