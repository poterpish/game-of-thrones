package edu.wotfk.economy;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.story.*;
import static edu.wotfk.game.GameRules.*;
public final class LoanService {
    public String borrow(CampaignState state,HouseId actor){
        require(state.round()+3<=state.maxRounds(),"A loan must mature before the campaign deadline.");
        require(state.consequences().stream().noneMatch(c->c.house()==actor&&c.kind()==ScheduledConsequence.Kind.DEBT),"Repay the existing loan before borrowing again.");
        pay(state,actor,Cost.NONE,1);state.house(actor).resources().gain(12,0,0);
        new ConsequenceScheduler().schedule(state,actor,3,ScheduledConsequence.Kind.DEBT,15,"Repay 15 gold after income settlement; default loses reputation and influence.");
        return "Borrowed 12 gold. Repay 15 at the end of round "+(state.round()+3)+", after income. Use obligations to inspect the deadline.";
    }
}
