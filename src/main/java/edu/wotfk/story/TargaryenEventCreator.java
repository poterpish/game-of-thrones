package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
public final class TargaryenEventCreator extends EventCreator {
    @Override public String key(){return "restoration";}
    @Override protected boolean eligible(CampaignState state,HouseId house){return house==HouseId.TARGARYEN&&state.round()>=2&&(state.triggers().happened("capture",house)||state.round()>=3);}
    @Override protected StoryEvent createEvent(CampaignState state,HouseId house){return new RestorationEvent();}
}
