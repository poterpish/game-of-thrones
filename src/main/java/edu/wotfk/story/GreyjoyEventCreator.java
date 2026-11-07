package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
public final class GreyjoyEventCreator extends EventCreator {
    @Override public String key(){return "iron-price";}
    @Override protected boolean eligible(CampaignState state,HouseId house){return house==HouseId.GREYJOY&&state.round()>=2&&state.map().ownedBy(house).stream().anyMatch(t->state.map().coastal(t.id()));}
    @Override protected StoryEvent createEvent(CampaignState state,HouseId house){return new IronPriceEvent();}
}
