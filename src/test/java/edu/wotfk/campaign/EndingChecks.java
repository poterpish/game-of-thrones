package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import edu.wotfk.ai.AiController;
import static edu.wotfk.testsupport.TestSupport.*;
public final class EndingChecks {
    public static void main(String[] args) {
        for (HouseId id : HouseId.values()) {
            CampaignState state = new CampaignDirector().standard(id, 18);
            int rounds = 0;
            while (!state.finished() && rounds++ < 12) new TurnService().endRound(state);
            check(state.finished(), id + " passive campaign ends within twelve rounds");
            for (HouseState house : state.houses()) check(house.resources().gold() >= 0 && house.resources().food() >= 0, "No negative balances after " + id + " campaign: " + house.id());
        }
        CampaignState eliminated = new CampaignDirector().standard(HouseId.STARK, 1);
        for (Territory t : eliminated.map().ownedBy(HouseId.STARK)) t.capture(HouseId.LANNISTER, 1);
        new VictoryService().evaluate(eliminated);
        check(eliminated.finished() && eliminated.ending().contains("last territory"), "Losing all territories ends the player's campaign");
        CampaignState military = new CampaignDirector().standard(HouseId.STARK, 1);
        for (String id : new String[]{"KL","TW","CR","LP"}) military.map().territory(id).capture(HouseId.STARK, 1);
        new VictoryService().evaluate(military); check(!military.finished(), "Capture round does not count as two full rounds");
        military.nextRound(); new VictoryService().evaluate(military); check(!military.finished(), "One elapsed round is insufficient");
        military.nextRound(); new VictoryService().evaluate(military); equal(HouseId.STARK, military.winner(), "Two elapsed rounds and six holdings win militarily");
        CampaignState a = new CampaignDirector().standard(HouseId.STARK, 88);
        CampaignState b = new CampaignDirector().standard(HouseId.STARK, 88);
        for (int i=0;i<4;i++) { new TurnService().endRound(a); new TurnService().endRound(b); }
        equal(a.map().atlas(), b.map().atlas(), "Identical input and seed reproduce AI conquests");
        equal(a.journal(), b.journal(), "Identical runs reproduce the full public chronicle");
    }
}
