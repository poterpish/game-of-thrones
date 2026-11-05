package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
public final class WinterEventCreator extends EventCreator {
    @Override public String key() { return "winter"; }
    @Override protected boolean eligible(CampaignState state, HouseId house) { return state.round() >= 5; }
    @Override protected StoryEvent createEvent(CampaignState state, HouseId house) { return new WinterEvent(); }
}
