package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
public record StoryContext(CampaignState state, HouseId actor) {
    public HouseState house() { return state.house(actor); }
    public void flag(String name) { state.storyFlags().add(name + ":" + actor); }
}
