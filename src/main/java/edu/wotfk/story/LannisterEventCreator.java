package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
public final class LannisterEventCreator extends EventCreator {
    @Override public String key(){return "golden-debt";}
    @Override protected boolean eligible(CampaignState state,HouseId house){return house==HouseId.LANNISTER&&state.round()>=2&&state.round()+3<=state.maxRounds()&&state.consequences().stream().noneMatch(c->c.house()==house&&c.kind()==ScheduledConsequence.Kind.DEBT);}
    @Override protected StoryEvent createEvent(CampaignState state,HouseId house){return new GoldenDebtEvent();}
}
