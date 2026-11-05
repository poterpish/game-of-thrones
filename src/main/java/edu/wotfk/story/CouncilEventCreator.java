package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
public final class CouncilEventCreator extends EventCreator {
    @Override public String key() { return "council"; }
    @Override protected boolean eligible(CampaignState state, HouseId house) { return state.round() >= 10; }
    @Override protected StoryEvent createEvent(CampaignState state, HouseId house) { return new CouncilWarningEvent(); }
}
